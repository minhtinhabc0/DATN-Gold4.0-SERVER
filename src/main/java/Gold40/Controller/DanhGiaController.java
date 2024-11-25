package Gold40.Controller;

import Gold40.Entity.DanhGia;
import Gold40.Entity.SanPham;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.DanhGiaService;
import Gold40.Service.TaiKhoanService;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/danhgia")
public class DanhGiaController {

    @Autowired
    private DanhGiaService danhGiaService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TaiKhoanService taiKhoanService;

    // Lấy danh sách đánh giá theo mã sản phẩm
    @GetMapping("/sanpham/{masanpham}")
    public ResponseEntity<List<DanhGia>> getDanhGiaBySanPham(@PathVariable Integer masanpham) {
        List<DanhGia> danhGias = danhGiaService.getDanhGiaBySanPham(masanpham);
        if (danhGias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(danhGias);
    }

    // Xử lý token để lấy username
    private String extractToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
    }

    // Tạo mã đánh giá ngẫu nhiên
    private String generateRandomProductCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt(90000) + 10000); // Mã 5 chữ số
    }

    // Tạo đánh giá cho sản phẩm
    @PostMapping("/add/{masanpham}")
    public ResponseEntity<?> createDanhGia(
            @RequestBody Map<String, String> reviewData,
            @PathVariable Integer masanpham,
            @RequestHeader("Authorization") String token) {
        try {
            // Lấy token và tài khoản người dùng
            String extractedToken = extractToken(token);
            String taikhoan = jwtUtil.extractUsername(extractedToken);
            TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Người dùng không tồn tại");
            }
            System.out.println(reviewData);

            // Cập nhật thông tin cho đánh giá
            DanhGia danhGia = new DanhGia();
            danhGia.setMaDanhGia(generateRandomProductCode());
            danhGia.setNguoiDung(user.getNguoiDung());
            danhGia.setSanPham(new SanPham());
            danhGia.getSanPham().setMaSanPham(masanpham);
            danhGia.setNgayDanhGia(LocalDateTime.now());
            danhGia.setNoiDungDanhGia(reviewData.get("NoiDungDanhGia"));
            danhGia.setDiemDanhGia(Float.parseFloat(reviewData.get("DiemDanhGia")));

            // Lưu đánh giá
            System.out.println(danhGia);
            DanhGia createdDanhGia = danhGiaService.createDanhGia(danhGia);

            return ResponseEntity.ok(createdDanhGia);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
}
