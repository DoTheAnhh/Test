package com.example.test_project.security;

import com.example.test_project.entity.Permission;
import com.example.test_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    // Repository dùng để lấy dữ liệu user và permission từ DB
    private final UserRepository userRepository;

    /**
     * Lấy tất cả permission của user dựa theo username
     * Format trả về: "HTTP_METHOD:/api/path", ví dụ "GET:/user/list"
     */
    @Transactional(readOnly = true)
    public Set<String> getUserPermissions(String username) {
        // Gọi query trong repository để lấy danh sách Permission của user
        List<Permission> permissions = userRepository.findUserPermissions(username);

        // Chuyển danh sách Permission thành Set<String> dạng "METHOD:/apiPath"
        return permissions.stream()
                .map(p -> p.getHttpMethod().toUpperCase() + ":" + p.getApiPath())
                .collect(Collectors.toSet());
    }

    /**
     * Kiểm tra user có quyền truy cập API hay không
     * @param username tên user đang request
     * @param httpMethod GET, POST, PUT, DELETE
     * @param apiPath đường dẫn API user đang gọi (ví dụ /user/list)
     * @return true nếu user có quyền, false nếu không
     */
    public boolean hasPermission(String username, String httpMethod, String apiPath) {
        // Lấy toàn bộ permission của user
        Set<String> userPermissions = getUserPermissions(username);

        // Ghép method + path thành dạng giống DB để so sánh
        String currentApi = httpMethod.toUpperCase() + ":" + apiPath;

        // Kiểm tra xem có permission nào khớp
        return userPermissions.stream()
                .anyMatch(permission -> {
                    // 1. Kiểm tra exact match trước
                    if (permission.equals(currentApi)) {
                        return true;
                    }
                    // 2. Kiểm tra prefix match với wildcard (*) ở cuối (ví dụ /user* khớp /user/list)
                    if (permission.endsWith("*")) {
                        String prefix = permission.substring(0, permission.length() - 1);
                        return currentApi.startsWith(prefix);
                    }
                    return false;
                });
    }
}

