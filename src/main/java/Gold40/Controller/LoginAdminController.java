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
public class LoginAdminController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RecapchaService recaptchaService;

    @PostMapping("/admin/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> loginData) {
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

            // Kiểm tra vai trò người dùng (admin hay không)
            if (user.getVaitro() == 1) {
                // Admin login
                return loginForAdmin(user);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Chỉ admin mới có thể đăng nhập tại đây");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    private ResponseEntity<?> loginForAdmin(TaiKhoan user) {
        // Xử lý đăng nhập cho admin (vaitro = 1)
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        String formattedTime = currentTime.format(formatter);
        System.out.println("Admin " + user.getTaikhoan() + " đã đăng nhập thành công vào lúc " + formattedTime);

        // Tạo token JWT cho admin
        String token = jwtUtil.generateToken(user.getTaikhoan());

        // Lấy thông tin admin
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", user.getTaikhoan());
        userInfo.put("hoTen", user.getAdmin().getHoTen());
        userInfo.put("id", user.getMaadmin());
        userInfo.put("email", user.getAdmin().getEmail());
        userInfo.put("roles", user.getVaitro());

        // Tạo response trả về cho client
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userInfo", userInfo);
        response.put("redirectUrl", "/admin/index.html"); // Redirect to the admin dashboard

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/check-auth")
    public ResponseEntity<?> checkAuth(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
            String username = jwtUtil.extractUsername(token);
            if (jwtUtil.validateToken(token, username)) {
                return ResponseEntity.ok("Xác thực thành công");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
    }
}
