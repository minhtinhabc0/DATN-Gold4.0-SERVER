package Gold40.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Hoa_Don")
public class HoaDon {

    @Id

    @Column(name = "mahoadon", nullable = false)
    private Integer maHoaDon;

    @Column(name = "ngayinhoadon", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayInHoaDon;

    @Column(name = "manguoidung", length = 20, nullable = false)
    private String maNguoiDung;

    @Column(name = "manhaphanphoi", length = 255)
    private String maNhaPhanPhoi;

    @Column(name = "tongtien", nullable = false)
    private Float tongTien;

    @Column(name = "phuongthuc", length = 50, nullable = false)
    private String phuongThuc;

    @Column(name = "trangthai", length = 50, nullable = false)
    private String trangThai;

    @Column(name = "ordercode", nullable = false)
    private Long orderCode;

    @Column(name = "gia", nullable = false)
    private String gia;

    @Column(name = "soluong", nullable = false)
    private String soLuong;

    @Column(name = "tensanpham", length = 255, nullable = false)
    private String tenSanPham;

    @Column(name = "tongsoluong", nullable = false)
    private Integer tongSoLuong;

    @Column(name = "kichthuoc")
    private String kichThuoc; // Cột kích thước
    @Column(name = "hinhanh", length = 255, nullable = false)
    private String hinhAnh;
    // Thêm mối quan hệ với SanPham
    @ManyToOne
    @JoinColumn(name = "masanpham")  // Thêm khóa ngoại "masanpham"
    private SanPham sanPham; // Mỗi hóa đơn có một sản phẩm

    // Hàm lấy tên cửa hàng của nhà phân phối từ sản phẩm
    public String getTenCuaHang() {
        if (this.sanPham != null && this.sanPham.getNhaPhanPhoi() != null) {
            return this.sanPham.getNhaPhanPhoi().getTenCuaHang();
        }
        return null; // Nếu không có thông tin sản phẩm hoặc nhà phân phối
    }

}
