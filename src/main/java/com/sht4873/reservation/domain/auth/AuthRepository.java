package com.sht4873.reservation.domain.auth;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
public class AuthRepository {
    private static final String KEY_PREFIX = "RESERVATION:AUTH:";
    private static final String ADMIN_PREFIX = "ADMIN:AUTH:";
    private static final Duration TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;

    public AuthRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String issueToken(String name, String encryptedPhone) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(KEY_PREFIX + token, String.format("%s:%s", name, encryptedPhone), TTL);
        return token;
    }

    public boolean existsByToken(String token) {
        return redisTemplate.hasKey(KEY_PREFIX + token);
    }

    public String issueAdminToken(String name, String encryptedPhone) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(ADMIN_PREFIX + token, String.format("%s:%s", name, encryptedPhone), TTL);
        return token;
    }

    public boolean existsAdminByToken(String token) {
        return redisTemplate.hasKey(ADMIN_PREFIX + token);
    }

}
