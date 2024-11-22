package Gold40.DAO;

import Gold40.Entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductsDAO extends JpaRepository<SanPham, Integer> {
    @Query("SELECT p FROM SanPham p WHERE p.trangThai = true")
    List<SanPham> findAllByTrangThaiTrue();

    List<SanPham> findByNhaPhanPhoi_MaNhaPhanPhoi(String maNhaPhanPhoi);

    boolean existsByMaSanPham(Integer maSanPham); // Thay đổi kiểu dữ liệu từ String sang Integer

    public Optional<SanPham> findById(Integer maSanPham);

    SanPham findByMaSanPham(Integer maSanPham);
}
