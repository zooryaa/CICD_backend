package com.example.demo.core.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redis.clients.jedis.JedisPool;

/**
 * The reason why this class is needed is, we can't autowire JedisPool (Class type collision).
 */
@Getter
@AllArgsConstructor
public class JedisPoolWrapper {
    private final JedisPool jedisPool;
}
