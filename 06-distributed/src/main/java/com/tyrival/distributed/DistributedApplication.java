package com.tyrival.distributed;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DistributedApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedApplication.class, args);
    }

    @Bean
    public Redisson redisson() {
        // 单机模式
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379").setDatabase(0).setPassword("123");
//        config.useClusterServers()
//                .addNodeAddress("redis://10.211.55.9:8001")
//                .addNodeAddress("redis://10.211.55.10:8002")
//                .addNodeAddress("redis://10.211.55.11:8003")
//                .addNodeAddress("redis://10.211.55.9:8004")
//                .addNodeAddress("redis://10.211.55.10:8005")
//                .addNodeAddress("redis://10.211.55.11:8006")
//                .setPassword("tyrival");
        return (Redisson) Redisson.create(config);
    }

}
