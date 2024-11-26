package Gold40.DAO;

import Gold40.Entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HoaDonDAO extends JpaRepository<HoaDon, Integer> {
    @Modifying
    @Query("UPDATE HoaDon h SET h.tongSoLuong = :tongSoLuong, h.tongTien = :tongTien WHERE h.orderCode = :orderCode")
    void updateTongSoLuongAndTongTien(@Param("orderCode") long orderCode,
                                      @Param("tongSoLuong") int tongSoLuong,
                                      @Param("tongTien") float tongTien);
    HoaDon findByOrderCode(long orderCode);

    @Query("SELECT h FROM HoaDon h WHERE h.maNguoiDung = :maNguoiDung")
    List<HoaDon> findByMaNguoiDung(@Param("maNguoiDung") String maNguoiDung);
}
