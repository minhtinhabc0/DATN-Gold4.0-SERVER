package Gold40.DAO;

import Gold40.Entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NguoiDungDAO extends JpaRepository<NguoiDung, Integer> {
    boolean existsByMaNguoiDung(String maNguoiDung);
    boolean existsByEmail(String email);

    NguoiDung findByMaNguoiDung(String maNguoiDung);

}
