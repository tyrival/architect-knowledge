package com.tyrival.distributed.old.lession05;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class RedisOpsController {
    private static final Logger logger = LoggerFactory.getLogger(RedisOpsController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 对象缓存的性能测试
     * 1. 以json形式存储
     * 2. 以属性形式拆分存储
     */
    @RequestMapping("/test_object_cache")
    public void testObjectCache() {

        logger.info("===================");
        Long avgCost = 0L;

        // 初次连接redis比较慢，将此次操作排除出测试数据
        stringRedisTemplate.opsForValue().set("begin", "begin");

        for (int i = 0; i < 100; i++) {
            Long start = System.currentTimeMillis() * 1000;
            User user = new User(i, "tom" + i, i);
            stringRedisTemplate.opsForValue().set("user:" + i, JSON.toJSONString(user));
            Long end = System.currentTimeMillis() * 1000;
            avgCost += end - start;
        }
        logger.info("Json set average cost: " + avgCost / 100);

        avgCost = 0L;
        for (int i = 0; i < 100; i++) {
            Long start = System.currentTimeMillis() * 1000;
            User user = JSON.parseObject(stringRedisTemplate.opsForValue().get("user:" + i), User.class);
            Long end = System.currentTimeMillis() * 1000;
            avgCost += end - start;
        }
        logger.info("Json get average cost: " + avgCost / 100);
        logger.info("===================");

        for (int i = 0; i < 100; i++) {
            Long start = System.currentTimeMillis() * 1000;
            User user = new User(i, "tom" + i, i);
            Map<String, String> map = new HashMap<>();
            map.put("user:" + user.getId() + ":name", user.getName());
            map.put("user:" + user.getId() + ":balance", String.valueOf(user.getBalance()));
            stringRedisTemplate.opsForValue().multiSet(map);
            Long end = System.currentTimeMillis() * 1000;
            avgCost += end - start;
        }
        logger.info("Property set average cost: " + avgCost / 100);

        avgCost = 0L;
        for (int i = 0; i < 100; i++) {
            Long start = System.currentTimeMillis() * 1000;
            List<String> keys = new ArrayList<>();
            keys.add("user:" + i + ":name");
            keys.add("user:" + i + ":balance");
            List list = stringRedisTemplate.opsForValue().multiGet(keys);
            Long end = System.currentTimeMillis() * 1000;
            avgCost += end - start;
        }
        logger.info("Property get average cost: " + avgCost / 100);

        for (int i = 0; i < 100; i++) {
            Long start = System.currentTimeMillis() * 1000;
            User user = new User(i, "tom" + i, i);
            stringRedisTemplate.opsForValue().set("user:" + user.getId() + ":balance",
                    String.valueOf(user.getBalance() * 10));
            Long end = System.currentTimeMillis() * 1000;
            avgCost += end - start;
        }
        logger.info("One property modify average cost: " + avgCost / 100);
        logger.info("===================");
    }
}
