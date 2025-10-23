package com.example.test_project.controller;

import com.example.test_project.common.SearchParams;
import com.example.test_project.dto.user.request.UserRequest;
import com.example.test_project.dto.user.response.UserResponse;
import com.example.test_project.service.IUserService;
import com.example.test_project.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final IUserService userService;

    @GetMapping("list")
    public ResponseEntity<List<UserResponse>> listUser(@RequestBody SearchParams params) {
        List<UserResponse> users = userService.listUser(params);
        return ResponseEntity.ok(users);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long id) {
        UserResponse user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("create")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("update/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UserRequest request,
                                                                @PathVariable Long userId) {
        return ResponseEntity.ok(userService.updateUser(request, userId));
    }
}
