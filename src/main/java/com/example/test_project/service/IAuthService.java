package com.example.test_project.service;

import com.example.test_project.common.ApiResponse;
import com.example.test_project.dto.auth.request.LoginRequest;
import com.example.test_project.dto.auth.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<ApiResponse<?>> login(LoginRequest request);
    ResponseEntity<ApiResponse<?>> register(RegisterRequest request);
}
