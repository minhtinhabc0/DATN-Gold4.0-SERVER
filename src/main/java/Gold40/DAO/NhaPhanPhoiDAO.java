package Gold40.DAO;

import Gold40.Entity.NhaPhanPhoi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhaPhanPhoiDAO extends JpaRepository<NhaPhanPhoi, String> {
    boolean existsByEmail(String email);


    boolean existsByMaNhaPhanPhoi(String maNhaPhanPhoi);
}
