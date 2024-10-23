package Gold40.Controller;

import Gold40.Entity.NguoiDung;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.EmailService;
import Gold40.Service.NguoiDungService;
import Gold40.Service.TaiKhoanService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/users")
public class PasswordResetController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private NguoiDungService nguoiDungService; // Để tìm email trong bảng NguoiDung

    @Autowired
    private EmailService emailService; // Giả sử bạn có EmailService để gửi email.

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, String> otpStorage = new HashMap<>(); // Bộ nhớ tạm để lưu OTP

    // Bước 1: Nhập tài khoản
    @PostMapping("/forgot-password")
    public ResponseEntity<?> verifyAccount(@RequestBody Map<String, String> request) {
        String taikhoan = request.get("taikhoan");

        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tài khoản không tồn tại."));
        }

        return ResponseEntity.ok(Collections.singletonMap("message", "Tài khoản hợp lệ. Vui lòng nhập email."));
    }

    // Bước 2: Nhập email và gửi OTP
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) throws MessagingException {
        String taikhoan = request.get("taikhoan");
        String email = request.get("email");

        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tài khoản không tồn tại."));
        }

        NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(user.getManguoidung()); // Tìm NguoiDung theo mã người dùng
        if (nguoiDung == null || !nguoiDung.getEmail().equals(email)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Email không đúng với tài khoản."));
        }

        // Tạo mã OTP ngẫu nhiên (6 chữ số)
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(taikhoan, otpCode); // Lưu OTP tạm thời trong bộ nhớ

        // Gửi email
        emailService.sendEmail(email, "Mã xác thực của bạn", "<h1>Mã xác thực là: " + otpCode + "</h1>", true); // true nếu gửi HTML



        return ResponseEntity.ok(Collections.singletonMap("message", "Mã xác thực đã được gửi qua email."));
    }

    // Bước 3: Xác thực OTP và đổi mật khẩu
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String taikhoan = request.get("taikhoan");
        String otpCode = request.get("otp");
        String newPassword = request.get("newPassword");

        // Kiểm tra tài khoản
        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tài khoản không tồn tại."));
        }

        // Kiểm tra mã OTP
        String storedOtp = otpStorage.get(taikhoan);
        if (storedOtp == null || !storedOtp.equals(otpCode)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Mã xác thực không hợp lệ hoặc đã hết hạn."));
        }

        // Đổi mật khẩu
        user.setMatkhau(passwordEncoder.encode(newPassword));
        taiKhoanService.save(user);

        // Xóa OTP sau khi sử dụng
        otpStorage.remove(taikhoan);

        return ResponseEntity.ok(Collections.singletonMap("message", "Mật khẩu đã được thay đổi thành công."));
    }
}
