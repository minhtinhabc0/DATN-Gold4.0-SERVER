package Gold40.DAO;

import Gold40.Entity.GioHang;
import Gold40.Entity.NguoiDung;
import Gold40.Entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GioHangDAO extends JpaRepository<GioHang, Integer> {


    GioHang findByNguoiDung(NguoiDung byMaNguoiDung);


    public abstract GioHang findByNguoiDungAndSanPham_MaSanPham(NguoiDung nguoiDung, Integer maSanPham);


    GioHang findByNguoiDungMaNguoiDung(String maNguoiDung);

    @Query("SELECT g FROM GioHang g WHERE g.nguoiDung.maNguoiDung = :maNguoiDung")
    List<GioHang> findSanPhamByNguoiDung(@Param("maNguoiDung") String maNguoiDung);
}
