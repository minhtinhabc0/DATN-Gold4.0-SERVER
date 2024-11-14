package Gold40.Controller;

import Gold40.Entity.NguoiDung;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.EmailService;
import Gold40.Service.NguoiDungService;
import Gold40.Service.RecapchaService;
import Gold40.Service.TaiKhoanService;
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
@RequestMapping("/api/re")
public class RegisterController {

    private final TaiKhoanService taiKhoanService;

    private final NguoiDungService nguoiDungService;

    private final RecapchaService reCaptchaService;

    private final EmailService emailService;

    private final Map<String, TaiKhoan> temporaryTaiKhoanData = new HashMap<>();
    private final Map<String, NguoiDung> temporaryNguoiDungData = new HashMap<>();
    private final Map<String, String> temporaryOtps = new HashMap<>();
    private final Map<String, LocalDateTime> temporaryOtpTimestamps = new HashMap<>();

    public RegisterController(TaiKhoanService taiKhoanService, NguoiDungService nguoiDungService, RecapchaService reCaptchaService, EmailService emailService) {
        this.taiKhoanService = taiKhoanService;
        this.nguoiDungService = nguoiDungService;
        this.reCaptchaService = reCaptchaService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        // Validate input data
        if (registrationData == null ||
                !registrationData.containsKey("taikhoan") ||
                !registrationData.containsKey("matkhau") ||
                !registrationData.containsKey("email") ||
                !registrationData.containsKey("hoten") ||
                !registrationData.containsKey("sdt") ||
                !registrationData.containsKey("recaptchaToken")) {
            return ResponseEntity.badRequest().body("Thiếu thông tin đăng ký");
        }

        String taikhoan = registrationData.get("taikhoan");
        String matkhau = registrationData.get("matkhau");
        String email = registrationData.get("email");
        String hoten = registrationData.get("hoten");
        String sdt = registrationData.get("sdt");
        String recaptchaToken = registrationData.get("recaptchaToken");

        boolean isRecaptchaValid = reCaptchaService.verifyRecaptcha(recaptchaToken);
        if (!isRecaptchaValid) {
            return ResponseEntity.badRequest().body("Xác minh reCAPTCHA thất bại");
        }

        if (taiKhoanService.existsByTaikhoan(taikhoan)) {
            System.out.println("tai khoan da ton tai ");
            return ResponseEntity.badRequest().body(2);
        }
        if (nguoiDungService.existsByEmail(email)) {
            System.out.println("Email da ton tai ");
            return ResponseEntity.badRequest().body(1);

        }

        try {
            String maNguoiDung = generateRandomId();
            NguoiDung newNguoiDung = new NguoiDung();
            newNguoiDung.setMaNguoiDung(maNguoiDung);
            newNguoiDung.setHoTen(hoten);
            newNguoiDung.setSdt(sdt);
            newNguoiDung.setEmail(email);

            TaiKhoan newTaiKhoan = new TaiKhoan();
            newTaiKhoan.setTaikhoan(taikhoan);
            newTaiKhoan.setMatkhau(matkhau);
            newTaiKhoan.setManguoidung(maNguoiDung);

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
            temporaryNguoiDungData.put(email, newNguoiDung);
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

        TaiKhoan userData = temporaryTaiKhoanData.get(email);
        NguoiDung nguoiDungData = temporaryNguoiDungData.get(email);
        String savedOtp = temporaryOtps.get(email);
        LocalDateTime otpSentTime = temporaryOtpTimestamps.get(email);

        if (userData == null || nguoiDungData == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy thông tin đăng ký cho email: " + email));
        }

        if (otpSentTime != null && ChronoUnit.MINUTES.between(otpSentTime, LocalDateTime.now()) > 1) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Mã OTP đã hết hiệu lực."));
        }

        if (savedOtp != null && savedOtp.equals(otp)) {
            nguoiDungService.save(nguoiDungData);
            String maNguoiDung = nguoiDungData.getMaNguoiDung();
            taiKhoanService.registerForUser(userData.getTaikhoan(), userData.getMatkhau(), maNguoiDung);

            temporaryTaiKhoanData.remove(email);
            temporaryNguoiDungData.remove(email);
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
        TaiKhoan userData = temporaryTaiKhoanData.get(email);
        NguoiDung nguoiDungData = temporaryNguoiDungData.get(email);

        if (userData == null || nguoiDungData == null) {
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
                    "  <p>đây là mã OTP đã được gửi lại:</p>" +
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
        } while (nguoiDungService.kiemTraNguoiDung(newId));

        return newId;
    }
}
