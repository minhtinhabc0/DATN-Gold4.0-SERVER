package Gold40.Service;

import Gold40.DAO.ProductsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SanPhamService {

    @Autowired
    private ProductsDAO productsDAO;

    public boolean kiemtrasanpham(String newId) {
        try {
            Integer maSanPham = Integer.valueOf(newId); // Chuyển đổi chuỗi sang Integer
            return productsDAO.existsByMaSanPham(maSanPham);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ: " + newId, e);
        }
    }
}
