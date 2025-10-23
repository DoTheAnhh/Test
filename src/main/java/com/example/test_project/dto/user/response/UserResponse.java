package com.example.test_project.dto.user.response;

import com.example.test_project.dto.user_role.response.UserRoleResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;

    private String code;

    private String name;

    private List<UserRoleResponse> userRoles;
}
