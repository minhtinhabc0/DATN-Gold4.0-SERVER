package Gold40.DAO;

import Gold40.Entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangDAO extends JpaRepository<DonHang, String> {
    long countByTrangThai(String trangThai);
    // Tìm danh sách đơn hàng qua mã nhà phân phối trong HoaDon
    @Query("SELECT dh FROM DonHang dh JOIN dh.hoaDon hd WHERE hd.maNhaPhanPhoi = :maNhaPhanPhoi")
    List<DonHang> findDonHangsByDistributor(@Param("maNhaPhanPhoi") String maNhaPhanPhoi);
    @Query("SELECT dh FROM DonHang dh JOIN dh.hoaDon hd WHERE hd.maNhaPhanPhoi = :maNhaPhanPhoi AND dh.trangThai = 'Hoàn thành'")
    List<DonHang> findCompletedDonHangsByDistributor(@Param("maNhaPhanPhoi") String maNhaPhanPhoi);
}
