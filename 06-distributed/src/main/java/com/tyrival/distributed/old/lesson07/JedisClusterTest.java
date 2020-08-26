package com.tyrival.distributed.old.lesson07;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JedisClusterTest {
    public static void main(String[] args) throws IOException {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        config.setMinIdle(5);

        Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
        jedisClusterNode.add(new HostAndPort("10.211.55.9", 8001));
        jedisClusterNode.add(new HostAndPort("10.211.55.10", 8002));
        jedisClusterNode.add(new HostAndPort("10.211.55.11", 8003));
        jedisClusterNode.add(new HostAndPort("10.211.55.9", 8004));
        jedisClusterNode.add(new HostAndPort("10.211.55.10", 8005));
        jedisClusterNode.add(new HostAndPort("10.211.55.11", 8006));

        JedisCluster jedisCluster = null;
        try {
            // connectionTimeout：指的是连接一个url的连接等待时间
            // soTimeout：指的是连接上一个url，获取response的返回等待时间
            jedisCluster =
                    new JedisCluster(jedisClusterNode, 6000, 5000, 10, "tyrival", config);
            System.out.println(jedisCluster.set("cluster", "tyrival"));
            System.out.println(jedisCluster.get("cluster"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedisCluster != null)
                jedisCluster.close();
        }
    }
}