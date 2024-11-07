package Gold40.DAO;

import Gold40.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface AdminDAO extends JpaRepository<Admin, String> {
    Optional<Admin> findByMaAdmin(String maAdmin);
    boolean existsByEmail(String email);
    boolean existsByMaAdmin(String maAdmin);
}
