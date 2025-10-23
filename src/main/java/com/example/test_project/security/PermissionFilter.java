package com.example.test_project.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionFilter extends OncePerRequestFilter {

    // Service dùng để lấy danh sách permission của user và check quyền
    private final PermissionService permissionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Lấy thông tin Authentication từ SecurityContext (đã được JwtAuthenticationFilter set)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Nếu user chưa đăng nhập hoặc authentication null, bỏ qua filter
        // Chỉ pass request tới filter tiếp theo
        if (auth == null || !auth.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy username từ authentication (trong JwtAuthenticationFilter mình set username làm principal)
        String username = auth.getName();

        // Lấy URI hiện tại của request
        String uri = request.getRequestURI();

        // Loại bỏ context path nếu có
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty()) {
            uri = uri.substring(contextPath.length());
        }

        // Kiểm tra user có quyền gọi API này hay không
        boolean hasPermission = permissionService.hasPermission(username, request.getMethod(), uri);

        // Nếu không có quyền, trả về 403 FORBIDDEN và message
        if (!hasPermission) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                {
                  "success": false,
                  "message": "Bạn không có quyền truy cập"
                }
            """);
            return; // kết thúc request ở đây
        }

        // Nếu có quyền, tiếp tục request tới filter tiếp theo hoặc controller
        filterChain.doFilter(request, response);
    }
}

