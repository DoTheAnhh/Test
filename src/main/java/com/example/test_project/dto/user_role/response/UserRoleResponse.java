package com.example.test_project.dto.user_role.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleResponse {
    private Long id;
    private String roleCode;
    private String roleName;
}
