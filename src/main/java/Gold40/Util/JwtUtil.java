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
        private SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        public String generateToken(String username) {
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ
                    .signWith(secretKey)  // Thay thế signWith bằng SecretKey
                    .compact();
        }

        public Boolean validateToken(String token, String username) {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        }

//    public String extractUsername(String token) {
//        return Jwts.parserBuilder().setSigningKey(secretKey).build()  // Thay thế setSigningKey bằng parserBuilder
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }

        private Boolean isTokenExpired(String token) {
            return Jwts.parserBuilder().setSigningKey(secretKey).build()  // Thay thế setSigningKey bằng parserBuilder
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        }

        public String extractUsername(String token) {
            // Giải mã token và lấy username từ đó
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            return claims.getSubject();
        }
    }

