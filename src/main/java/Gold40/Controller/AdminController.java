package Gold40.Controller;

import Gold40.DAO.NhaPhanPhoiDAO;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.NhaPhanPhoiService;
import Gold40.Service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/adctrl")
public class AdminController {

    @Autowired
    private NhaPhanPhoiService nhaPhanPhoiService;

    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private NhaPhanPhoiDAO nhaPhanPhoiDAO;

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





                return ResponseEntity.ok("Hồ sơ đã bị từ chối và thông tin liên quan đã được xóa.");
            } else {
                return ResponseEntity.badRequest().body("Tài khoản không phải là nhà phân phối chờ duyệt.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("unlock/{id}")
    public ResponseEntity<String> unlockDistributorAccount(@PathVariable String id) {
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanService.findById(id);
        if (taiKhoanOpt.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            if (taiKhoan.getVaitro() == 5) { // Ensure it's a locked distributor (role 5)
                taiKhoan.setVaitro(2); // Chuyển vai trò thành 2 (đã duyệt)
                taiKhoanService.save(taiKhoan);
                return ResponseEntity.ok("Tài khoản đã được khóa.");
            } else {
                return ResponseEntity.badRequest().body("Tài khoản không phải là nhà phân phối khóa.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
