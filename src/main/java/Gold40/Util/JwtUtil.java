package Gold40.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Sử dụng SecretKey thay cho chuỗi String để ký token
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Phương thức tạo token
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", role) // Thêm claim role (hoặc quyền)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ
                .signWith(secretKey)
                .compact();
    }

    // Phương thức kiểm tra token hợp lệ và chưa hết hạn
    // Phương thức kiểm tra token hợp lệ và chưa hết hạn
    public Boolean validateToken(String token, String username) {
        try {
            // Giải mã token để kiểm tra tính hợp lệ
            String extractedUsername = extractUsername(token);  // Lấy username từ token
            return extractedUsername != null && !isTokenExpired(token) && extractedUsername.equals(username);  // Kiểm tra token có hợp lệ và chưa hết hạn
        } catch (Exception e) {
            return false;
        }
    }


    // Phương thức kiểm tra token đã hết hạn chưa
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Lấy thời gian hết hạn của token
    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // Giải mã token và lấy username
    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Giải mã token và lấy role từ claims
    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roles", String.class); // Lấy "role" từ claims
    }


    // Kiểm tra xem người dùng có quyền ADMIN không
    public Boolean hasAdminRole(String token) {
        try {
            String role = extractRole(token); // Lấy role từ token
            return "ROLE_ADMIN".equals(role);  // Kiểm tra xem role có phải là "1" hay không
        } catch (Exception e) {
            return false;  // Nếu có lỗi khi lấy role, return false
        }
    }

}
