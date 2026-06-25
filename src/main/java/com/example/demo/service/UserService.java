package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Logic Đăng ký tài khoản mới
     */
    public User register(RegisterRequest request) {
        // 1. Kiểm tra xem email đã tồn tại dưới DB chưa
        if (userRepository.findByEmail(request.getEmail().trim().toLowerCase()).isPresent()) {
            throw new RuntimeException("Email này đã được đăng ký sử dụng!");
        }

        // 2. Tiến hành mã hóa băm mật khẩu trước khi lưu
        String hashedPw = passwordEncoder.encode(request.getPassword());

        // 3. Tạo Object User và lưu xuống DB
        User newUser = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(hashedPw)
                .accountType("FREE")
                .build();

        return userRepository.save(newUser);
    }

    /**
     * Logic Đăng nhập hệ thống
     */
    public User login(LoginRequest request) {
        // 1. Tìm user theo email
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Tài khoản email hoặc mật khẩu không chính xác!"));

        // 2. Đối chiếu mật khẩu nhập vào với chuỗi băm dưới DB
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Tài khoản email hoặc mật khẩu không chính xác!");
        }

        // Đăng nhập thành công, trả về thông tin User (Tạm thời chưa dùng JWT token cho đơn giản)
        return user;
    }
}