package Gold40.Controller;


import Gold40.DAO.HoaDonDAO;
import Gold40.Entity.HoaDon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/hoadon")
public class HoaDonController {

    @Autowired
    private HoaDonDAO hoaDonDAO;

    // Lấy danh sách đơn hàng


    // Lấy chi tiết đơn hàng theo mã đơn hàng
    @GetMapping("/{maHoaDon}")
    public ResponseEntity<HoaDon> getDonHangById(@PathVariable("maHoaDon") Integer maHoaDon) {
        Optional<HoaDon> hoaDon = hoaDonDAO.findById(maHoaDon);
        return hoaDon.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }



}
