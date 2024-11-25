package Gold40.DAO;

import Gold40.Entity.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DanhGiaDAO extends JpaRepository<DanhGia, String> {
    List<DanhGia> findBySanPhamMaSanPham(Integer sanPhamId);

    @Query("SELECT d FROM DanhGia d WHERE d.sanPham.maSanPham = :maSanPham")
    List<DanhGia> getDanhGiaBySanPham(@Param("maSanPham") Integer maSanPham);


}
