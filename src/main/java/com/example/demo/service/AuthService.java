package com.example.demo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final StringRedisTemplate redisTemplate;
    // Tiền tố để phân biệt dữ liệu trong Redis cache
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    public AuthService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Đút token vào danh sách đen của Redis khi người dùng ấn Logout
     * Mặc định đặt TTL sống là 1 tiếng (3600 giây) cho giả lập DATN
     */
    public void logout(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token không hợp lệ để thực hiện đăng xuất!");
        }

        String key = BLACKLIST_PREFIX + token;
        // Gán giá trị rác vào key, set thời gian tự động xóa (TTL) là 1 tiếng
        redisTemplate.opsForValue().set(key, "logout_session", 60, TimeUnit.MINUTES);
    }

    /**
     * Hàm kiểm tra xem Token gửi lên có nằm trong danh sách đen không
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) return true;
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}