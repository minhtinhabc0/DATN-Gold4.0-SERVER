package Gold40.Controller;

import Gold40.DAO.DonHangDAO;
import Gold40.Entity.DonHang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/donhang")
public class DonHangController {

    @Autowired
    private DonHangDAO donHangDao;

    // Lấy danh sách đơn hàng
    @GetMapping
    public List<DonHang> getDonHang() {
        return donHangDao.findAll();

    }

    // Lấy chi tiết đơn hàng theo mã đơn hàng
    @GetMapping("/{maDonHang}")
    public ResponseEntity<DonHang> getDonHangById(@PathVariable("maDonHang") String maDonHang) {
        Optional<DonHang> donHang = donHangDao.findById(maDonHang);
        return donHang.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/demsl/hoanthanh")
    public long countCompletedOrders() {
        return donHangDao.countByTrangThai("Hoàn thành"); // Giả sử trạng thái 'COMPLETED'
    }


}
