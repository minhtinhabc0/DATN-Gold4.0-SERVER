package Gold40.Controller;


import Gold40.DAO.ProductsDAO;
import Gold40.Entity.*;
import Gold40.Service.*;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/nppctrl")
public class NPPController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private ProductsDAO productsDAO;
    @Autowired
    private VangMiengService vangMiengService;
    @Autowired
    private LichSuGiaoDichService lichSuGiaoDichService;
//    @Autowired
//    private NhaPhanPhoiService nhaPhanPhoiService;
    @Autowired
    private LichSuGiaoDichNPPService lichSuGiaoDichNPPService;
    private String extractToken(String token) {
        // Return null if token is invalid or doesn't start with "Bearer "
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
    }

    // This method handles authorization and product retrieval based on the distributor role
    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR')")
    @GetMapping("/getsp")
    public ResponseEntity<List<SanPham>> getProductsByNPP(@RequestHeader("Authorization") String token) {
        String extractedToken = extractToken(token);

        if (extractedToken == null) {
            return ResponseEntity.badRequest().body(null);  // Returning a 400 Bad Request if the token is invalid
        }

        String username = jwtUtil.extractUsername(extractedToken);
        TaiKhoan taiKhoan = taiKhoanService.findByTaikhoan(username);

        if (taiKhoan == null || taiKhoan.getNhaPhanPhoi() == null) {
            return ResponseEntity.status(404).body(null); // Not Found if no distributor is linked
        }

        String maNhaPhanPhoi = taiKhoan.getNhaPhanPhoi().getMaNhaPhanPhoi();
        if (maNhaPhanPhoi == null) {
            return ResponseEntity.badRequest().body(null); // Invalid distributor ID
        }

        List<SanPham> sanPhams = productsDAO.findByNhaPhanPhoi_MaNhaPhanPhoi(maNhaPhanPhoi);
        if (sanPhams.isEmpty()) {
            return ResponseEntity.noContent().build();  // No content found for the distributor
        }

        return ResponseEntity.ok(sanPhams);  // Successfully return the list of products
    }

    // Method to generate a random product code (masanpham)
    private int generateRandomProductCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;  // Generate a 6-digit number between 100000 and 999999
    }

    // This method handles adding a new product
    @PostMapping("/add-product")
    public ResponseEntity<SanPham> addProduct(@RequestBody SanPham newProduct,@RequestHeader("Authorization") String token) {
        String extractedToken = extractToken(token);

        if (extractedToken == null) {
            return ResponseEntity.badRequest().body(null);  // Returning a 400 Bad Request if the token is invalid
        }

        String username = jwtUtil.extractUsername(extractedToken);
        TaiKhoan taiKhoan = taiKhoanService.findByTaikhoan(username);

        if (taiKhoan == null || taiKhoan.getNhaPhanPhoi() == null) {
            return ResponseEntity.status(404).body(null); // Not Found if no distributor is linked
        }

        if (newProduct == null || newProduct.getTenSanPham() == null || newProduct.getTenSanPham().isEmpty()) {
            return ResponseEntity.badRequest().body(null);  // Return 400 Bad Request if product is invalid
        }

        if (newProduct.getGia() == null || newProduct.getGia().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(null);  // Return 400 Bad Request if price is invalid
        }
        // Generate a random masanpham and set it
        int randomProductCode = generateRandomProductCode();
        newProduct.setMaSanPham(randomProductCode);

        // Set default product status as inactive (false)
        newProduct.setTrangThai(false);
        newProduct.setNhaPhanPhoi(taiKhoan.getNhaPhanPhoi());

        // Save the product to the database
        SanPham savedProduct = productsDAO.save(newProduct);
        return ResponseEntity.status(201).body(savedProduct);  // Return 201 Created with the saved product
    }
    // API sửa sản phẩm
    @PutMapping("/update-product/{id}")
    public ResponseEntity<SanPham> updateProduct(
            @PathVariable("id") int productId,
            @RequestBody SanPham updatedProduct,
            @RequestHeader("Authorization") String token) {

        String extractedToken = extractToken(token);
        if (extractedToken == null) {
            return ResponseEntity.badRequest().body(null); // Token không hợp lệ
        }

        String username = jwtUtil.extractUsername(extractedToken);
        TaiKhoan taiKhoan = taiKhoanService.findByTaikhoan(username);

        if (taiKhoan == null || taiKhoan.getNhaPhanPhoi() == null) {
            return ResponseEntity.status(404).body(null); // Không tìm thấy tài khoản hoặc nhà phân phối
        }

        SanPham existingProduct = productsDAO.findById(productId).orElse(null);
        if (existingProduct == null || !existingProduct.getNhaPhanPhoi().getMaNhaPhanPhoi().equals(taiKhoan.getNhaPhanPhoi().getMaNhaPhanPhoi())) {
            return ResponseEntity.status(403).body(null); // Không có quyền sửa sản phẩm
        }
        System.out.println(existingProduct);
        // Cập nhật thông tin sản phẩm
        existingProduct.setTenSanPham(updatedProduct.getTenSanPham());
        existingProduct.setGia(updatedProduct.getGia());
        existingProduct.setChiTiet(updatedProduct.getChiTiet());
        existingProduct.setLoai(updatedProduct.getLoai());
        existingProduct.setHinhAnh(updatedProduct.getHinhAnh());
        existingProduct.setKichCo(updatedProduct.getKichCo());
        existingProduct.setLoaiVang(updatedProduct.getLoaiVang());
        existingProduct.setTrongLuong(updatedProduct.getTrongLuong());
        existingProduct.setLoaiDa(updatedProduct.getLoaiDa());
        existingProduct.setSoLuong(updatedProduct.getSoLuong());
        existingProduct.setTienCong(updatedProduct.getTienCong());
        existingProduct.setTrangThai(false);

        SanPham updated = productsDAO.save(existingProduct);
        return ResponseEntity.ok(updated); // Trả về sản phẩm đã sửa
    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable("id") int productId,
            @RequestHeader("Authorization") String token) {

        String extractedToken = extractToken(token);

        if (extractedToken == null) {
            return ResponseEntity.badRequest().body("Token không hợp lệ");
        }

        String username = jwtUtil.extractUsername(extractedToken);
        TaiKhoan taiKhoan = taiKhoanService.findByTaikhoan(username);

        if (taiKhoan == null || taiKhoan.getNhaPhanPhoi() == null) {
            return ResponseEntity.status(404).body("Không tìm thấy nhà phân phối");
        }

        // Lấy sản phẩm từ cơ sở dữ liệu
        SanPham existingProduct = productsDAO.findById(productId).orElse(null);
        if (existingProduct == null) {
            return ResponseEntity.status(404).body("Sản phẩm không tồn tại");
        }

        // Kiểm tra quyền của nhà phân phối, chỉ có thể xóa sản phẩm của chính mình
        if (!existingProduct.getNhaPhanPhoi().getMaNhaPhanPhoi().equals(taiKhoan.getNhaPhanPhoi().getMaNhaPhanPhoi())) {
            return ResponseEntity.status(403).body("Không có quyền xóa sản phẩm này");
        }

        // Xóa sản phẩm
        productsDAO.delete(existingProduct);
        return ResponseEntity.ok("Sản phẩm đã được xóa thành công");
    }
    @PreAuthorize("hasRole('ROLE_DISTRIBUTOR')")
    @PostMapping("/confirm-gold/{maVang}")
    public ResponseEntity<?> confirmGold(@RequestHeader("Authorization") String token, @PathVariable String maVang) {
        // Tách token và kiểm tra tính hợp lệ
        String extractedToken = extractToken(token);
        if (extractedToken == null) {
            return ResponseEntity.badRequest().body("Token không hợp lệ");
        }

        // Lấy tài khoản từ token
        String username = jwtUtil.extractUsername(extractedToken);
        TaiKhoan taiKhoan = taiKhoanService.findByTaikhoan(username);
        if (taiKhoan == null || taiKhoan.getNhaPhanPhoi() == null) {
            return ResponseEntity.status(403).body("Không tìm thấy nhà phân phối");
        }

        // Lấy thông tin nhà phân phối từ tài khoản
        NhaPhanPhoi nhaPhanPhoi = taiKhoan.getNhaPhanPhoi();

        // Kiểm tra mã vàng
        VangMieng vangMieng = vangMiengService.findById(maVang);
        if (vangMieng == null) {
            return ResponseEntity.status(404).body("Mã vàng không tồn tại");
        }


        // Nếu mã vàng đã có nhà phân phối
        if (vangMieng.getMaNhaPhanPhoi() != null) {
            saveLichSuGiaoDichNPP(maVang, nhaPhanPhoi, "Thất bại: Mã vàng đã được sử dụng");
            return ResponseEntity.badRequest().body("Mã vàng đã được sử dụng");
        }

        // Cập nhật mã nhà phân phối cho mã vàng
        vangMieng.setMaNhaPhanPhoi(nhaPhanPhoi);
        vangMiengService.save(vangMieng);

        // Cập nhật trạng thái trong lịch sử giao dịch
        LichSuGiaoDich lichSu = lichSuGiaoDichService.findByMaVang(vangMieng);
        if (lichSu != null) {
            lichSu.setTrangThai("Đã sử dụng");
            lichSuGiaoDichService.save(lichSu);
        }

        // Lưu lịch sử giao dịch thành công
        saveLichSuGiaoDichNPP(maVang, nhaPhanPhoi, "Thành công");

        return ResponseEntity.ok("Xác nhận mã vàng thành công");
    }

    private void saveLichSuGiaoDichNPP(String maVang, NhaPhanPhoi nhaPhanPhoi, String trangThai) {
        LichSuGiaoDichNPP lichSuGiaoDichNPP = new LichSuGiaoDichNPP();
        lichSuGiaoDichNPP.setMaLsgdnpp("LSGDNPP" + System.currentTimeMillis());
        lichSuGiaoDichNPP.setThoiGian(LocalDateTime.now());
        lichSuGiaoDichNPP.setTrangThai(trangThai);

        VangMieng vangMieng = vangMiengService.findById(maVang);
        lichSuGiaoDichNPP.setMaVang(vangMieng);
        lichSuGiaoDichNPP.setMaNhaPhanPhoi(nhaPhanPhoi);

        lichSuGiaoDichNPPService.save(lichSuGiaoDichNPP);
    }
}
