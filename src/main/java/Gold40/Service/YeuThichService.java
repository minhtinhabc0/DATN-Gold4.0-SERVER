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


    // Thêm sản phẩm vào danh sách yêu thích
    public void addProductToFavorites(YeuThich yeuThich) {
        // Kiểm tra xem sản phẩm đã có trong danh sách yêu thích chưa
        YeuThich existing = yeuThichDAO.findByNguoiDung_MaNguoiDungAndSanPham_MaSanPham(yeuThich.getNguoiDung().getMaNguoiDung(), yeuThich.getSanPham().getMaSanPham());
        if (existing == null) {
            yeuThichDAO.save(yeuThich);
        } else {
            // Nếu sản phẩm đã có trong danh sách yêu thích, không làm gì cả (hoặc có thể trả về thông báo)
        }
    }


}
