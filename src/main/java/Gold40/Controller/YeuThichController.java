package Gold40.Controller;

import Gold40.Entity.YeuThich;
import Gold40.Entity.SanPham;
import Gold40.Service.SanPhamService;
import Gold40.Service.TaiKhoanService;
import Gold40.DAO.YeuThichDAO;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/yeuthich")
public class YeuThichController {

    @Autowired
    private YeuThichDAO yeuThichDAO; // DAO cho YeuThich

    @Autowired
    private TaiKhoanService taiKhoanService; // Dịch vụ cho TaiKhoan

    @Autowired
    private SanPhamService sanPhamService; // Dịch vụ cho SanPham

    @Autowired
    private JwtUtil jwtUtil; // Dịch vụ xử lý JWT

    // API lấy danh sách sản phẩm yêu thích của người dùng
    @GetMapping
    public ResponseEntity<?> getYeuthichByUser(@RequestHeader("Authorization") String token) {
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
                    List<YeuThich> yeuthichList = yeuThichDAO.findByNguoiDung_MaNguoiDung(maNguoiDung);
                    System.out.println("Danh sách yêu thích của " + maNguoiDung + ": " + yeuthichList);

                    return ResponseEntity.ok(yeuthichList);
                }
                return ResponseEntity.status(403).body("Tài khoản không tồn tại.");
            }
            return ResponseEntity.status(401).body("Token không hợp lệ.");
        }
        return ResponseEntity.status(400).body("Token không được cung cấp.");
    }


    // API thêm sản phẩm vào danh sách yêu thích
    // API thêm sản phẩm vào danh sách yêu thích
    @PostMapping("/add")
    public ResponseEntity<?> addYeuthich(
            @RequestHeader("Authorization") String token,
            @RequestParam("maSanPham") Integer maSanPham) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ "Bearer "
            String username = jwtUtil.extractUsername(token);
            if (jwtUtil.validateToken(token, username)) {
                var taiKhoan = taiKhoanService.findByTaikhoan(username);
                if (taiKhoan != null) {
                    String maNguoiDung = taiKhoan.getManguoidung();  // Giả sử người dùng có mã phân phối

                    // Lấy thông tin sản phẩm
                    var sanPham = sanPhamService.findByMaSanPham(maSanPham);
                    if (sanPham == null) {
                        return ResponseEntity.status(404).body("Sản phẩm không tồn tại.");
                    }

                    // Tạo đối tượng Yeuthich và lưu vào cơ sở dữ liệu
                    YeuThich yeuthich = new YeuThich();
                    yeuthich.setNguoiDung(taiKhoan.getNguoiDung()); // Lấy đối tượng NguoiDung từ TaiKhoan
                    yeuthich.setSanPham(sanPham);  // Sử dụng đối tượng SanPham tìm được
                    yeuThichDAO.save(yeuthich);  // Lưu sản phẩm yêu thích vào DB

                    return ResponseEntity.ok("Sản phẩm đã được thêm vào danh sách yêu thích.");
                }
                return ResponseEntity.status(403).body("Tài khoản không tồn tại.");
            }
            return ResponseEntity.status(401).body("Token không hợp lệ.");
        }
        return ResponseEntity.status(400).body("Token không được cung cấp.");
    }

    // API xóa sản phẩm khỏi danh sách yêu thích
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeYeuthich(
            @RequestHeader("Authorization") String token,
            @RequestParam("maSanPham") Integer maSanPham) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ "Bearer "
            String username = jwtUtil.extractUsername(token);



            if (jwtUtil.validateToken(token, username)) {
                var taiKhoan = taiKhoanService.findByTaikhoan(username);
                if (taiKhoan != null) {
                    String maNguoiDung = taiKhoan.getManguoidung();  // Lấy mã người dùng
                    System.out.println("Người dùng đang đăng nhập: " + username);
                    System.out.println("Mã người dùng: " + maNguoiDung);
                    // Gọi hàm xóa sản phẩm yêu thích từ DAO
                    yeuThichDAO.deleteBySanPhamAndNguoiDung(maSanPham, maNguoiDung);

                    return ResponseEntity.ok("Sản phẩm đã được xóa khỏi danh sách yêu thích.");
                }
                return ResponseEntity.status(403).body("Tài khoản không tồn tại.");
            }
            return ResponseEntity.status(401).body("Token không hợp lệ.");
        }
        return ResponseEntity.status(400).body("Token không được cung cấp.");
    }

}
