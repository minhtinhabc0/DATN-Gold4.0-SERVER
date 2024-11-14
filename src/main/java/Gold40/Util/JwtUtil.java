package Gold40.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    // Sử dụng SecretKey để tạo chữ ký bảo mật
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Phương thức tạo token JWT
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username) // Đặt tên người dùng làm chủ thể của token
                .claim("role", role) // Thêm quyền của người dùng vào claims
                .setIssuedAt(new Date(System.currentTimeMillis())) // Đặt thời gian phát hành
                .setExpiration(Date.from(Instant.now().plus(10, ChronoUnit.HOURS))) // Hạn token trong 10 giờ
                .signWith(secretKey) // Ký token bằng secretKey
                .compact();
    }

    // Kiểm tra token có hợp lệ và chưa hết hạn
    public Boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);  // Lấy username từ token
            return extractedUsername != null && !isTokenExpired(token) && extractedUsername.equals(username);
        } catch (Exception e) {
            return false; // Nếu có lỗi, token không hợp lệ
        }
    }

    // Kiểm tra xem token đã hết hạn chưa
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Lấy thời gian hết hạn của token
    private Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    // Giải mã token và lấy username
    public String extractUsername(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    // Lấy quyền role từ claims của token
    public String extractRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    // Kiểm tra xem token có quyền ADMIN hay không
    public Boolean hasAdminRole(String token) {
        try {
            String role = extractRole(token); // Lấy role từ token
            return "ADMIN".equals(role); // Kiểm tra quyền có phải là ADMIN hay không
        } catch (Exception e) {
            return false;
        }
    }

    // Giải mã token và lấy tất cả claims
    public Claims extractAllClaims(String token) {
        return parseClaims(token);
    }

    // Phương thức chung để parse claims từ token
    private Claims parseClaims(String token) {
        try {
            // Kiểm tra nếu token không hợp lệ hoặc không có dấu chấm
            if (token == null || token.trim().isEmpty() || token.split("\\.").length != 3) {
                throw new JwtException("JWT không hợp lệ.");
            }

            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
}
