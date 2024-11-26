package Gold40.Controller;

import Gold40.DAO.HoaDonDAO;
import Gold40.Entity.HoaDon;
import Gold40.Service.TaiKhoanService;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoadonnd")
public class HoaDonNDController {

    @Autowired
    private HoaDonDAO hoaDonDAO;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private JwtUtil jwtUtil;

    // Lấy danh sách hóa đơn theo người dùng đăng nhập
    @GetMapping
    public ResponseEntity<?> getHoaDonByUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ "Bearer "
            String username = jwtUtil.extractUsername(token);
            if (jwtUtil.validateToken(token, username)) {
                var taiKhoan = taiKhoanService.findByTaikhoan(username);
                if (taiKhoan != null) {
                    String maNguoiDung = taiKhoan.getManguoidung();

                    // Log thông tin người dùng
                    System.out.println("Người dùng đang đăng nhập: " + username);
                    System.out.println("Mã người dùng: " + maNguoiDung);

                    // Lấy danh sách hóa đơn từ `HoaDonDAO`
                    List<HoaDon> hoaDons = hoaDonDAO.findByMaNguoiDung(maNguoiDung);
                    System.out.println("Danh sách hóa đơn: " + hoaDons);

                    if (hoaDons.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Không có hóa đơn nào.");
                    }

                    return ResponseEntity.ok(hoaDons);
                }
                return ResponseEntity.status(403).body("Tài khoản không tồn tại.");
            }
            return ResponseEntity.status(401).body("Token không hợp lệ.");
        }
        return ResponseEntity.status(400).body("Token không được cung cấp.");
    }
}
