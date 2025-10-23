package com.example.test_project.security;

import com.example.test_project.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;           // Utility để tạo và xác thực JWT
    private final UserRepository userRepository; // Repository để lấy thông tin User từ DB nếu cần

    /**
     * Hàm chính của filter, chạy mỗi request một lần.
     * Mục đích: kiểm tra xem request có header Authorization hợp lệ không,
     * nếu có thì đặt Authentication vào SecurityContext để Spring Security biết người dùng đã đăng nhập.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Lấy header Authorization
        String authHeader = request.getHeader("Authorization");

        // Nếu không có header hoặc không bắt đầu bằng "Bearer ", bỏ qua filter này
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // tiếp tục chuỗi filter
            return;
        }

        // Lấy token thực sự, bỏ phần "Bearer "
        String token = authHeader.substring(7);

        // Kiểm tra token có hợp lệ không (chữ ký và expiration)
        if (!jwtUtil.isTokenValid(token)) {
            filterChain.doFilter(request, response); // token không hợp lệ, tiếp tục chuỗi filter nhưng không set Authentication
            return;
        }

        // Lấy username từ token
        String username = jwtUtil.extractUsername(token);
        if (username == null) {
            filterChain.doFilter(request, response); // nếu token không có username, tiếp tục filter chain
            return;
        }

        /**
         * Tạo Authentication object để Spring Security biết user đã xác thực
         * - Principal: username
         * - Credentials: null vì không cần password nữa
         * - Authorities: new ArrayList<>() tạm thời chưa dùng quyền, sẽ kiểm tra sau bằng PermissionFilter
         */
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

        // Set authentication vào context của Spring Security
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tiếp tục chuỗi filter
        filterChain.doFilter(request, response);
    }
}
