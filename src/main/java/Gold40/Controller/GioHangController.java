package Gold40.Controller;

import Gold40.DAO.GioHangDAO;
import Gold40.DAO.ProductsDAO;
import Gold40.Entity.GioHang;
import Gold40.Entity.NguoiDung;
import Gold40.Entity.SanPham;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.GioHangService;
import Gold40.Service.NguoiDungService;
import Gold40.Service.TaiKhoanService;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("api/user")
public class GioHangController {
    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private GioHangDAO GioHangDAO;
@Autowired
private ProductsDAO productsDAO;
@Autowired
private GioHangService gioHangService;
    @Autowired
    private JwtUtil jwtUtil;
    private String extractToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
    }
    @GetMapping("/giohang")
    public ResponseEntity<?> getUserCart(@RequestHeader("Authorization") String token) {
        // Trích xuất token
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
        }

        // Lấy tên tài khoản từ token
        String taikhoan = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        // Lấy mã người dùng từ tài khoản
        String maNguoiDung = user.getManguoidung();

        // Lấy danh sách sản phẩm từ giỏ hàng của người dùng
        List<GioHang> sanPhamList = GioHangDAO.findSanPhamByNguoiDung(maNguoiDung);

        // Kiểm tra nếu giỏ hàng không có sản phẩm
        if (sanPhamList == null || sanPhamList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Giỏ hàng của bạn hiện không có sản phẩm nào");
        }

        // Trả về danh sách sản phẩm trong giỏ hàng
        return ResponseEntity.ok(sanPhamList);
    }





    private int generateRandomProductCode() {
        Random random = new Random();
        return random.nextInt(90000) + 10000;  // Generate a 6-digit number between 100000 and 999999
    }
    @PostMapping("/giohang")
    public ResponseEntity<?> updateGioHang(
            @RequestHeader("Authorization") String token,
            @RequestBody GioHang gioHangRequest) {

        // Kiểm tra token
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không có quyền truy cập");
        }

        // Trích xuất tài khoản từ token
        String taikhoan = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        // Lấy mã người dùng từ tài khoản
        String maNguoiDung = user.getManguoidung();

        // Kiểm tra sản phẩm có tồn tại không
        if (productsDAO.findByMaSanPham(gioHangRequest.getSanPham().getMaSanPham()) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không tồn tại");
        }

        // Tìm giỏ hàng của người dùng với sản phẩm tương ứng
        GioHang gioHang = GioHangDAO.findByNguoiDungAndSanPham_MaSanPham(
                nguoiDungService.findByMaNguoiDung(maNguoiDung),
                gioHangRequest.getSanPham().getMaSanPham()
        );

        // Tạo mới giỏ hàng nếu chưa có sản phẩm
        if (gioHang == null) {
            gioHang = new GioHang();
            gioHang.setMaGioHang(generateRandomProductCode());  // Đảm bảo mã giỏ hàng được gán
            gioHang.setNguoiDung(user.getNguoiDung());
            gioHang.setSanPham(gioHangRequest.getSanPham());
            gioHang.setSoLuong(gioHangRequest.getSoLuong());
            gioHang.setKichThuoc(gioHangRequest.getKichThuoc());
            System.out.println(gioHang);
        } else {
            // Cập nhật thông tin giỏ hàng nếu sản phẩm đã có
            gioHang.setSoLuong(gioHang.getSoLuong() + gioHangRequest.getSoLuong());
            gioHang.setKichThuoc(gioHangRequest.getKichThuoc());
        }

// Kiểm tra xem mã giỏ hàng có được gán đúng không trước khi lưu
        if (gioHang.getMaGioHang() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã giỏ hàng không hợp lệ");
        }

// Lưu thông tin giỏ hàng vào cơ sở dữ liệu
        gioHangService.save(gioHang);


        // Trả về phản hồi
        Map<String, Object> response = new HashMap<>();
        response.put("gioHang", gioHang);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/giohang/{maGioHang}")
    public ResponseEntity<?> updateProductQuantity(
            @RequestHeader("Authorization") String token,
            @PathVariable("maGioHang") int maGioHang,
            @RequestParam("soLuong") int soLuong) {

        // Kiểm tra token
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không có quyền truy cập");
        }

        // Trích xuất tài khoản từ token
        String taikhoan = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        // Tìm giỏ hàng của người dùng với mã giỏ hàng
        GioHang gioHang = GioHangDAO.findById(maGioHang).orElse(null);
        if (gioHang == null || !gioHang.getNguoiDung().getMaNguoiDung().equals(user.getManguoidung())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không tồn tại trong giỏ hàng của bạn");
        }

        // Cập nhật số lượng sản phẩm
        gioHang.setSoLuong(soLuong);

        // Lưu lại thông tin giỏ hàng
        gioHangService.save(gioHang);

        // Trả về phản hồi
        Map<String, Object> response = new HashMap<>();
        response.put("gioHang", gioHang);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/giohang/{maSanPham}")
    public ResponseEntity<?> removeFromCart(
            @RequestHeader("Authorization") String token,
            @PathVariable("maSanPham") int maSanPham) {

        // Kiểm tra token và người dùng như bình thường
        token = extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không có quyền truy cập");
        }

        String taikhoan = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        // Lấy mã người dùng từ tài khoản
        String maNguoiDung = user.getManguoidung();

        // Tìm sản phẩm trong giỏ hàng của người dùng
        GioHang gioHang = GioHangDAO.findByNguoiDungAndSanPham_MaSanPham(
                nguoiDungService.findByMaNguoiDung(maNguoiDung),
                maSanPham
        );

        if (gioHang == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không có trong giỏ hàng");
        }

        // Xóa sản phẩm khỏi giỏ hàng
        GioHangDAO.delete(gioHang);

        return ResponseEntity.ok().body("Sản phẩm đã được xóa khỏi giỏ hàng");
    }






}
