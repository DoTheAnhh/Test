package com.example.test_project.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    // Filter xử lý JWT token, set Authentication vào SecurityContext
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Filter kiểm tra quyền truy cập API dựa vào user và permission
    private final PermissionFilter permissionFilter;

    /**
     * Bean để mã hóa mật khẩu. Sẽ được autowire vào service khi cần
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cấu hình bảo mật Spring Security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Tắt CSRF (thường cần cho API, không dùng form)
                .csrf(AbstractHttpConfigurer::disable)

                // Cấu hình phân quyền
                .authorizeHttpRequests(auth -> auth
                        // Cho phép mọi request tới /auth/** mà không cần login
                        .requestMatchers("/auth/**").permitAll()
                        // Các request khác cần phải xác thực (authenticated)
                        .anyRequest().authenticated()
                )

                // Thêm filter trước filter mặc định UsernamePasswordAuthenticationFilter
                // JwtAuthenticationFilter sẽ parse token, set Authentication
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Thêm PermissionFilter sau JwtAuthenticationFilter
                // Filter này sẽ dựa trên Authentication đã set để check quyền
                .addFilterAfter(permissionFilter, JwtAuthenticationFilter.class)

                .build();
    }
}




