package com.example.test_project.service;

import com.example.test_project.common.SearchParams;
import com.example.test_project.dto.user.request.UserRequest;
import com.example.test_project.dto.user.response.UserResponse;
import com.example.test_project.common.ApiResponse;

import java.util.List;

public interface IUserService {
    List<UserResponse> listUser(SearchParams params);
    UserResponse findUserById(Long id);
    ApiResponse<UserResponse> createUser(UserRequest userRequest);
    ApiResponse<UserResponse> updateUser(UserRequest request, Long userId);
}
