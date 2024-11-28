package Gold40.Controller;

import Gold40.DAO.DonHangDAO;
import Gold40.DAO.NhaPhanPhoiDAO;

import Gold40.DAO.ProductsDAO;
import Gold40.Entity.DonHang;
import Gold40.Entity.SanPham;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.DonHangService;
import Gold40.Service.EmailService;
import Gold40.Service.SanPhamService;
import Gold40.Service.TaiKhoanService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/adctrl")
public class AdminController {

    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private NhaPhanPhoiDAO nhaPhanPhoiDAO;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ProductsDAO productsDAO;
    @Autowired
    private DonHangDAO donHangDAO;
    @Autowired
    private DonHangService donHangService;
    @Autowired
    private SanPhamService sanPhamService;


    // Hiển thị danh sách nhà phân phối với vai trò 2 (đã duyệt)
    @GetMapping("/approved")
    public ResponseEntity<List<TaiKhoan>> getApprovedDistributors() {
        // Fetching all distributors with role 2 (approved)
        List<TaiKhoan> approvedDistributors = taiKhoanService.findByVaitro(2); // Vai trò 2 - đã duyệt
        if (approvedDistributors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(approvedDistributors);
    }

    @GetMapping("/ngdung")
    public ResponseEntity<List<TaiKhoan>> getDistributors() {
        // Fetching all distributors with role 2 (approved)
        List<TaiKhoan> UserAccount = taiKhoanService.findByVaitro(0); // Vai trò 2 - đã duyệt
        if (UserAccount.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(UserAccount);
    }

    @GetMapping("/ngdungband")
    public ResponseEntity<List<TaiKhoan>> getuserbandDistributors() {
        // Fetching all distributors with role 2 (approved)
        List<TaiKhoan> UserAccountb = taiKhoanService.findByVaitro(6); // Vai trò 2 - đã duyệt
        if (UserAccountb.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(UserAccountb);
    }

    @PostMapping("/ngdungband/{id}")
    public ResponseEntity<String> lockUserAccount(@PathVariable String id) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanService.findById(id);
        if (taiKhoanOpt.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            if (taiKhoan.getVaitro() == 0) {
                taiKhoan.setVaitro(6);
                taiKhoanService.save(taiKhoan);
                try {
                    String emailBody = "<h2>Tài khoản " + taiKhoan.getTaikhoan() + " của bạn đã bị khóa</h2><p>Chúng tôi thông báo rằng tài khoản của bạn đã bị khóa do vi phạm quy định.</p>";
                    emailService.sendEmail(nhaPhanPhoiDAO.findById(taiKhoan.getManhaphanphoi()).get().getEmail(), "Thông báo khóa tài khoản", emailBody, true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return ResponseEntity.ok("Tài khoản nguoidung đã được khóa.");
            } else {
                return ResponseEntity.badRequest().body("Tài khoản bi khoa chua?.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/ngdungunband/{id}")
    public ResponseEntity<String> unlockUserAccount(@PathVariable String id) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanService.findById(id);
        if (taiKhoanOpt.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            if (taiKhoan.getVaitro() == 6) {
                taiKhoan.setVaitro(0);
                taiKhoanService.save(taiKhoan);
                try {
                    String emailBody = "<h2>Tài khoản " + taiKhoan.getTaikhoan() + " của bạn đã được mở khóa</h2><p>Tài khoản của bạn đã được mở khóa bây giờ bạn có thể sử dụng tài khoản để có thể giao dịch Gcoin.</p>";
                    emailService.sendEmail(nhaPhanPhoiDAO.findById(taiKhoan.getManhaphanphoi()).get().getEmail(), "Thông báo khóa tài khoản", emailBody, true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return ResponseEntity.ok("Tài khoản nguoidung đã được khóa.");
            } else {
                return ResponseEntity.badRequest().body("Tài khoản bi khoa chua?.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Hiển thị danh sách nhà phân phối với vai trò 4 (chờ duyệt)
    @GetMapping("/pending")
    public ResponseEntity<List<TaiKhoan>> getPendingDistributors() {
        // Fetching all distributors with role 4 (pending)
        List<TaiKhoan> pendingDistributors = taiKhoanService.findByVaitro(4); // Vai trò 4 - chờ duyệt
        if (pendingDistributors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pendingDistributors);
    }

    @GetMapping("/locked")
    public ResponseEntity<List<TaiKhoan>> getLockedDistributors() {
        // Fetching all distributors with role 5 (locked)
        List<TaiKhoan> lockedDistributors = taiKhoanService.findByVaitro(5); // Vai trò 5 - khóa tài khoản
        if (lockedDistributors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lockedDistributors);
    }

    // Khóa tài khoản nhà phân phối (chuyển vai trò 2 thành vai trò 5)
    @PostMapping("/lock/{id}")
    public ResponseEntity<String> lockDistributorAccount(@PathVariable String id) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanService.findById(id);
        if (taiKhoanOpt.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            if (taiKhoan.getVaitro() == 2) { // Ensure it's an approved distributor (role 2)
                taiKhoan.setVaitro(5); // Chuyển vai trò thành 5 (khóa tài khoản)
                taiKhoanService.save(taiKhoan);

                // Gửi email thông báo tài khoản đã bị khóa
                try {
                    String emailBody = "<h2>Tài khoản của bạn đã bị khóa</h2><p>Chúng tôi thông báo rằng tài khoản của bạn đã bị khóa do vi phạm quy định.</p>";
                    emailService.sendEmail(nhaPhanPhoiDAO.findById(taiKhoan.getManhaphanphoi()).get().getEmail(), "Thông báo khóa tài khoản", emailBody, true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                return ResponseEntity.ok("Tài khoản đã bị khóa.");
            } else {
                return ResponseEntity.badRequest().body("Tài khoản không phải là nhà phân phối đã duyệt.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    // Duyệt hồ sơ nhà phân phối (chuyển vai trò 4 thành vai trò 2)
    @PostMapping("/approve/{id}")
    public ResponseEntity<String> approveDistributor(@PathVariable String id) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanService.findById(id);
        if (taiKhoanOpt.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            if (taiKhoan.getVaitro() == 4) { // Ensure it's a pending distributor (role 4)
                taiKhoan.setVaitro(2); // Chuyển vai trò thành 2 (đã duyệt)
                taiKhoanService.save(taiKhoan);
                try {
                    String emailBody = "<h2>chúc mừng bạn đã đăng ký hợp tác với chúng tôi thành công</h2><p>bây giờ bạn có thể tới trang đăng nhập của nhà phân phối để có thể truy cập và tạo các sản phẩm của bạn </p>";
                    emailService.sendEmail(nhaPhanPhoiDAO.findById(taiKhoan.getManhaphanphoi()).get().getEmail(), "Duyệt hồ sơ", emailBody, true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return ResponseEntity.ok("Hồ sơ đã được duyệt.");
            } else {
                return ResponseEntity.badRequest().body("Tài khoản không phải là nhà phân phối chờ duyệt.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    // Từ chối hồ sơ nhà phân phối (xóa tài khoản)
    @PostMapping("/reject/{id}")
    public ResponseEntity<String> rejectDistributor(@PathVariable String id) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanService.findById(id);
        if (taiKhoanOpt.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            if (taiKhoan.getVaitro() == 4) { // Ensure it's a pending distributor (role 4)
                // Xóa thông tin của nhà phân phối (nếu có liên kết với bảng khác)// Xóa thông tin trong bảng NhaPhanPhoi
                taiKhoanService.delete(taiKhoan);
                nhaPhanPhoiDAO.deleteById(taiKhoan.getManhaphanphoi());

                try {
                    String emailBody = "<h2>Tài khoản của bạn đã bị từ chối</h2><p>chúng tôi rất tiêc khi gửi thông báo này nhưng có vẻ như thông tin bạn cung cấp cho chúng tôi không đúng hoặc không đủ yêu cầu để có thể duyệt hồ sơ bạn có thể tạo lại hồ sơ</p>";
                    emailService.sendEmail(nhaPhanPhoiDAO.findById(taiKhoan.getManhaphanphoi()).get().getEmail(), "Mong rằng chúng ta có thể hợp tác với nhau trong thời gian tới", emailBody, true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }


                return ResponseEntity.ok("Hồ sơ đã bị từ chối và thông tin liên quan đã được xóa.");
            } else {
                return ResponseEntity.badRequest().body("Tài khoản không phải là nhà phân phối chờ duyệt.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/unlock/{id}")
    public ResponseEntity<String> unlockDistributorAccount(@PathVariable String id) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanService.findById(id);
        if (taiKhoanOpt.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            if (taiKhoan.getVaitro() == 5) { // Ensure it's a locked distributor (role 5)
                taiKhoan.setVaitro(2); // Chuyển vai trò thành 2 (đã duyệt)
                taiKhoanService.save(taiKhoan);

                // Gửi email thông báo tài khoản đã được mở khóa
                try {
                    String emailBody = "<h2>Tài khoản của bạn đã được mở khóa</h2><p>Chúng tôi vui mừng thông báo rằng tài khoản của bạn đã được mở khóa. Bạn có thể tiếp tục sử dụng tài khoản.</p>";
                    emailService.sendEmail(nhaPhanPhoiDAO.findById(taiKhoan.getManhaphanphoi()).get().getEmail(), "Thông báo mở khóa tài khoản", emailBody, true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                return ResponseEntity.ok("Tài khoản đã được mở khóa.");
            } else {
                return ResponseEntity.badRequest().body("Tài khoản không phải là nhà phân phối khóa.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    //quan ly san pham ===============================


    @GetMapping("/productsall")
    public List<SanPham> getAllsp(Model model) {
        return productsDAO.findAll();
    }

    @GetMapping("/products/{id}")
    public SanPham getOne(@PathVariable("id") Integer id) {
        return productsDAO.findById(id).get();
    }

    @PutMapping("/duyetsp/{id}")
    public ResponseEntity<String> duyetSanPham(@PathVariable Integer id) {
        Optional<SanPham> sanPhamOpt = productsDAO.findById(id);
        if (sanPhamOpt.isPresent()) {
            SanPham sanPham = sanPhamOpt.get();
            sanPham.setTrangThai(true);
            productsDAO.save(sanPham);
            return ResponseEntity.ok("Duyết thanh cong");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/kduyetsp/{id}")
    public ResponseEntity<String> kduyetSanPham(@PathVariable Integer id) {
        Optional<SanPham> sanPhamOpt = productsDAO.findById(id);
        if (sanPhamOpt.isPresent()) {
            SanPham sanPham = sanPhamOpt.get();
            sanPham.setTrangThai(false);
            productsDAO.save(sanPham);
            return ResponseEntity.ok("Duyết thanh cong");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Integer id) {
        Optional<SanPham> sanPhamOpt = productsDAO.findById(id);
        if (sanPhamOpt.isPresent()) {
            SanPham sanPham = sanPhamOpt.get();
            productsDAO.delete(sanPham);
            return ResponseEntity.ok("Xóa thanh cong");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // quan ly don hang
    @GetMapping("/donhangall")
    public List<DonHang> getAlldh(Model model) {
        return donHangDAO.findAll();
    }

    @GetMapping("/spbanchay")
    public ResponseEntity<List<Object[]>> a() {
        List<Object[]> productsselling = donHangDAO.getFrequentlyOrderedProductNamesWithCount();
        if (productsselling.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productsselling);
    }

    @GetMapping("/doiduyet")
    public ResponseEntity<List<SanPham>> spdoiduyet() {
        // Fetching all distributors with role 2 (approved)
        List<SanPham> spdoiduyet = sanPhamService.findSanPhamByTrangThaiFalse();
        if (spdoiduyet.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(spdoiduyet);
    }
}


