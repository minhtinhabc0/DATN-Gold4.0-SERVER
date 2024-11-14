package Gold40.Controller;

import Gold40.Entity.NhaPhanPhoi;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/npp")
public class RegisterNPPController {

    private final TaiKhoanService taiKhoanService;
    private final NhaPhanPhoiService phanPhoiService;
    private final RecapchaService reCaptchaService;
    private final EmailService emailService;
    private final NhaPhanPhoiService nhaPhanPhoiService;
    private final CloudinaryService cloudinaryService;

    private final Map<String, TaiKhoan> temporaryTaiKhoanData = new HashMap<>();
    private final Map<String, NhaPhanPhoi> temporaryNhaPhanPhoiData = new HashMap<>();
    private final Map<String, String> temporaryOtps = new HashMap<>();
    private final Map<String, LocalDateTime> temporaryOtpTimestamps = new HashMap<>();

    public RegisterNPPController(TaiKhoanService taiKhoanService, NhaPhanPhoiService phanPhoiService, RecapchaService reCaptchaService, EmailService emailService, NhaPhanPhoiService nhaPhanPhoiService, CloudinaryService cloudinaryService) {
        this.taiKhoanService = taiKhoanService;
        this.phanPhoiService = phanPhoiService;
        this.reCaptchaService = reCaptchaService;
        this.emailService = emailService;
        this.nhaPhanPhoiService = nhaPhanPhoiService;
        this.cloudinaryService = cloudinaryService;
    }

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData,
                                      @RequestParam("file1") MultipartFile file1,
                                      @RequestParam("file2") MultipartFile file2,
                                      @RequestParam("file3") MultipartFile file3) {
        if (registrationData == null ||
                !registrationData.containsKey("taikhoan") ||
                !registrationData.containsKey("matkhau") ||
                !registrationData.containsKey("email") ||
                !registrationData.containsKey("diachi") ||
                !registrationData.containsKey("tencuahang") ||
                !registrationData.containsKey("sdt") ||
                !registrationData.containsKey("dl1") ||
                !registrationData.containsKey("dl2") ||
                !registrationData.containsKey("dl3") ||
                !registrationData.containsKey("recaptchaToken")) {
            return ResponseEntity.badRequest().body("Thiếu thông tin đăng ký");
        }

        String taikhoan = registrationData.get("taikhoan");
        String matkhau = registrationData.get("matkhau");
        String email = registrationData.get("email");
        String tencuahang = registrationData.get("tencuahang");
        String diachi = registrationData.get("diachi");
        String sdt = registrationData.get("sdt");
        String recaptchaToken = registrationData.get("recaptchaToken");

        boolean isRecaptchaValid = reCaptchaService.verifyRecaptcha(recaptchaToken);
        if (!isRecaptchaValid) {
            return ResponseEntity.badRequest().body("Xác minh reCAPTCHA thất bại");
        }

        if (taiKhoanService.existsByTaikhoan(taikhoan)) {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại");
        }
        if (nhaPhanPhoiService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email đã tồn tại");
        }

        try {
            // Handle file uploads and get URLs
            String dl1Url = cloudinaryService.uploadFile(file1);
            String dl2Url = cloudinaryService.uploadFile(file2);
            String dl3Url = cloudinaryService.uploadFile(file3);

            String maNhaPhanPhoi = generateRandomId();
            NhaPhanPhoi newNhaPhanPhoi = new NhaPhanPhoi();
            newNhaPhanPhoi.setMaNhaPhanPhoi(maNhaPhanPhoi);
            newNhaPhanPhoi.setTenCuaHang(tencuahang);
            newNhaPhanPhoi.setSdt(sdt);
            newNhaPhanPhoi.setEmail(email);
            newNhaPhanPhoi.setDiaChi(diachi);
            newNhaPhanPhoi.setDl1(dl1Url);
            newNhaPhanPhoi.setDl2(dl2Url);
            newNhaPhanPhoi.setDl3(dl3Url);

            TaiKhoan newTaiKhoan = new TaiKhoan();
            newTaiKhoan.setTaikhoan(taikhoan);
            newTaiKhoan.setMatkhau(matkhau);
            newTaiKhoan.setManhaphanphoi(maNhaPhanPhoi);

            String otp = generateOtp();
            String subject = "GOLD 4.0 SUPPORT - Mã OTP Của Bạn";
            String body = "<html><body><h1>Chào bạn,</h1><p>OTP của bạn là: " + otp + "</p></body></html>";
            emailService.sendEmail(email, subject, body, true);

            temporaryTaiKhoanData.put(email, newTaiKhoan);
            temporaryNhaPhanPhoiData.put(email, newNhaPhanPhoi);
            temporaryOtps.put(email, otp);
            temporaryOtpTimestamps.put(email, LocalDateTime.now());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đã gửi mã OTP đến email. Vui lòng xác thực mã OTP.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đăng ký thất bại: " + e.getMessage());
        }
    }

    // OTP verification endpoint
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> otpData) {
        String email = otpData.get("email");
        String otp = otpData.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body("Email hoặc OTP không hợp lệ");
        }

        // Check if OTP exists and if it is valid
        if (!temporaryOtps.containsKey(email)) {
            return ResponseEntity.badRequest().body("Không tìm thấy mã OTP cho email này");
        }

        String storedOtp = temporaryOtps.get(email);
        LocalDateTime otpTimestamp = temporaryOtpTimestamps.get(email);

        if (!storedOtp.equals(otp)) {
            return ResponseEntity.badRequest().body("Mã OTP không chính xác");
        }

        // Check OTP expiration (5 minutes)
        if (ChronoUnit.MINUTES.between(otpTimestamp, LocalDateTime.now()) > 5) {
            return ResponseEntity.badRequest().body("Mã OTP đã hết hạn");
        }

        // OTP is valid, save information
        TaiKhoan newTaiKhoan = temporaryTaiKhoanData.get(email);
        NhaPhanPhoi newNhaPhanPhoi = temporaryNhaPhanPhoiData.get(email);

        taiKhoanService.save(newTaiKhoan);
        nhaPhanPhoiService.save(newNhaPhanPhoi);

        // Clear temporary data
        temporaryTaiKhoanData.remove(email);
        temporaryNhaPhanPhoiData.remove(email);
        temporaryOtps.remove(email);
        temporaryOtpTimestamps.remove(email);

        return ResponseEntity.ok("Đăng ký thành công");
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private String generateRandomId() {
        String characters = "0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }
}

