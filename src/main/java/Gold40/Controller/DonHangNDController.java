package Gold40.Controller;

import Gold40.DAO.DonHangDAO;
import Gold40.Entity.DonHang;
import Gold40.Service.TaiKhoanService;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/donhangnd")
public class DonHangNDController {

    @Autowired
    private DonHangDAO donHangDao;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private JwtUtil jwtUtil;
    // Lấy danh sách đơn hàng
    @GetMapping
    public ResponseEntity<?> getDonHangByUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ "Bearer "
            String username = jwtUtil.extractUsername(token);
            if (jwtUtil.validateToken(token, username)) {
                var taiKhoan = taiKhoanService.findByTaikhoan(username);
                if (taiKhoan != null) {
                    String maNguoiDung = taiKhoan.getManguoidung();  // Giả sử người dùng có mã phân phối

                    // In ra mã người dùng và thông tin đăng nhập
                    System.out.println("Người dùng đang đăng nhập: " + username);
                    System.out.println("Mã người dùng: " + maNguoiDung);

                    // Lấy danh sách yêu thích của người dùng từ YeuThichDAO
                    List<DonHang> donHangs = donHangDao.findDonHangsByUser(maNguoiDung);
                    System.out.println("Danh sách đơn hàng: " + donHangs); // Log debug
                    if (donHangs.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Không có đơn hàng nào.");
                    }
                    // Chuyển đổi danh sách DonHang thành danh sách DonHangDTO để gửi thêm thông tin

                    return ResponseEntity.ok(donHangs);
                }
                return ResponseEntity.status(403).body("Tài khoản không tồn tại.");
            }
            return ResponseEntity.status(401).body("Token không hợp lệ.");
        }
        return ResponseEntity.status(400).body("Token không được cung cấp.");
    }


}
