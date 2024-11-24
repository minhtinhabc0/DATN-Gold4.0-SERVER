package Gold40.Service;

import Gold40.Entity.YeuThich;
import Gold40.DAO.YeuThichDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YeuThichService {

    @Autowired
    private YeuThichDAO yeuThichDAO;

    // Lấy danh sách sản phẩm yêu thích của người dùng
    public List<YeuThich> getFavoriteProductsByUser(String maNguoiDung) {
        if (maNguoiDung == null || maNguoiDung.isEmpty()) {
            throw new IllegalArgumentException("Mã người dùng không hợp lệ.");
        }
        return yeuThichDAO.findByNguoiDung_MaNguoiDung(maNguoiDung);
    }
    public void removeProductFromFavorites(YeuThich yeuThich) {
        yeuThichDAO.delete(yeuThich);  // Xóa sản phẩm khỏi danh sách yêu thích
    }


    // Thêm sản phẩm vào danh sách yêu thích
    public void addProductToFavorites(YeuThich yeuThich) {
        if (yeuThich == null || yeuThich.getNguoiDung() == null || yeuThich.getSanPham() == null) {
            // Xử lý lỗi nếu đối tượng YeuThich hoặc các trường cần thiết không hợp lệ
            throw new IllegalArgumentException("Yêu thích hoặc thông tin sản phẩm không hợp lệ.");
        }

        // Kiểm tra xem sản phẩm đã có trong danh sách yêu thích chưa
        YeuThich existing = yeuThichDAO.findByNguoiDungMaNguoiDungAndSanPhamMaSanPham(
                yeuThich.getNguoiDung().getMaNguoiDung(),
                yeuThich.getSanPham().getMaSanPham()
        );
        if (existing == null) {
            yeuThichDAO.save(yeuThich);
        } else {
            // Nếu sản phẩm đã có trong danh sách yêu thích, không làm gì cả (hoặc có thể trả về thông báo)
        }
    }
    public YeuThich findFavoriteByUserAndProduct(String maNguoiDung, Integer maSanPham) {
        return yeuThichDAO.findByNguoiDungMaNguoiDungAndSanPhamMaSanPham(maNguoiDung, maSanPham);
    }


}