package services;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestRedis {
    public String get(){
        // Configure JedisPool
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(poolConfig, "redis-10284.c11.us-east-1-2.ec2.redns.redis-cloud.com", 10284, 180000, "default","xdcf7d2Rk3xDaJlS52AJkOg4Mr7ivlxl");
        try (Jedis jedis = jedisPool.getResource()) {
            // Perform Redis operations here
            jedis.set("myKey", "Hello, JedisPool!");
            return "Value for key 'myKey': " + jedis.get("myKey");
        } finally {
            jedisPool.close(); // Close the pool when done
        }
    }
}
