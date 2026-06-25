package com.example.demo.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Tạo một khóa bí mật an toàn với độ dài chuẩn mã hóa thuật toán HS256
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Đặt thời gian sống của Token là 24 tiếng (Tính bằng mili-giây)
    private final long jwtExpirationMs = 86400000;

    /**
     * Sinh JWT Token thật dựa trên thông tin Email của User
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Trích xuất Email từ chuỗi JWT Token nhận được
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Xác thực tính toàn vẹn và hạn dùng của Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}