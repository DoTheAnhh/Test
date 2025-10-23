package com.example.test_project.service.impl;

import com.example.test_project.common.SearchParams;
import com.example.test_project.dto.user.request.UserRequest;
import com.example.test_project.dto.user.response.UserResponse;
import com.example.test_project.dto.user_role.request.UserRoleRequest;
import com.example.test_project.dto.user_role.response.UserRoleResponse;
import com.example.test_project.entity.Role;
import com.example.test_project.entity.User;
import com.example.test_project.common.ApiResponse;
import com.example.test_project.entity.UserRole;
import com.example.test_project.repository.RoleRepository;
import com.example.test_project.util.DynamicFilter;
import com.example.test_project.util.SpecificationBuilder;
import com.example.test_project.repository.UserRepository;
import com.example.test_project.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SpecificationBuilder specificationBuilder;
    private final DynamicFilter dynamicFilter;

    @Override
    public List<UserResponse> listUser(SearchParams params) {
        Map<String, Object> filters = dynamicFilter.toFilterMap(params);
        Specification<User> spec = specificationBuilder.build(filters);
        List<User> users = userRepository.findAll(spec);

        return users.stream().map(user -> {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setCode(user.getCode());
            response.setName(user.getName());

            if (user.getUserRoles() != null) {
                List<UserRoleResponse> userRoles = user.getUserRoles().stream().map(ur -> {
                    UserRoleResponse urRes = new UserRoleResponse();
                    urRes.setId(ur.getRole().getId());
                    urRes.setRoleCode(ur.getRole().getCode());
                    urRes.setRoleName(ur.getRole().getName());
                    urRes.setStatus(ur.isStatus() ? "Hoạt động" : "Không hoạt động");
                    return urRes;
                }).toList();
                response.setUserRoles(userRoles);
            }

            return response;
        }).toList();
    }

    @Override
    public UserResponse findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy với id: " + id));

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setCode(user.getCode());
        response.setName(user.getName());

        if (user.getUserRoles() != null) {
            List<UserRoleResponse> userRoles = user.getUserRoles().stream().map(ur -> {
                UserRoleResponse urRes = new UserRoleResponse();
                urRes.setId(ur.getRole().getId());
                urRes.setRoleCode(ur.getRole().getCode());
                urRes.setRoleName(ur.getRole().getName());
                urRes.setStatus(ur.isStatus() ? "Hoạt động" : "Không hoạt động");
                return urRes;
            }).toList();
            response.setUserRoles(userRoles);
        }

        return response;
    }

    @Override
    public ApiResponse<UserResponse> createUser(UserRequest request) {
        validateUserRequest(request);

        User user = new User();
        user.setCode(request.getCode());
        user.setName(request.getName());
        setUserRoles(user, request.getUserRoles());

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setCode(savedUser.getCode());
        response.setName(savedUser.getName());
        response.setUserRoles(savedUser.getUserRoles().stream().map(ur -> {
            UserRoleResponse urRes = new UserRoleResponse();
            urRes.setId(ur.getRole().getId());
            urRes.setRoleCode(ur.getRole().getCode());
            urRes.setRoleName(ur.getRole().getName());
            urRes.setStatus(ur.isStatus() ? "Hoạt động" : "Không hoạt động");
            return urRes;
        }).toList());

        return ApiResponse.success("Tạo thành công", response);
    }

    @Override
    public ApiResponse<UserResponse> updateUser(UserRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        validateUserRequest(request);

        user.setCode(request.getCode());
        user.setName(request.getName());
        setUserRoles(user, request.getUserRoles());

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setCode(savedUser.getCode());
        response.setName(savedUser.getName());
        response.setUserRoles(savedUser.getUserRoles().stream().map(ur -> {
            UserRoleResponse urRes = new UserRoleResponse();
            urRes.setId(ur.getRole().getId());
            urRes.setRoleCode(ur.getRole().getCode());
            urRes.setRoleName(ur.getRole().getName());
            urRes.setStatus(ur.isStatus() ? "Hoạt động" : "Không hoạt động");
            return urRes;
        }).toList());

        return ApiResponse.success("Cập nhật thành công", response);    }

    private void setUserRoles(User user, List<UserRoleRequest> roleRequests) {
        if (roleRequests == null) return;

        List<UserRole> roles = roleRequests.stream().map(urReq -> {
            UserRole ur = new UserRole();
            ur.setUser(user);

            Role role = roleRepository.findById(urReq.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role không tồn tại: " + urReq.getRoleId()));
            ur.setRole(role);

            ur.setStatus(urReq.isStatus());

            return ur;
        }).toList();
        user.setUserRoles(roles);
    }

    private void validateUserRequest(UserRequest request) {

        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new RuntimeException("Không được để trống");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Không được để trống");
        }

        boolean codeExists = userRepository.existsByCode((request.getCode()));
        if (codeExists) {
            throw new RuntimeException("Mã đã tồn tại");
        }

        boolean nameExists = userRepository.existsByName((request.getName()));
        if (nameExists) {
            throw new RuntimeException("Tên đã tồn tại");
        }

        if (request.getUserRoles() == null || request.getUserRoles().isEmpty()) {
            throw new RuntimeException("Không được để trống");
        }

        request.getUserRoles().forEach(ur -> {
            if (ur.getRoleId() == null) {
                throw new RuntimeException("Không được để trống");
            }
        });
    }
}
