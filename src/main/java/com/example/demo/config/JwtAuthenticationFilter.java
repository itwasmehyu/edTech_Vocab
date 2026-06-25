package com.example.demo.config;

import com.example.demo.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AuthService authService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, AuthService authService) {
        this.jwtUtils = jwtUtils;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Bỏ qua bộ lọc kiểm tra đối với các API công khai (Đăng ký, Đăng nhập)
        if (path.contains("/api/v1/auth/login") || path.contains("/api/v1/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();

            // 1. KIỂM TRA REDIS BLACKLIST: Nếu token đã nằm trong Redis -> Từ chối truy cập ngay lập tức
            if (authService.isTokenBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token nay da het han hoac phien dang nhap da bi huy bo (Logged out)!");
                return;
            }

            // 2. Kiểm tra tính hợp lệ về mặt chữ ký mã hóa của chuỗi JWT
            if (!jwtUtils.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token khong hop le hoac da het han!");
                return;
            }
        } else {
            // Nếu các API bảo mật (như /widget/daily, /srs/review) gửi lên mà không đính kèm Token -> Chặn luôn
            if (path.contains("/api/v1/widget") || path.contains("/api/v1/srs")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Ban can dinh kem JWT Token hop le vao Header de truy cap API nay!");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}