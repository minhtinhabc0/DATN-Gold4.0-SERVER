package Gold40.DAO;

import Gold40.Entity.VangMieng;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VangMiengDAO extends JpaRepository<VangMieng, String> {
    Optional<VangMieng> findByMaVang(String maVang);
}
