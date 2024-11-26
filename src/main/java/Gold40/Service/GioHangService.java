package Gold40.Service;

import Gold40.DAO.GioHangDAO;
import Gold40.Entity.GioHang;
import Gold40.Entity.SanPham;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GioHangService {

    @Autowired
    private GioHangDAO GioHangDAO;
    @Autowired
    private GioHangDAO gioHangDAO;

    public void save(GioHang gioHang) {
        GioHangDAO.save(gioHang);

    }
    /**
     * Lấy danh sách giỏ hàng của người dùng dựa trên mã người dùng
     * @param maNguoiDung mã người dùng
     * @return danh sách giỏ hàng
     */
    public List<GioHang> findByNguoiDung(String maNguoiDung) {
        return GioHangDAO.findSanPhamByNguoiDung(maNguoiDung);
    }

    /**
     * Xóa toàn bộ giỏ hàng của người dùng
     * @param maNguoiDung mã người dùng
     */
    public void clearCart(String maNguoiDung) {
        // Lấy tất cả các sản phẩm trong giỏ hàng của người dùng
        List<GioHang> gioHangList = findByNguoiDung(maNguoiDung);

        // Kiểm tra nếu giỏ hàng có sản phẩm
        if (gioHangList != null && !gioHangList.isEmpty()) {
            // Xóa tất cả các mục trong giỏ hàng
            gioHangDAO.deleteAll(gioHangList);
            System.out.println("Giỏ hàng đã được xóa cho người dùng: " + maNguoiDung);
        } else {
            System.out.println("Giỏ hàng trống hoặc không tồn tại cho người dùng: " + maNguoiDung);
        }
    }

}
