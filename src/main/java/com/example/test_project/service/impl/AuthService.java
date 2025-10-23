package com.example.test_project.service.impl;

import com.example.test_project.common.ApiResponse;
import com.example.test_project.dto.auth.request.LoginRequest;
import com.example.test_project.dto.auth.request.RegisterRequest;
import com.example.test_project.entity.User;
import com.example.test_project.repository.UserRepository;
import com.example.test_project.security.JwtUtil;
import com.example.test_project.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    // Repository thao tác với bảng User
    private final UserRepository userRepository;

    // Bean PasswordEncoder (BCrypt) để mã hóa và kiểm tra mật khẩu
    private final PasswordEncoder passwordEncoder;

    // Bean JwtUtil để tạo và kiểm tra JWT token
    private final JwtUtil jwtUtil;

    /**
     * Đăng ký tài khoản mới
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse<?>> register(RegisterRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // trả 400 nếu đã tồn tại
                    .body(ApiResponse.error("Tài khoản đã tồn tại"));
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // mã hóa mật khẩu
        userRepository.save(user);

        // Tạo JWT token
        String token = jwtUtil.generateToken(user.getUsername());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());

        // Tự động login sau khi đăng ký
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về 200 + token
        return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công", data));
    }

    /**
     * Đăng nhập
     */
    @Override
    public ResponseEntity<ApiResponse<?>> login(LoginRequest request) {
        // Lấy user từ DB theo username
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        // Nếu user không tồn tại hoặc mật khẩu sai, trả 401
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401: sai username/mật khẩu
                    .body(ApiResponse.error("Sai tài khoản hoặc mật khẩu"));
        }

        // Tạo JWT token hợp lệ
        String token = jwtUtil.generateToken(user.getUsername());

        // Set Authentication vào SecurityContext để các filter sau nhận biết user đã login
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());

        // Trả về 200 + token
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", data));
    }
}

