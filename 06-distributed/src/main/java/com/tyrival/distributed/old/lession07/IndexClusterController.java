package com.tyrival.distributed.old.lession07;

import com.tyrival.distributed.old.lession05.RedisOpsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexClusterController {

    private static final Logger logger = LoggerFactory.getLogger(RedisOpsController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/test_cluster")
    public void testCluster() throws InterruptedException {
        stringRedisTemplate.opsForValue().set("tyrival", "666");
        logger.info(stringRedisTemplate.opsForValue().get("tyrival"));

        stringRedisTemplate.opsForValue().set("tom", "666");
        logger.info(stringRedisTemplate.opsForValue().get("tom"));
    }
}