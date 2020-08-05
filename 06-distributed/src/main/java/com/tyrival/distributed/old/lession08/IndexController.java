package com.tyrival.distributed.old.lession08;

import com.tyrival.distributed.old.lession05.RedisOpsController;
import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(RedisOpsController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Redisson redisson;

    /*
     业务场景：商品秒杀活动，扣减库存功能的实现
     */

    /**
     * 示例1
     * 这种情况下，容易出现超卖，因为多线程情况下，可能有多个线程同时取到同一个值，然后看起来库存-1，实际生成了多个订单。
     */
    @RequestMapping("/deduct_stock_1")
    public String deductStock1() {
        int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
        if (stock > 0) {
            int realStock = stock - 1;
            stringRedisTemplate.opsForValue().set("stock", realStock + "");
            logger.info("扣减成功，剩余库存：" + realStock);
        } else {
            logger.info("扣减失败，库存不足。");
        }
        return "end";
    }

    /**
     * 示例2
     * 通过synchronized来为IndexController加锁，当代码执行到synchronized时，执行串行操作，
     * 每次只执行一个线程的操作，一个线程执行完成后，才执行下一个线程。
     * 问题：
     * 这种写法只适合单机架构，在分布式架构的情况下，无法跨服务器对整个集群的该接口加锁。
     */
    @RequestMapping("/deduct_stock_2")
    public String deductStock2() {
        synchronized (this) {
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock + "");
                logger.info("扣减成功，剩余库存：" + realStock);
            } else {
                logger.info("扣减失败，库存不足。");
            }
            return "end";
        }
    }

    /**
     * 示例3
     * 通过redis的setnx进行加锁，利用redis的单线程机制，实现分布式架构中的锁。
     * - 为避免程序在中间环节抛异常，在finally中执行解锁操作；
     * - 为避免程序在中间环节挂掉，导致锁无法释放，给这把锁设置一个超时时间；
     * 问题：
     * 高并发场景下，可能出现锁失效的问题。
     * 例如，线程A执行时间超过了超时时间的10s，当到达第10s时，redis使锁失效，此时有另一个线程B抢到这把锁，
     * 而此时线程A还未执行完，假设再过了5s，线程A完成后，执行finally代码块，会对线程B的锁进行删除操作。
     * 同理线程B可能删除线程C的锁，于是在高并发场景下，这把锁就一直处于失效的状态下，并没有起到应有的作用。
     */
    @RequestMapping("/deduct_stock_3")
    public String deductStock3() {
        String lockKey = "product_001";
        try {
            Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "tyrival", 10, TimeUnit.SECONDS);
            if (!result) {
                return "error";
            }
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock + "");
                logger.info("扣减成功，剩余库存：" + realStock);
            } else {
                logger.info("扣减失败，库存不足。");
            }
        } finally {
            stringRedisTemplate.delete(lockKey);
        }
        return "end";
    }

    /**
     * 示例4
     * 增加了唯一的clientId，避免出现锁被其他线程删除的问题。
     * 问题：
     * 解决了锁被其他线程删除的问题，但仍旧没有解决当前线程执行时间超长，导致锁失效的问题。
     */
    @RequestMapping("/deduct_stock_4")
    public String deductStock4() {
        String lockKey = "product_001";
        String clientId = UUID.randomUUID().toString();
        try {
            Boolean result = stringRedisTemplate.opsForValue()
                    .setIfAbsent(lockKey, clientId, 30, TimeUnit.SECONDS);
            if (!result) {
                return "error";
            }
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock + "");
                logger.info("扣减成功，剩余库存：" + realStock);
            } else {
                logger.info("扣减失败，库存不足。");
            }
        } finally {
            if (clientId.equals(stringRedisTemplate.opsForValue().get(lockKey))) {
                stringRedisTemplate.delete(lockKey);
            }
        }
        return "end";
    }

    /**
     * Redisson
     */
    @RequestMapping("/deduct_stock")
    public String deductStock() throws InterruptedException {
        String lockKey = "product_001";
        RLock redissonLock = redisson.getLock(lockKey);
        try {
            // 加锁，并实现锁续命功能，默认30s超时时间，并且10s续命一次
            redissonLock.lock();
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock + "");
                System.out.println("扣减成功，剩余库存:" + realStock + "");
            } else {
                System.out.println("扣减失败，库存不足");
            }
        } finally {
            redissonLock.unlock();
        }
        return "end";
    }

    @RequestMapping("/redlock")
    public String redlock() throws InterruptedException {

        String lockKey = "product_001";

        // 这里需要自己实例化不同redis实例的redisson客户端连接，这里只是伪代码用一个redisson客户端简化了
        RLock lock1 = redisson.getLock(lockKey);
        RLock lock2 = redisson.getLock(lockKey);
        RLock lock3 = redisson.getLock(lockKey);

        // 根据多个 RLock 对象构建 RedissonRedLock （最核心的差别就在这里）
        RedissonRedLock redLock = new RedissonRedLock(lock1, lock2, lock3);
        try {
            /**
             * 尝试获取锁
             * waitTimeout 尝试获取锁的最大等待时间，超过这个值，则认为获取锁失败
             * leaseTime   锁的持有时间,超过这个时间锁会自动失效（值应设置为大于业务处理的时间，确保在锁有效期内业务能处理完）
             */
            boolean res = redLock.tryLock(10, 30, TimeUnit.SECONDS);
            if (res) {
                // 成功获得锁，在这里处理业务
            }
        } catch (Exception e) {
            throw new RuntimeException("lock fail");
        } finally {
            // 最后解锁
            redLock.unlock();
        }
        return "end";
    }

}