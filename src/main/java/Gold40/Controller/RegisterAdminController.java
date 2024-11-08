package Gold40.Controller;

import Gold40.Entity.Admin;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/ad")
public class RegisterAdminController {

    private TaiKhoanService taiKhoanService;
    private AdminService adminService;
    private RecapchaService reCaptchaService;
    private EmailService emailService;

    private final Map<String, TaiKhoan> temporaryTaiKhoanData = new HashMap<>();
    private final Map<String, Admin> temporaryAdminData = new HashMap<>();
    private final Map<String, String> temporaryOtps = new HashMap<>();
    private final Map<String, LocalDateTime> temporaryOtpTimestamps = new HashMap<>();

    public RegisterAdminController(TaiKhoanService taiKhoanService, AdminService adminService, RecapchaService reCaptchaService, EmailService emailService) {
        this.taiKhoanService = taiKhoanService;
        this.adminService = adminService;
        this.reCaptchaService = reCaptchaService;
        this.emailService = emailService;
    }
    @GetMapping("/check-admin-account")
    public ResponseEntity<?> checkAdminAccount() {
        Map<String, String> response = new HashMap<>();
        if (taiKhoanService.existsByVaitro(1)) {
            // Nếu có tài khoản admin, trả về thông báo lỗi
            response.put("message", "Đã có tài khoản admin trong hệ thống. Bạn không thể truy cập trang đăng ký.");
            System.out.println("Phản hồi: " + response);
            return ResponseEntity.badRequest().body(response);
        }
        // Nếu không có tài khoản admin, trả về thông báo thành công
        response.put("message", "Bạn có thể đăng ký tài khoản admin.");
        System.out.println("Phản hồi: " + response);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/registerad")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        if (registrationData == null ||
                !registrationData.containsKey("taikhoan") ||
                !registrationData.containsKey("matkhau") ||
                !registrationData.containsKey("email") ||
                !registrationData.containsKey("hoten") ||
                !registrationData.containsKey("recaptchaToken")) {
            return ResponseEntity.badRequest().body("Thiếu thông tin đăng ký");
        }
        String taikhoan = registrationData.get("taikhoan");
        String matkhau = registrationData.get("matkhau");
        String email = registrationData.get("email");
        String hoten = registrationData.get("hoten");
        String recaptchaToken = registrationData.get("recaptchaToken");
        boolean isRecaptchaValid = reCaptchaService.verifyRecaptcha(recaptchaToken);
        if (!isRecaptchaValid) {
            return ResponseEntity.badRequest().body("Xác minh reCAPTCHA thất bại");
        }

        if (taiKhoanService.existsByTaikhoan(taikhoan)) {
            return ResponseEntity.badRequest().body(2);
        }
        if (adminService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(1);
        }
        try {
            String maadmin = generateRandomId();
            Admin adminnew = new Admin();
            adminnew.setMaAdmin(maadmin);
            adminnew.setEmail(email);
            adminnew.setHoTen(hoten);

            TaiKhoan newTaiKhoan = new TaiKhoan();
            newTaiKhoan.setTaikhoan(taikhoan);
            newTaiKhoan.setMatkhau(matkhau);
            newTaiKhoan.setMaadmin(adminnew.getMaAdmin());

            String otp = generateOtp();
            String subject = "GOLD 4.0 SUPPORT - Mã OTP Của Bạn";
            String body = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "  body { font-family: Arial, sans-serif; }" +
                    "  .container { padding: 20px; background-color: #f9f9f9; border-radius: 10px; width: 100%; max-width: 600px; margin: 0 auto; }" +
                    "  h1 { color: #4CAF50; }" +
                    "  p { font-size: 16px; }" +
                    "  .otp { font-size: 24px; color: #ff5722; font-weight: bold; }" +
                    "  .footer { margin-top: 20px; font-size: 12px; color: #888; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "  <h1>Chào bạn,  " + hoten + "</h1>" +
                    "  <p>Cảm ơn bạn đã đăng ký tài khoản tại GOLD 4.0. Dưới đây là mã OTP của bạn:</p>" +
                    "  <p class='otp'>" + otp + "</p>" +
                    "  <p>Vui lòng nhập mã này trong vòng 1 phút để xác nhận tài khoản của bạn.</p>" +
                    "  <p>Trân trọng,<br>Đội ngũ GOLD 4.0 SUPPORT</p>" +
                    "  <div class='footer'>" +
                    "    <p>Nếu bạn không yêu cầu mã OTP này, vui lòng bỏ qua email này.</p>" +
                    "  </div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            emailService.sendEmail(email, subject, body, true);
            temporaryTaiKhoanData.put(email, newTaiKhoan);
            temporaryAdminData.put(email, adminnew);
            temporaryOtps.put(email, otp);
            temporaryOtpTimestamps.put(email, LocalDateTime.now());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đã gửi mã OTP đến email. Vui lòng xác thực mã OTP.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đăng ký thất bại: " + e.getMessage());
        }
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> verificationData) {
        String email = verificationData.get("email");
        String otp = verificationData.get("otp");

        Admin adminData = temporaryAdminData.get(email);  // Use Admin instead of NguoiDung
        String savedOtp = temporaryOtps.get(email);
        TaiKhoan userData = temporaryTaiKhoanData.get(email);
        LocalDateTime otpSentTime = temporaryOtpTimestamps.get(email);

        if (adminData == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy thông tin đăng ký cho email: " + email));
        }

        if (otpSentTime != null && ChronoUnit.MINUTES.between(otpSentTime, LocalDateTime.now()) > 1) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Mã OTP đã hết hiệu lực."));
        }

        if (savedOtp != null && savedOtp.equals(otp)) {
            // Save admin data
            adminService.save(adminData);  // Save the Admin entity
            String maadmin = adminData.getMaAdmin();
            // Save corresponding TaiKhoan entity


            taiKhoanService.registerForAdmin(userData.getTaikhoan(), userData.getMatkhau(), maadmin);

            // Clear temporary data
            temporaryTaiKhoanData.remove(email);
            temporaryAdminData.remove(email);
            temporaryOtps.remove(email);
            temporaryOtpTimestamps.remove(email);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng ký thành công.");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Mã OTP không hợp lệ."));
        }
    }


    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> resendData) {
        String email = resendData.get("email");

        // Lấy thông tin người dùng tạm thời
        TaiKhoan userData = temporaryTaiKhoanData.get(email);
        Admin adminData = temporaryAdminData.get(email);

        if (userData == null || adminData == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy thông tin đăng ký cho email: " + email));
        }

        try {
            String otp = generateOtp();
            String subject = "GOLD 4.0 SUPPORT - Mã OTP Của Bạn";
            String body = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "  body { font-family: Arial, sans-serif; }" +
                    "  .container { padding: 20px; background-color: #f9f9f9; border-radius: 10px; width: 100%; max-width: 600px; margin: 0 auto; }" +
                    "  h1 { color: #4CAF50; }" +
                    "  p { font-size: 16px; }" +
                    "  .otp { font-size: 24px; color: #ff5722; font-weight: bold; }" +
                    "  .footer { margin-top: 20px; font-size: 12px; color: #888; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "  <h1>Chào bạn,</h1>" +
                    "  <p>Đây là mã OTP đã được gửi lại:</p>" +
                    "  <p class='otp'>" + otp + "</p>" +
                    "  <p>Vui lòng nhập mã này trong vòng 1 phút để xác nhận tài khoản của bạn.</p>" +
                    "  <p>Trân trọng,<br>Đội ngũ GOLD 4.0 SUPPORT</p>" +
                    "  <div class='footer'>" +
                    "    <p>Nếu bạn không yêu cầu mã OTP này, vui lòng bỏ qua email này.</p>" +
                    "  </div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            emailService.sendEmail(email, subject, body, true);

            // Cập nhật lại mã OTP và thời gian gửi
            temporaryOtps.put(email, otp);
            temporaryOtpTimestamps.put(email, LocalDateTime.now());

            return ResponseEntity.ok(Collections.singletonMap("message", "Mã OTP đã được gửi lại đến email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gửi lại mã OTP thất bại: " + e.getMessage());
        }
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private String generateRandomId() {
        String characters = "0123456789";
        StringBuilder result;
        String newId;

        do {
            result = new StringBuilder();
            Random random = new Random();

            for (int i = 0; i < 10; i++) {
                int index = random.nextInt(characters.length());
                result.append(characters.charAt(index));
            }

            newId = result.toString();
        } while (adminService.kiemTraNguoiDung(newId));

        return newId;
    }
}
