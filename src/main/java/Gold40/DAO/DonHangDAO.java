package Gold40.DAO;

import Gold40.Entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonHangDAO extends JpaRepository<DonHang, String> {
    long countByTrangThai(String trangThai);
}
