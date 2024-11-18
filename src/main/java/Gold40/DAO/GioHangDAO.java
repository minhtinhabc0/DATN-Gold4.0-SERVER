package Gold40.DAO;

import Gold40.Entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GioHangDAO extends JpaRepository<GioHang, String> {
    static Object findByMaNguoiDung(String maNguoiDung) {
        return null;
    }
}
