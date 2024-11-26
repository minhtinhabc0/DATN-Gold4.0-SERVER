package Gold40.DAO;

import Gold40.Entity.DonHang;
import Gold40.Entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangDAO extends JpaRepository<DonHang, String> {
    long countByTrangThai(String trangThai);
    // Tìm danh sách đơn hàng qua mã nhà phân phối trong HoaDon
    @Query("SELECT dh FROM DonHang dh JOIN dh.sanPham sp WHERE sp.nhaPhanPhoi.maNhaPhanPhoi = :maNhaPhanPhoi")
    List<DonHang> findDonHangsByDistributor(@Param("maNhaPhanPhoi") String maNhaPhanPhoi);

    @Query("SELECT dh FROM DonHang dh JOIN dh.hoaDon hd WHERE hd.maNhaPhanPhoi = :maNhaPhanPhoi AND dh.trangThai = 'Hoàn thành'")
    List<DonHang> findCompletedDonHangsByDistributor(@Param("maNhaPhanPhoi") String maNhaPhanPhoi);

    // Tìm danh sách đơn hàng qua mã người dùng
    @Query("SELECT dh FROM DonHang dh WHERE dh.nguoiDung.maNguoiDung = :maNguoiDung")
    List<DonHang> findDonHangsByUser(@Param("maNguoiDung") String maNguoiDung);


    @Query("SELECT h FROM HoaDon h WHERE h.maNguoiDung = :maNguoiDung")
    List<HoaDon> findByMaNguoiDung(@Param("maNguoiDung") String maNguoiDung);

}
