package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả các endpoint API
                .allowedOrigins("*") // Cho phép tất cả các nguồn gửi request tới (Phù hợp cho môi trường DEV/DATN)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Cho phép đầy đủ các phương thức
                .allowedHeaders("*") // Cho phép truyền mọi loại Header (Authorization, Content-Type...)
                .maxAge(3600); // Lưu cấu hình cache CORS trong 1 tiếng để tăng tốc độ phản hồi
    }
}