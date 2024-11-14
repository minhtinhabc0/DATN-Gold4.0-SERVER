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

    // Login method for Admin
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

        // Check recaptcha validity
        boolean isRecaptchaValid = recaptchaService.verifyRecaptcha(recaptchaToken);
        if (!isRecaptchaValid) {
            return ResponseEntity.badRequest().body("Xác minh reCAPTCHA thất bại");
        }

        try {
            // Attempt to login and retrieve user details
            TaiKhoan user = taiKhoanService.login(tenTK, matKhau);

            // Check if user role is admin (vaitro == 1)
            if (user.getVaitro() == 1) {
                return loginForAdmin(user);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Chỉ admin mới có thể đăng nhập tại đây");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Tên tài khoản hoặc mật khẩu không đúng. Vui lòng kiểm tra lại.");
        }
    }

    // Method to handle login for admin users
    private ResponseEntity<?> loginForAdmin(TaiKhoan user) {
        // Process admin login (vaitro = 1)
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        String formattedTime = currentTime.format(formatter);
        System.out.println("Admin " + user.getTaikhoan() + " đã đăng nhập thành công vào lúc " + formattedTime);

        // Generate JWT token for admin
        String username = taiKhoanService.findByTaikhoan(user.getTaikhoan()).getTaikhoan(); // Fetch username from the database
        String role = "ROLE_ADMIN"; // Role for admin
        String token = jwtUtil.generateToken(username, role); // Generate token

        // Log the generated token (ensure to remove in production for security)
        System.out.println("Generated Token: " + token);

        // Collect admin information to send in the response
        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("username", user.getTaikhoan());
        adminInfo.put("mapin", user.getMapin());
        adminInfo.put("hoTen", user.getAdmin().getHoTen());
        adminInfo.put("id", user.getMaadmin());
        adminInfo.put("email", user.getAdmin().getEmail());
        adminInfo.put("roles", user.getVaitro());

        // Prepare response for the client
        Map<String, Object> response = new HashMap<>();
        response.put("token", token); // Include JWT token
        response.put("adminInfo", adminInfo); // Include admin information
        response.put("redirectUrl", "/admin/index.html"); // URL to redirect after login (admin dashboard)

        return ResponseEntity.ok(response); // Respond with success
    }

    // Endpoint to check if the user is authenticated
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
