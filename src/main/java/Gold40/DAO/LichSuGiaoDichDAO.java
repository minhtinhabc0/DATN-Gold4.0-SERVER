package Gold40.DAO;

import Gold40.Entity.LichSuGiaoDich;
import Gold40.Entity.VangMieng;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LichSuGiaoDichDAO extends JpaRepository<LichSuGiaoDich, String> {
    List<LichSuGiaoDich> findAllByNguoiDung_MaNguoiDung(String maNguoiDung);
    LichSuGiaoDich findByMaVang(VangMieng maVang);
}
