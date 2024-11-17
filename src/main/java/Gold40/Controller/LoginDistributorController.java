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
public class LoginDistributorController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RecapchaService recaptchaService;

    // Login method for Distributor
    @PostMapping("/distributor/login")
    public ResponseEntity<?> loginDistributor(@RequestBody Map<String, String> loginData1) {
        if (loginData1 == null ||
                !loginData1.containsKey("tenTK") ||
                !loginData1.containsKey("mathau") ||
                !loginData1.containsKey("recaptchaToken")) {
            System.out.println("thieeu thong tin dang nhap");
            return ResponseEntity.badRequest().body("Thiếu thông tin đăng nhập");
        }

        String tenTK = loginData1.get("tenTK");
        String matKhau = loginData1.get("mathau");
        String recaptchaToken = loginData1.get("recaptchaToken");

        System.out.println("Login data received: " + loginData1);
        System.out.println(tenTK);
        System.out.println(matKhau);

        // Check recaptcha validity
        boolean isRecaptchaValid = recaptchaService.verifyRecaptcha(recaptchaToken);
        if (!isRecaptchaValid) {
            System.out.println("recaptcha invalid");
            return ResponseEntity.badRequest().body("Xác minh reCAPTCHA thất bại");
        }

        try {
            // Attempt to login and retrieve user details
            System.out.println("Login data received: " + loginData1);
            System.out.println(tenTK);
            System.out.println(matKhau);
            TaiKhoan user = taiKhoanService.login(tenTK, matKhau);
            System.out.println(user);
            System.out.println("dang nhap");

            if (user.getVaitro() == 2) {
                System.out.println("ddanwg nhap thanh cong");
                return loginForDistributor(user);
            } else {
                System.out.println("sai role");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)

                        .body("Chỉ nhà phân phối mới có thể đăng nhập tại đây");

            }

        } catch (Exception e) {
            System.out.println("sai tk mat khau");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Tên tài khoản hoặc mật khẩu không đúng. Vui lòng kiểm tra lại.");
        }
    }

    // Method to handle login for distributor users
    private ResponseEntity<?> loginForDistributor(TaiKhoan user) {
        // Process distributor login (vaitro = 2)
        System.out.println("10");
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        String formattedTime = currentTime.format(formatter);
        System.out.println("Distributor " + user.getTaikhoan() + " đã đăng nhập thành công vào lúc " + formattedTime);
        System.out.println("11");
        // Generate JWT token for distributor
        String username = taiKhoanService.findByTaikhoan(user.getTaikhoan()).getTaikhoan(); // Fetch username from the database
        String role = "ROLE_DISTRIBUTOR"; // Role for distributor
        String token = jwtUtil.generateToken(username, role); // Generate token

        // Log the generated token (ensure to remove in production for security)
        System.out.println("Generated Token: " + token);

        // Collect distributor information to send in the response
        Map<String, Object> distributorInfo = new HashMap<>();
        distributorInfo.put("username", user.getTaikhoan());
        distributorInfo.put("mapin", user.getMapin());
        distributorInfo.put("tencuahang", user.getNhaPhanPhoi().getTenCuaHang());
        distributorInfo.put("id", user.getManhaphanphoi());
        distributorInfo.put("email", user.getNhaPhanPhoi().getEmail());
        distributorInfo.put("roles", user.getVaitro());

        // Prepare response for the client
        Map<String, Object> response = new HashMap<>();
        response.put("token", token); // Include JWT token
        response.put("distributorInfo", distributorInfo); // Include distributor information
        response.put("redirectUrl", "/distributor/index.html"); // URL to redirect after login (distributor dashboard)

        return ResponseEntity.ok(response); // Respond with success
    }

    // Endpoint to check if the user is authenticated
    @GetMapping("/distributor/check-auth")
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
