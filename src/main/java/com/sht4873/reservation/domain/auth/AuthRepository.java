package com.sht4873.reservation.domain.auth;

import com.sht4873.reservation.domain.auth.dto.request.AuthRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
public class AuthRepository {
    private static final String KEY_PREFIX = "RESERVATION:AUTH:";
    private static final Duration TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;

    public AuthRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String issueToken(AuthRequest request) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(KEY_PREFIX + token, String.format("%s:%s", request.getName(), request.getPhoneNum()), TTL);
        return token;
    }

    public boolean existsByToken(String token) {
        return redisTemplate.hasKey(KEY_PREFIX + token);
    }

    public String getPhoneNumByToken(String token) {
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + token);
        if (value == null) return null;
        // value 형식: "{name}:{phoneNum}"
        String[] parts = value.split(":");
        return parts.length == 2 ? parts[1] : null;
    }

}
