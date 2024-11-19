package Gold40.Controller;

import Gold40.DAO.ProductsDAO;
import Gold40.Entity.SanPham;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.EmailService;
import Gold40.Service.NhaPhanPhoiService;
import Gold40.Service.SanPhamService;
import Gold40.Service.TaiKhoanService;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static com.cloudinary.AccessControlRule.AccessType.token;

@RestController
@RequestMapping("/api/nppctrl")
public class NPPController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private NhaPhanPhoiService nhaPhanPhoiService;

    @Autowired
    private ProductsDAO productsDAO;

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private EmailService mailService;

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
}
