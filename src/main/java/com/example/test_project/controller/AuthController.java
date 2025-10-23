package com.example.test_project.controller;

import com.example.test_project.common.ApiResponse;
import com.example.test_project.dto.auth.request.LoginRequest;
import com.example.test_project.entity.User;
import com.example.test_project.repository.UserRepository;
import com.example.test_project.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByCode(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Sai tài khoản"));

        String token = jwtUtil.generateToken(user.getCode());
        return ApiResponse.success("Đăng nhập thành công", Map.of("token", token));
    }
}
