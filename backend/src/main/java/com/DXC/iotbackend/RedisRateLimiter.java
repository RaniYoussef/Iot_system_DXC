package com.DXC.iotbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisRateLimiter {

    private static final int MAX_REQUESTS = 20;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean isAllowed(String ipAddress) {
        String key = "rate_limit:" + ipAddress;
        Long current = redisTemplate.opsForValue().increment(key);

        if (current == 1) {
            // Set expiration only when the key is newly created
            redisTemplate.expire(key, WINDOW);
        }

        return current <= MAX_REQUESTS;
    }
}

