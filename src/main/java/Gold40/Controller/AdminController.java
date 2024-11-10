package Gold40.Controller;

import Gold40.DAO.NhaPhanPhoiDAO;
import Gold40.Entity.TaiKhoan;
import Gold40.Entity.NhaPhanPhoi;
import Gold40.Service.TaiKhoanService;
import Gold40.Service.NhaPhanPhoiService;
import Gold40.Service.AdminService;
import Gold40.Util.JwtUtil;  // Thêm JwtUtil để kiểm tra quyền admin
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private NhaPhanPhoiService nhaPhanPhoiService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private NhaPhanPhoiDAO nhaPhanPhoiDAO;

    @Autowired
    private JwtUtil jwtUtil;  // Sử dụng JwtUtil để kiểm tra quyền admin

    // Endpoint tạo tài khoản nhà phân phối
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create-distributor")
    public ResponseEntity<?> createDistributorAccount(@RequestBody TaiKhoan taiKhoan, @RequestHeader("Authorization") String authorizationHeader) {
        // Lấy token từ header Authorization
        String token = authorizationHeader.replace("Bearer ", "");

        // Kiểm tra token hợp lệ và lấy username từ token
        String username = jwtUtil.extractUsername(token);  // Extract username from token
        if (!jwtUtil.validateToken(token, username)) {
            System.out.println("Token không hợp lệ hoặc đã hết hạn.");
            return ResponseEntity.status(403).body("Token không hợp lệ hoặc đã hết hạn.");
        }

        // Kiểm tra nếu người dùng hiện tại có vai trò admin
        String role = jwtUtil.extractRole(token);
        if (!"ROLE_ADMIN".equals(role)) {
            System.out.println("Chỉ Admin mới có quyền tạo tài khoản nhà phân phối.");
            return ResponseEntity.status(402).body("Chỉ Admin mới có quyền tạo tài khoản nhà phân phối.");
        }

        // Tạo mới tài khoản nhà phân phối
        TaiKhoan newTaiKhoan = new TaiKhoan();
        newTaiKhoan.setTaikhoan(taiKhoan.getTaikhoan());
        newTaiKhoan.setMatkhau(taiKhoan.getMatkhau());
        newTaiKhoan.setVaitro(2);  // Vai trò nhà phân phối (mã vai trò = 2)
        newTaiKhoan.setMaadmin(taiKhoan.getMaadmin()); // Maadmin để xác định Admin đã tạo

        // Tạo nhà phân phối
        NhaPhanPhoi nhaPhanPhoi = new NhaPhanPhoi();
        nhaPhanPhoi.setMaNhaPhanPhoi(generateDistributorCode());  // Mã nhà phân phối ngẫu nhiên
        nhaPhanPhoi.setEmail(taiKhoan.getNhaPhanPhoi().getEmail());
        nhaPhanPhoi.setSdt(taiKhoan.getNhaPhanPhoi().getSdt());
        nhaPhanPhoi.setDiaChi(taiKhoan.getNhaPhanPhoi().getDiaChi());
        nhaPhanPhoi.setTenCuaHang(taiKhoan.getNhaPhanPhoi().getTenCuaHang());

        // Lưu nhà phân phối vào cơ sở dữ liệu
        nhaPhanPhoiService.saveNhaPhanPhoi(nhaPhanPhoi);

        // Cập nhật thông tin nhà phân phối vào tài khoản
        newTaiKhoan.setManhaphanphoi(nhaPhanPhoi.getMaNhaPhanPhoi());

        // Lưu tài khoản nhà phân phối vào cơ sở dữ liệu
        taiKhoanService.save(newTaiKhoan);

        return ResponseEntity.ok("Tạo tài khoản nhà phân phối thành công.");
    }

    // Tạo mã nhà phân phối ngẫu nhiên 10 ký tự và kiểm tra tính duy nhất
    public String generateDistributorCode() {
        String characters = "0123456789";
        StringBuilder result;
        String newId;

        // Lặp lại cho đến khi tạo được mã duy nhất
        do {
            result = new StringBuilder();
            Random random = new Random();

            // Tạo chuỗi ngẫu nhiên 10 ký tự từ các số
            for (int i = 0; i < 10; i++) {
                int index = random.nextInt(characters.length());
                result.append(characters.charAt(index));
            }

            newId = "NP" + result.toString();  // Thêm tiền tố "NP" cho mã nhà phân phối
        } while (nhaPhanPhoiDAO.existsById(Integer.valueOf(newId)));  // Kiểm tra mã nhà phân phối đã tồn tại chưa

        return newId;
    }
}
