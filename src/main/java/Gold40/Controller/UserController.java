package Gold40.Controller;

import Gold40.Entity.Gcoin;
import Gold40.Entity.NguoiDung;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.GcoinService;
import Gold40.Service.NguoiDungService;
import Gold40.Service.TaiKhoanService;
import Gold40.Util.JwtUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final Cloudinary cloudinary;

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserController() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcr0bghdp",
                "api_key", "324571541243998",
                "api_secret", "9qkIzV4MT0uRXvlVwSFkpZrHUEo"));
    }

    private String extractToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
    }

    @PostMapping("/upload/{manguoidung}")
    public ResponseEntity<String> uploadImage(@PathVariable("manguoidung") String maNguoiDung, @RequestParam("file") MultipartFile file) {
        try {
            // Tải ảnh lên Cloudinary
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) result.get("url"); // Nhận URL của ảnh đã upload

            // Lưu URL vào NguoiDung
            NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(maNguoiDung);
            if (nguoiDung != null) {
                nguoiDung.setAvt(imageUrl); // Lưu URL vào trường avt
                nguoiDungService.save(nguoiDung); // Lưu lại NguoiDung với URL mới
                return ResponseEntity.ok(imageUrl); // Trả về URL
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
        }

        String taikhoan = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(user.getManguoidung());
        if (nguoiDung == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thông tin người dùng không tồn tại");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("taikhoan", user.getTaikhoan());
        userInfo.put("email", nguoiDung.getEmail());
        userInfo.put("hoTen", nguoiDung.getHoTen());
        userInfo.put("sdt", nguoiDung.getSdt());
        userInfo.put("avt", nguoiDung.getAvt());
        userInfo.put("pin", user.getMapin());
        userInfo.put("gcoin",nguoiDung.getMaGCoin());

        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestHeader("Authorization") String token, @RequestBody NguoiDung updateData) {
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
        }

        String taikhoan = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(user.getManguoidung());
        if (nguoiDung == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thông tin người dùng không tồn tại");
        }

        nguoiDung.setEmail(updateData.getEmail());
        nguoiDung.setHoTen(updateData.getHoTen());
        nguoiDung.setSdt(updateData.getSdt());
        nguoiDung.setAvt(updateData.getAvt());

        nguoiDungService.save(nguoiDung);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Cập nhật thông tin thành công."));
    }
    @GetMapping("/profile/gcoin")
    public ResponseEntity<?> getGcoinInfo(@RequestHeader("Authorization") String token) {
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
        }

        String taikhoan = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(user.getManguoidung());
        if (nguoiDung == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thông tin người dùng không tồn tại");
        }

        Map<String, Object> response = new HashMap<>();
        if (nguoiDung.getMaGCoin() == null) {
            response.put("hasWallet", false); // Không có ví Gcoin
        } else {
            Gcoin gcoin = GcoinService.findByMagcoin(nguoiDung.getMaGCoin());
            response.put("hasWallet", true); // Có ví Gcoin
            response.put("balance", gcoin.getSogcoin()); // Trả về số dư
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/profile/create-gcoin")
    public ResponseEntity<?> createGcoin(@RequestHeader("Authorization") String token) {
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
        }

        String taikhoan = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(user.getManguoidung());
        if (nguoiDung.getMaGCoin() != null) {
            return ResponseEntity.badRequest().body("Gcoin wallet already exists");
        }

        Gcoin newGcoin = new Gcoin();
        newGcoin.setMagcoin("G" + System.currentTimeMillis()); // Tạo ID Gcoin duy nhất
        newGcoin.setSogcoin(0); // Số dư ban đầu
        GcoinService.save(newGcoin); // Lưu lại entity Gcoin mới

        nguoiDung.setMaGCoin(newGcoin.getMagcoin()); // Gán ID Gcoin cho người dùng
        nguoiDungService.save(nguoiDung); // Cập nhật thông tin người dùng

        return ResponseEntity.ok(Collections.singletonMap("message", "Tạo ví Gcoin thành công."));
    }

    @PutMapping("/profile/password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> passwords) {
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập.");
        }

        String taikhoan = jwtUtil.extractUsername(token);
        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");

        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Tài khoản không tồn tại."));
        }

        if (!passwordEncoder.matches(oldPassword, user.getMatkhau())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Mật khẩu cũ không chính xác."));
        }

        user.setMatkhau(passwordEncoder.encode(newPassword));
        taiKhoanService.save(user);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Cập nhật mật khẩu thành công."));
    }

    @PutMapping("/profile/pin")
    public ResponseEntity<?> updatePin(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> pins) {
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập.");
        }

        String taikhoan = jwtUtil.extractUsername(token);
        String oldPin = pins.get("oldPin");
        String newPin = pins.get("newPin");

        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.badRequest().body("Tài khoản không tồn tại.");
        }

        if (newPin == null || newPin.length() > 6) {
            return ResponseEntity.badRequest().body("Mã PIN mới phải có tối đa 6 ký tự.");
        }

        if (!passwordEncoder.matches(oldPin, user.getMapin())) {
            return ResponseEntity.badRequest().body("Mã PIN cũ không chính xác.");
        }

        user.setMapin(passwordEncoder.encode(newPin));
        taiKhoanService.save(user);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Cập nhật mã PIN thành công."));
    }
}
