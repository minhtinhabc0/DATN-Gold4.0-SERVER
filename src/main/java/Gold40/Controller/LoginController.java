package Gold40.Controller;

import Gold40.Entity.TaiKhoan;
import Gold40.Service.RecapchaService;
import Gold40.Service.TaiKhoanService;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RecapchaService recaptchaService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        if (loginData == null ||
                !loginData.containsKey("tenTK") ||
                !loginData.containsKey("matKhau") ||
                !loginData.containsKey("recaptchaToken")) {
            return ResponseEntity.badRequest().body("Thiếu thông tin đăng nhập");
        }

        String tenTK = loginData.get("tenTK");
        String matKhau = loginData.get("matKhau");
        String recaptchaToken = loginData.get("recaptchaToken");

        // Kiểm tra reCAPTCHA
        boolean isRecaptchaValid = recaptchaService.verifyRecaptcha(recaptchaToken);
        if (!isRecaptchaValid) {
            return ResponseEntity.badRequest().body("Xác minh reCAPTCHA thất bại");
        }

        try {
            // Đăng nhập và lấy người dùng
            TaiKhoan user = taiKhoanService.login(tenTK, matKhau);

            // Kiểm tra vai trò người dùng
            if (user.getVaitro() != 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Tài khoản không có quyền truy cập");
            }

            // Lấy thời gian hiện tại
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
            String formattedTime = currentTime.format(formatter);
            System.out.println("Khách hàng " + tenTK + " đã đăng nhập thành công vào lúc " + formattedTime);

            // Tạo token JWT cho người dùng
            String token = jwtUtil.generateToken(user.getTaikhoan(), "ROLE_USER");

            // Lấy thông tin người dùng
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", user.getTaikhoan());
            userInfo.put("email", user.getNguoiDung().getEmail());
            userInfo.put("roles", user.getVaitro());

            // Tạo response trả về cho client
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userInfo", userInfo);
            response.put("redirectUrl", "/home");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // Lấy token sau "Bearer "

            // Giải mã token để lấy username
            String username = jwtUtil.extractUsername(token);  // Lấy username từ token

            // Kiểm tra token hợp lệ và username
            if (jwtUtil.validateToken(token, username)) {
                return ResponseEntity.ok("Xác thực thành công");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
    }

}
