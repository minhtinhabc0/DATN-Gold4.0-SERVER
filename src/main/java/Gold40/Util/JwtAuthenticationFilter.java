package Gold40.Util;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ROLE_PREFIX = "ROLE_";  // Define the role prefix for better maintainability
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String token = null;

        // Kiểm tra Authorization Header có chứa Bearer token không
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);  // Lấy token từ header
            username = jwtUtil.extractUsername(token); // Trích xuất username từ token
        }

        // Nếu token hợp lệ và chưa có Authentication trong SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Validate the token
                if (!jwtUtil.validateToken(token, username)) {
                    logger.warn("Token is invalid or expired");
                    chain.doFilter(request, response);
                    return;
                }

                Claims claims = jwtUtil.extractAllClaims(token);
                String role = claims.get("role", String.class); // Trích xuất role (vaitro) từ token

                if (role == null) {
                    logger.warn("Role missing in the token for user: " + username);
                    chain.doFilter(request, response);
                    return;
                }

                // Tạo đối tượng Authentication với vai trò (role)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singleton(() -> ROLE_PREFIX + role)); // Chuyển vaitro thành ROLE_

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Đặt thông tin chi tiết

                // Gán vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                logger.error("Error processing JWT authentication", e);
            }
        }
        // Tiếp tục chuỗi bộ lọc
        chain.doFilter(request, response);
    }
}
