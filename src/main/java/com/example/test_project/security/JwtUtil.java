package com.example.test_project.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Lấy secret key từ application.properties
    // Đây là key dùng để ký JWT (HS256)
    @Value("${jwt.secret}")
    private String secret;

    // Thời gian sống của token (ms), lấy từ application.properties
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Chuyển secret thành key dùng cho ký JWT
     * Keys.hmacShaKeyFor() yêu cầu byte array
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Tạo JWT token từ username
     * - setSubject(username): đặt tên user vào token
     * - setIssuedAt: thời gian tạo token
     * - setExpiration: token hết hạn
     * - signWith: ký token với key và thuật toán HS256
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Lấy username từ token
     * Nếu token hợp lệ, trả về nội dung của claim "subject" (username)
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // set key để xác thực chữ ký
                .build()
                .parseClaimsJws(token)          // parse token
                .getBody()
                .getSubject();                  // lấy subject (username)
    }

    /**
     * Kiểm tra token có hợp lệ không
     * - Hợp lệ nghĩa là: đúng chữ ký, chưa hết hạn, không bị sửa đổi
     * - Nếu parse không lỗi => true, nếu lỗi (JwtException) => false
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token); // parse token
            return true; // parse thành công => token hợp lệ
        } catch (JwtException e) {
            return false; // parse lỗi => token không hợp lệ
        }
    }
}
