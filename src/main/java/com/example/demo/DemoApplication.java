package com.example.demo;

import com.example.demo.core.redis.JedisPoolWrapper;
import com.example.demo.domain.recommender.Gorse;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisPool;

@SpringBootApplication
@Log4j2
public class DemoApplication {
    @Bean
    Gorse getGorseClient() {
        log.debug("Creating connection to gorse on: http://localhost:8088");
        return new Gorse("http://localhost:8088", "Depenendcy");
    }

    @Bean
    JedisPoolWrapper getRedisConnection() {
        return new JedisPoolWrapper(new JedisPool("localhost", 6379));
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}