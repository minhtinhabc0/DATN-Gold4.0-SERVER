package Gold40.Controller;

import Gold40.Entity.TaiKhoan;
import Gold40.Entity.YeuThich;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import Gold40.Service.SanPhamService;
import Gold40.Service.TaiKhoanService;
import Gold40.DAO.YeuThichDAO;
import Gold40.Service.YeuThichService;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private YeuThichService yeuThichService;

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



    // Hàm tạo mã yêu thích ngẫu nhiên
    private Integer generateRandomMaYeuThich() {
        Random random = new Random();
        return random.nextInt(999999);  // Tạo một số ngẫu nhiên từ 0 đến 999999
    }
    @PostMapping("/add")
    public ResponseEntity<?> addYeuthich(
            @RequestHeader("Authorization") String token,
            @RequestParam("maSanPham") Integer maSanPham) {
        // In ra token và mã sản phẩm để kiểm tra
        System.out.println("Request received with token: " + token + ", maSanPham: " + maSanPham);

        // Kiểm tra xem token có được cung cấp và có đúng định dạng không
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Loại bỏ "Bearer " từ token
            String username = jwtUtil.extractUsername(token);  // Trích xuất username từ token

            // Kiểm tra tính hợp lệ của token
            if (jwtUtil.validateToken(token, username)) {
                // Tìm tài khoản người dùng từ tên đăng nhập
                var taiKhoan = taiKhoanService.findByTaikhoan(username);

                // Kiểm tra tài khoản người dùng
                if (taiKhoan != null) {
                    String maNguoiDung = taiKhoan.getManguoidung();  // Lấy mã người dùng từ tài khoản
                    System.out.println("Mã người dùng: " + maNguoiDung);

                    // Lấy thông tin sản phẩm từ mã sản phẩm
                    var sanPham = sanPhamService.findByMaSanPham(maSanPham);
                    if (sanPham == null) {
                        // Nếu sản phẩm không tồn tại, trả về lỗi 404
                        return ResponseEntity.status(404).body("Sản phẩm không tồn tại.");
                    }
                    System.out.println("Sản phẩm muốn thêm: " + sanPham);

                    // Tạo mã yêu thích ngẫu nhiên
                    Integer maYeuThich = generateRandomMaYeuThich(); // Gọi hàm tạo mã yêu thích ngẫu nhiên

                    // Tạo đối tượng YeuThich để lưu vào cơ sở dữ liệu
                    YeuThich yeuthich = new YeuThich();
                    yeuthich.setMaYeuThich(maYeuThich);  // Gán mã yêu thích ngẫu nhiên
                    yeuthich.setNguoiDung(taiKhoan.getNguoiDung()); // Lấy đối tượng NguoiDung từ TaiKhoan
                    yeuthich.setSanPham(sanPham);  // Sử dụng đối tượng SanPham tìm được
                    System.out.println("Saving favorite product: " + yeuthich);

                    // Lưu sản phẩm vào danh sách yêu thích
                    yeuThichService.addProductToFavorites(yeuthich);
// Tạo đối tượng DTO hoặc Map
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Sản phẩm đã được thêm vào danh sách yêu thích.");
                    response.put("status", "success");

                    return ResponseEntity.ok(response);

                    // Trả về thông báo thành công

                } // Nếu tài khoản không tồn tại
                System.out.println("Không tìm thấy tài khoản với username: " + username);
                return ResponseEntity.status(403).body("Tài khoản không tồn tại.");
            }
            // Nếu token không hợp lệ
            System.out.println("Token không hợp lệ cho username: " + username);
            return ResponseEntity.status(401).body("Token không hợp lệ.");
        }

        // Nếu không có token được cung cấp
        System.out.println("Token không được cung cấp.");
        return ResponseEntity.status(400).body("Token không được cung cấp.");
    }


    private String extractToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
    }
    // API xóa sản phẩm khỏi danh sách yêu thích
    @DeleteMapping("/{maSanPham}")
    public ResponseEntity<?> removeFromFavorites(
            @RequestHeader("Authorization") String token,
            @PathVariable("maSanPham") int maSanPham) {

        try {
            // Kiểm tra và trích xuất token từ header
            token = extractToken(token); // Trích xuất token từ header
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không có quyền truy cập");
            }

            // Trích xuất username từ token
            String taikhoan = jwtUtil.extractUsername(token);
            if (taikhoan == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }

            // Tìm tài khoản người dùng từ username
            TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
            }

            // Lấy mã người dùng từ tài khoản
            String maNguoiDung = user.getManguoidung();

            // Tìm sản phẩm trong danh sách yêu thích của người dùng
            YeuThich yeuThich = yeuThichDAO.findByNguoiDungMaNguoiDungAndSanPhamMaSanPham(maNguoiDung, maSanPham);
            if (yeuThich == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không có trong danh sách yêu thích");
            }

            // Kiểm tra quyền xóa: Chỉ người dùng sở hữu sản phẩm yêu thích mới được xóa
            if (!maNguoiDung.equals(yeuThich.getNguoiDung().getMaNguoiDung())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền xóa sản phẩm này.");
            }

            // Xóa sản phẩm khỏi danh sách yêu thích
            yeuThichDAO.delete(yeuThich);

            // Trả về thông báo thành công dưới dạng chuỗi đơn giản
            // Tạo đối tượng DTO hoặc Map
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Sản phẩm đã được xóa khỏi yêu thích");
            response.put("status", "success");

            return ResponseEntity.ok(response);


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xóa sản phẩm");
        }
    }


}