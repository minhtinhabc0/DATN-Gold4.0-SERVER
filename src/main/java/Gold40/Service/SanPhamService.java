package Gold40.Service;

import Gold40.DAO.ProductsDAO;
import Gold40.Entity.SanPham;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SanPhamService {

    @Autowired
    private ProductsDAO productsDAO;


    // Phương thức tìm sản phẩm theo maSanPham
    public SanPham findByMaSanPham(Integer maSanPham) {
        Optional<SanPham> sanPham = productsDAO.findById(maSanPham);
        return sanPham.orElse(null);  // Trả về null nếu không tìm thấy sản phẩm
    }

    public boolean kiemtrasanpham(String newId) {
        try {
            Integer maSanPham = Integer.valueOf(newId); // Chuyển đổi chuỗi sang Integer
            return productsDAO.existsByMaSanPham(maSanPham);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ: " + newId, e);
        }
    }
}
