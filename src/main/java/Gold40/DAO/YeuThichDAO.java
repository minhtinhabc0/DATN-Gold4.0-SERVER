package Gold40.DAO;

import Gold40.Entity.YeuThich;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface YeuThichDAO extends JpaRepository<YeuThich, Integer> {



    // Kiểm tra sản phẩm yêu thích của người dùng
    @Query("SELECT y FROM YeuThich y WHERE y.nguoiDung.maNguoiDung = :maNguoiDung AND y.sanPham.maSanPham = :maSanPham")
    YeuThich findByNguoiDung_MaNguoiDungAndSanPham_MaSanPham(String maNguoiDung, Integer maSanPham);
    @Query("SELECT y FROM YeuThich y WHERE y.nguoiDung.maNguoiDung = :maNguoiDung")
    List<YeuThich> findByNguoiDung_MaNguoiDung(@Param("maNguoiDung") String maNguoiDung);

    @Query("DELETE FROM YeuThich y WHERE y.sanPham.maSanPham = :maSanPham AND y.nguoiDung.maNguoiDung = :maNguoiDung")
    void deleteBySanPhamAndNguoiDung(@Param("maSanPham") Integer maSanPham, @Param("maNguoiDung") String maNguoiDung);

}
