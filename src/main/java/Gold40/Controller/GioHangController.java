package Gold40.Controller;

import Gold40.DAO.GioHangDAO;
import Gold40.Entity.GioHang;
import Gold40.Entity.NguoiDung;
import Gold40.Entity.TaiKhoan;
import Gold40.Service.NguoiDungService;
import Gold40.Service.TaiKhoanService;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
public class GioHangController {
    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private JwtUtil jwtUtil;
    private String extractToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
    }
    @GetMapping("/giohang")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        token =extractToken(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền truy cập");
        }

        String taikhoan = jwtUtil.extractUsername(token);
        TaiKhoan user = taiKhoanService.findByTaikhoan(taikhoan);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(user.getManguoidung());
        if (nguoiDung == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thông tin người dùng không tồn tại");
        }
        Map<String, Object> gioHangUser = new HashMap<>();
        gioHangUser.put("giohang", GioHangDAO.findByMaNguoiDung(nguoiDung.getMaNguoiDung()));

        return ResponseEntity.ok(gioHangUser);
    }




}
