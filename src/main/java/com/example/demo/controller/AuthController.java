package com.example.demo.controller;

import com.example.demo.config.JwtUtils;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, AuthService authService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User registeredUser = userService.register(request);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
            User user = userService.login(request);

            String jwtToken = jwtUtils.generateToken(user.getEmail());

            return ResponseEntity.ok(new LoginResponse(jwtToken, user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            // Cắt bỏ chữ "Bearer " nếu Client gửi dạng chuẩn
            String token = authHeader.replace("Bearer ", "").trim();
            authService.logout(token);
            return ResponseEntity.ok("Đăng xuất thành công! Session/Token đã được hủy bỏ.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi đăng xuất: " + e.getMessage());
        }
    }

}