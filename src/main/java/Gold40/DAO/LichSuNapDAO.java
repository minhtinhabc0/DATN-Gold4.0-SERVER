package Gold40.DAO;

import Gold40.Entity.LichSuNap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LichSuNapDAO extends JpaRepository<LichSuNap, Integer> {
    Optional<LichSuNap> findByOrderCode(long orderCode);
}
