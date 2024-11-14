package Gold40.Controller;

import Gold40.Entity.Admin;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.*;
import jakarta.mail.MessagingException;
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

    private final TaiKhoanService taiKhoanService;
    private final AdminService adminService;
    private final RecapchaService reCaptchaService;
    private final EmailService emailService;

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
    public ResponseEntity<Map<String, String>> checkAdminAccount() {
        Map<String, String> response = new HashMap<>();
        if (taiKhoanService.existsByVaitro(1)) {
            response.put("message", "Đã có tài khoản admin trong hệ thống. Bạn không thể truy cập trang đăng ký.");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "Bạn có thể đăng ký tài khoản admin.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registerad")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registrationData) {
        if (registrationData == null || !registrationData.containsKey("taikhoan") || !registrationData.containsKey("matkhau") ||
                !registrationData.containsKey("email") || !registrationData.containsKey("mapin") || !registrationData.containsKey("hoten") ||
                !registrationData.containsKey("recaptchaToken")) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Thiếu thông tin đăng ký"));
        }

        String taikhoan = registrationData.get("taikhoan");
        String matkhau = registrationData.get("matkhau");
        String mapin = registrationData.get("mapin");
        String email = registrationData.get("email");
        String hoten = registrationData.get("hoten");
        String recaptchaToken = registrationData.get("recaptchaToken");

        if (!reCaptchaService.verifyRecaptcha(recaptchaToken)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Xác minh reCAPTCHA thất bại"));
        }

        if (taiKhoanService.existsByTaikhoan(taikhoan)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tài khoản đã tồn tại"));
        }

        if (adminService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Email đã được đăng ký"));
        }

        try {
            String maadmin = generateRandomId(); // Generate unique ID for admin
            Admin admin = new Admin();
            admin.setMaAdmin(maadmin);
            admin.setEmail(email);
            admin.setHoTen(hoten);

            // Save the admin entity before creating TaiKhoan to ensure the foreign key constraint is not violated
            adminService.save(admin);

            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTaikhoan(taikhoan);
            taiKhoan.setMatkhau(matkhau);
            taiKhoan.setMapin(mapin);
            taiKhoan.setMaadmin(admin.getMaAdmin()); // Link TaiKhoan with the Admin

            String otp = generateOtp();
            sendOtpEmail(email, hoten, otp);

            temporaryTaiKhoanData.put(email, taiKhoan);
            temporaryAdminData.put(email, admin);
            temporaryOtps.put(email, otp);
            temporaryOtpTimestamps.put(email, LocalDateTime.now());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đã gửi mã OTP đến email. Vui lòng xác thực mã OTP.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Đăng ký thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> verificationData) {
        String email = verificationData.get("email");
        String otp = verificationData.get("otp");

        Admin adminData = temporaryAdminData.get(email);
        String savedOtp = temporaryOtps.get(email);
        TaiKhoan taiKhoanData = temporaryTaiKhoanData.get(email);
        LocalDateTime otpSentTime = temporaryOtpTimestamps.get(email);

        if (adminData == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy thông tin đăng ký cho email: " + email));
        }

        if (otpSentTime != null && ChronoUnit.MINUTES.between(otpSentTime, LocalDateTime.now()) > 1) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Mã OTP đã hết hiệu lực."));
        }

        if (savedOtp != null && savedOtp.equals(otp)) {
            String maAdmin = adminData.getMaAdmin();
            taiKhoanService.registerForAdmin(taiKhoanData.getTaikhoan(), taiKhoanData.getMatkhau(), taiKhoanData.getMapin(),maAdmin);
            adminService.save(adminData);
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
    public ResponseEntity<Map<String, Object>> resendOtp(@RequestBody Map<String, String> resendData) {
        String email = resendData.get("email");

        TaiKhoan taiKhoan = temporaryTaiKhoanData.get(email);
        Admin admin = temporaryAdminData.get(email);

        if (taiKhoan == null || admin == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy thông tin đăng ký cho email: " + email));
        }

        try {
            String otp = generateOtp();
            sendOtpEmail(email, admin.getHoTen(), otp);

            temporaryOtps.put(email, otp);
            temporaryOtpTimestamps.put(email, LocalDateTime.now());

            return ResponseEntity.ok(Collections.singletonMap("message", "Mã OTP đã được gửi lại đến email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Gửi lại mã OTP thất bại: " + e.getMessage()));
        }
    }

    private void sendOtpEmail(String email, String hoten, String otp) throws MessagingException {
        String subject = "GOLD 4.0 SUPPORT - Mã OTP Của Bạn";
        String body = "<html><body>" +
                "<h1>Chào bạn, " + hoten + "</h1>" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản tại GOLD 4.0. Dưới đây là mã OTP của bạn:</p>" +
                "<p class='otp'>" + otp + "</p>" +
                "<p>Vui lòng nhập mã này trong vòng 1 phút để xác nhận tài khoản của bạn.</p>" +
                "<p>Trân trọng,<br>Đội ngũ GOLD 4.0 SUPPORT</p>" +
                "</body></html>";
        emailService.sendEmail(email, subject, body, true);
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
        } while (adminService.kiemTraNguoiDung(newId)); // Check if ID already exists

        return newId;
    }
}
