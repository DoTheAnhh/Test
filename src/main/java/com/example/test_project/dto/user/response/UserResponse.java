package com.example.test_project.dto.user.response;

import com.example.test_project.dto.user_role.response.UserRoleResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    public Long id;

    public String code;

    public String name;

    public List<UserRoleResponse> userRoles;
}
