package Gold40.DAO;

import Gold40.Entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface ProductsDAO extends JpaRepository<SanPham, Integer> {
}
