package Gold40.Controller;

import Gold40.Entity.NhaPhanPhoi;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/npp")
public class RegisterNPPController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private final TaiKhoanService taiKhoanService;
    private final NhaPhanPhoiService phanPhoiService;
    private final RecapchaService reCaptchaService;
    private final EmailService emailService;

    // Temporary data storage for OTP verification
    private final Map<String, TaiKhoan> temporaryTaiKhoanData = new HashMap<>();
    private final Map<String, NhaPhanPhoi> temporaryNhaPhanPhoiData = new HashMap<>();
    private final Map<String, String> temporaryOtps = new HashMap<>();
    private final Map<String, LocalDateTime> temporaryOtpTimestamps = new HashMap<>();

    public RegisterNPPController(TaiKhoanService taiKhoanService, NhaPhanPhoiService phanPhoiService,
                                 RecapchaService reCaptchaService, EmailService emailService) {
        this.taiKhoanService = taiKhoanService;
        this.phanPhoiService = phanPhoiService;
        this.reCaptchaService = reCaptchaService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        // Check if registration data is valid
        if (registrationData == null || !registrationData.containsKey("taikhoan") ||
                !registrationData.containsKey("matkhau") || !registrationData.containsKey("email")) {
            return ResponseEntity.badRequest().body("Thiếu thông tin đăng ký");
        }

        try {
            // Generate unique ID for NhaPhanPhoi and TaiKhoan objects
            String maNhaPhanPhoi = generateRandomId();
            NhaPhanPhoi newNhaPhanPhoi = new NhaPhanPhoi();
            newNhaPhanPhoi.setMaNhaPhanPhoi(maNhaPhanPhoi);
            newNhaPhanPhoi.setTenCuaHang(registrationData.get("tencuahang"));
            newNhaPhanPhoi.setEmail(registrationData.get("email"));
            newNhaPhanPhoi.setDiaChi(registrationData.get("diachi"));
            newNhaPhanPhoi.setSdt(registrationData.get("sdt"));
            newNhaPhanPhoi.setDl1(registrationData.get("dl1"));
            newNhaPhanPhoi.setDl2(registrationData.get("dl2"));
            newNhaPhanPhoi.setDl3(registrationData.get("dl3"));

            TaiKhoan newTaiKhoan = new TaiKhoan();
            newTaiKhoan.setTaikhoan(registrationData.get("taikhoan"));
            newTaiKhoan.setMatkhau(passwordEncoder.encode(registrationData.get("matkhau")));
            newTaiKhoan.setManhaphanphoi(maNhaPhanPhoi);

            // Generate OTP and send email
            String otp = generateOtp();
            String subject = "GOLD 4.0 SUPPORT - Mã OTP Của Bạn";
            String body = "<html><body><h1>Chào bạn,</h1><p>Mã OTP của bạn là: " + otp + "</p></body></html>";
            emailService.sendEmail(registrationData.get("email"), subject, body, true);

            // Temporarily store the data for OTP verification
            temporaryTaiKhoanData.put(registrationData.get("email"), newTaiKhoan);
            temporaryNhaPhanPhoiData.put(registrationData.get("email"), newNhaPhanPhoi);
            temporaryOtps.put(registrationData.get("email"), otp);
            temporaryOtpTimestamps.put(registrationData.get("email"), LocalDateTime.now());

            return ResponseEntity.ok(Collections.singletonMap("message", "Đã gửi mã OTP đến email. Vui lòng xác thực mã OTP."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đăng ký thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> verificationData) {
        String email = verificationData.get("email");
        String otp = verificationData.get("otp");

        NhaPhanPhoi nhaPhanPhoi = temporaryNhaPhanPhoiData.get(email);
        String savedOtp = temporaryOtps.get(email);
        TaiKhoan taiKhoandata = temporaryTaiKhoanData.get(email);
        LocalDateTime otpSentTime = temporaryOtpTimestamps.get(email);

        if (nhaPhanPhoi == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy thông tin đăng ký cho email: " + email));
        }

        if (otpSentTime != null && ChronoUnit.MINUTES.between(otpSentTime, LocalDateTime.now()) > 1) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Mã OTP đã hết hiệu lực."));
        }

        if (savedOtp != null && savedOtp.equals(otp)) {
            // Save the TaiKhoan and NhaPhanPhoi to the database
            String maNhaPhanPhoi = nhaPhanPhoi.getMaNhaPhanPhoi();
            phanPhoiService.save(nhaPhanPhoi);

            taiKhoanService.registerForDistributor(taiKhoandata.getTaikhoan(), taiKhoandata.getMatkhau(), maNhaPhanPhoi);


            // Remove temporary data
            temporaryTaiKhoanData.remove(email);
            temporaryNhaPhanPhoiData.remove(email);
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
        NhaPhanPhoi nhaPhanPhoi = temporaryNhaPhanPhoiData.get(email);

        if (taiKhoan == null || nhaPhanPhoi == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Không tìm thấy thông tin đăng ký cho email: " + email));
        }

        try {
            String otp = generateOtp();
            String subject = "GOLD 4.0 SUPPORT - Mã OTP Của Bạn";
            String body = "<html><body><h1>Chào bạn,</h1><p>Mã OTP của bạn là: " + otp + "</p></body></html>";
            emailService.sendEmail(email, subject, body, true);

            temporaryOtps.put(email, otp);
            temporaryOtpTimestamps.put(email, LocalDateTime.now());

            return ResponseEntity.ok(Collections.singletonMap("message", "Mã OTP đã được gửi lại đến email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Gửi lại mã OTP thất bại: " + e.getMessage()));
        }
    }

    // Helper function to generate OTP
    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    // Helper function to generate random ID
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
        } while (phanPhoiService.kiemTraNguoiDung(newId)); // Check if ID already exists

        return newId;
    }
}
