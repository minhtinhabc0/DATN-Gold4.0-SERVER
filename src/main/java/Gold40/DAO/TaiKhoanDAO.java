package Gold40.DAO;

import Gold40.Entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaiKhoanDAO extends JpaRepository<TaiKhoan, Long> {
    Optional<TaiKhoan> findByTaikhoan(String taikhoan);

    boolean existsByTaikhoan(String taikhoan);



}
