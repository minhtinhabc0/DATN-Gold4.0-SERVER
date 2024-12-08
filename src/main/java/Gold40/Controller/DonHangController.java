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
import java.util.Optional;

@RestController
@RequestMapping("/api/donhang")
public class DonHangController {

    @Autowired
    private DonHangDAO donHangDao;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private JwtUtil jwtUtil;
    // Lấy danh sách đơn hàng
    @GetMapping
    public ResponseEntity<?> getDonHangByDistributor(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ "Bearer "
            String username = jwtUtil.extractUsername(token);
            if (jwtUtil.validateToken(token, username)) {
                // Lấy tài khoản đăng nhập
                var taiKhoan = taiKhoanService.findByTaikhoan(username);
                if (taiKhoan != null && taiKhoan.getVaitro() == 2) { // Kiểm tra vai trò nhà phân phối
                    String maNhaPhanPhoi = taiKhoan.getManhaphanphoi();
                    System.out.println("Mã nhà phân phối: " + maNhaPhanPhoi); // Log debug
                    List<DonHang> donHangs = donHangDao.findDonHangsByDistributor(maNhaPhanPhoi);
                    System.out.println("Danh sách đơn hàng: " + donHangs); // Log debug
                    return ResponseEntity.ok(donHangs);
                } else {
                    System.out.println("Tài khoản không phải nhà phân phối");
                }
            } else {
                System.out.println("Token không hợp lệ");
            }
        } else {
            System.out.println("Token không được cung cấp");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
    }


    // Lấy chi tiết đơn hàng theo mã đơn hàng
    @GetMapping("/{maDonHang}")
    public ResponseEntity<DonHang> getDonHangById(@PathVariable("maDonHang") String maDonHang) {
        Optional<DonHang> donHang = donHangDao.findById(maDonHang);
        return donHang.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    // Cập nhật trạng thái đơn hàng khi xác nhận
    @PostMapping("/{maDonHang}/xacnhan")
    public ResponseEntity<?> xacNhanDonHang(@PathVariable("maDonHang") String maDonHang,
                                            @RequestBody String trangThaiMoi,
                                            @RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ "Bearer "
            String username = jwtUtil.extractUsername(token);
            if (jwtUtil.validateToken(token, username)) {
                // Kiểm tra tài khoản có vai trò nhà phân phối
                var taiKhoan = taiKhoanService.findByTaikhoan(username);
                if (taiKhoan != null && taiKhoan.getVaitro() == 2) {
                    Optional<DonHang> donHangOpt = donHangDao.findById(maDonHang);
                    if (donHangOpt.isPresent()) {
                        DonHang donHang = donHangOpt.get();
                        // Chỉ cập nhật nếu trạng thái hiện tại là "Đang xử lý"
                        if ("Đang xử lý".equals(donHang.getTrangThai())) {
                            donHang.setTrangThai(trangThaiMoi); // Cập nhật trạng thái mới
                            donHangDao.save(donHang); // Lưu lại đơn hàng với trạng thái mới
                            return ResponseEntity.ok("Cập nhật trạng thái thành công");
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Trạng thái không thể thay đổi");
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Đơn hàng không tồn tại");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền cập nhật");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
    }
    @GetMapping("/demsl/hoanthanh")
    public long countCompletedOrders(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ "Bearer "
            String username = jwtUtil.extractUsername(token); // Trích xuất username từ token
            if (jwtUtil.validateToken(token, username)) {
                // Lấy tài khoản đăng nhập
                var taiKhoan = taiKhoanService.findByTaikhoan(username);
                if (taiKhoan != null && taiKhoan.getVaitro() == 2) { // Kiểm tra vai trò nhà phân phối
                    String maNhaPhanPhoi = taiKhoan.getManhaphanphoi(); // Lấy mã nhà phân phối
                    // Tìm các đơn hàng "Hoàn thành" của nhà phân phối
                    List<DonHang> donHangs = donHangDao.findCompletedDonHangsByDistributor(maNhaPhanPhoi);
                    return donHangs.size(); // Đếm số lượng đơn hàng hoàn thành
                } else {
                    System.out.println("Tài khoản không phải nhà phân phối");
                }
            } else {
                System.out.println("Token không hợp lệ");
            }
        } else {
            System.out.println("Token không được cung cấp");
        }
        return 0; // Nếu không có quyền truy cập
    }



}
