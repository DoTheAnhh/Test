package com.example.test_project.dto.user.request;

import com.example.test_project.dto.user_role.request.UserRoleRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRequest {

    private String code;

    private String name;

    private List<UserRoleRequest> userRoles;
}
