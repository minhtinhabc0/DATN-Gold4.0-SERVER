package Gold40.DAO;

import Gold40.Entity.Gcoin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GcoinDAO extends JpaRepository<Gcoin, String> {
    Gcoin findByMagcoin(String magcoin); // Đổi tên phương thức cho đúng với tên trường trong entity
}
