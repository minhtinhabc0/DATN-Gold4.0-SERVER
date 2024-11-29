package Gold40.Controller;

import Gold40.Entity.*;
import Gold40.Service.*;
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
import java.util.List;
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
    private Gold40.Service.LichSuGiaoDichService LichSuGiaoDichService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Gold40.Service.VangMiengService VangMiengService;

    public UserController() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcr0bghdp",
                "api_key", "324571541243998",
                "api_secret", "9qkIzV4MT0uRXvlVwSFkpZrHUEo"));
    }

    private String extractToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
    }

//    @PostMapping("/upload/{manguoidung}")
//    public ResponseEntity<String> uploadImage(@PathVariable("manguoidung") String maNguoiDung, @RequestParam("file") MultipartFile file) {
//        try {
//            // Tải ảnh lên Cloudinary
//            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
//            String imageUrl = (String) result.get("url"); // Nhận URL của ảnh đã upload
//
//            // Lưu URL vào NguoiDung
//            NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(maNguoiDung);
//            if (nguoiDung != null) {
//                nguoiDung.setAvt(imageUrl); // Lưu URL vào trường avt
//                nguoiDungService.save(nguoiDung); // Lưu lại NguoiDung với URL mới
//                return ResponseEntity.ok(imageUrl); // Trả về URL
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

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
        GcoinService.save(newGcoin);

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

        // Kiểm tra nếu chưa có mã PIN
        if (user.getMapin() == null || user.getMapin().isEmpty()) {
            // Tạo mã PIN mới
            if (newPin == null || newPin.length() > 6) {
                return ResponseEntity.badRequest().body("Mã PIN mới phải có tối đa 6 ký tự.");
            }
            user.setMapin(passwordEncoder.encode(newPin));
        } else {
            // Đổi mã PIN
            if (!passwordEncoder.matches(oldPin, user.getMapin())) {
                return ResponseEntity.badRequest().body("Mã PIN cũ không chính xác.");
            }
            if (newPin == null || newPin.length() > 6) {
                return ResponseEntity.badRequest().body("Mã PIN mới phải có tối đa 6 ký tự.");
            }
            user.setMapin(passwordEncoder.encode(newPin));
        }

        taiKhoanService.save(user);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Cập nhật mã PIN thành công."));
    }
    @PostMapping("/profile/exchange-gold")
    public ResponseEntity<?> exchangeGold(@RequestHeader("Authorization") String token, @RequestParam double goldAmount) {
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
        }

        // Xác thực người dùng
        String username = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(user.getManguoidung());
        if (nguoiDung == null || nguoiDung.getMaGCoin() == null) {
            return ResponseEntity.badRequest().body("Người dùng chưa có ví Gcoin");
        }

        // Lấy ví Gcoin
        Gcoin gcoin = GcoinService.findByMagcoin(nguoiDung.getMaGCoin());
        if (gcoin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ví Gcoin không tồn tại");
        }

        // Tính toán Gcoin cần thiết (1000 Gcoin = 1 chỉ vàng)
        double gcoinRequired = goldAmount * 1000;

        if (gcoin.getSogcoin() < gcoinRequired) {
            return ResponseEntity.badRequest().body("Số dư Gcoin không đủ để quy đổi");
        }

        // Trừ Gcoin và cập nhật
        gcoin.setSogcoin((int) (gcoin.getSogcoin() - gcoinRequired));
        GcoinService.save(gcoin);

        // Tạo mã vàng mới
        VangMieng newGold = new VangMieng();
        String maVang = "VANG" + System.currentTimeMillis(); // Mã vàng duy nhất
        newGold.setMaVang(maVang);
        newGold.setLoaiVang(null); // Để trống loại vàng
        newGold.setTenVang(null);  // Để trống tên vàng
        newGold.setGiaVang(null);  // Để trống giá vàng
        newGold.setMaNhaPhanPhoi(null); // Để trống mã nhà phân phối

        // Lưu mã vàng vào DB
        VangMiengService.save(newGold);

        // Ghi lịch sử giao dịch
        LichSuGiaoDich lichSu = new LichSuGiaoDich();
        lichSu.setMaLichSuGiaoDich("LSGD" + System.currentTimeMillis());
        lichSu.setThoiGian(new java.sql.Date(System.currentTimeMillis()));
        lichSu.setTrangThai("Đang đợi xử lý"); // Trạng thái mặc định
        lichSu.setSoluong((int) goldAmount);
        lichSu.setMaVang(newGold); // Liên kết mã vàng với lịch sử giao dịch
        lichSu.setNguoiDung(nguoiDung); // Ghi thông tin người dùng

        LichSuGiaoDichService.save(lichSu);

        return ResponseEntity.ok(Collections.singletonMap("message", "Quy đổi thành công! Mã vàng của bạn là: " + maVang));
    }
    @GetMapping("/profile/transaction-history")
    public ResponseEntity<?> getTransactionHistory(@RequestHeader("Authorization") String token) {
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
        }

        // Lấy thông tin người dùng từ token
        String username = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(user.getManguoidung());
        if (nguoiDung == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thông tin người dùng không tồn tại");
        }

        // Lấy lịch sử giao dịch của người dùng
        List<LichSuGiaoDich> transactions = LichSuGiaoDichService.getTransactionsByUser(nguoiDung.getMaNguoiDung());
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Không có giao dịch nào.");
        }

        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/profile/transaction-detail/{id}")
    public ResponseEntity<?> getTransactionDetail(@RequestHeader("Authorization") String token, @PathVariable("id") String maLichSuGiaoDich) {
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
        }

        // Xác thực người dùng
        String username = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(user.getManguoidung());
        if (nguoiDung == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thông tin người dùng không tồn tại");
        }

        // Lấy chi tiết giao dịch
        LichSuGiaoDich transaction = LichSuGiaoDichService.findById(maLichSuGiaoDich);
        if (transaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Giao dịch không tồn tại");
        }

        // Kiểm tra quyền: chỉ cho phép xem giao dịch của chính mình
        if (!transaction.getNguoiDung().getMaNguoiDung().equals(nguoiDung.getMaNguoiDung())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền xem giao dịch này");
        }

        // Trả về chi tiết giao dịch
        Map<String, Object> response = new HashMap<>();
        response.put("maLichSuGiaoDich", transaction.getMaLichSuGiaoDich());
        response.put("thoiGian", transaction.getThoiGian());
        response.put("trangThai", transaction.getTrangThai());
        response.put("soLuong", transaction.getSoluong());
        response.put("maVang", transaction.getMaVang() != null ? transaction.getMaVang().getMaVang() : null);

        return ResponseEntity.ok(response);
    }


}
