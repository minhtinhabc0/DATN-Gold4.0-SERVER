package Gold40.DAO;

import Gold40.Entity.VangMieng;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VangMiengDAO extends JpaRepository<VangMieng, String> {
    @Query("SELECT v FROM VangMieng v WHERE v.maVang = :maVang")
    Optional<VangMieng> findByMaVang(@Param("maVang") String maVang);

}
