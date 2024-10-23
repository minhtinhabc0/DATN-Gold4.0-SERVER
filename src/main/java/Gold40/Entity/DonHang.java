package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "Don_Hang")
public class DonHang {
    @Id
    @Column(name = "madonhang", length = 255, nullable = false)
    private String maDonHang;

    @ManyToOne
    @JoinColumn(name = "masanpham")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "manguoidung")
    private NguoiDung nguoiDung;

    @Column(name = "tongtien")
    private Float tongTien;

    @Column(name = "trangthai", length = 255)
    private String trangThai;

    @Column(name = "thoigian")
    private java.util.Date thoiGian;

    @Column(name = "soluong")
    private Integer soLuong;

    @Column(name = "dongia")
    private Float donGia;

    @ManyToOne
    @JoinColumn(name = "magiamgia")
    private GiamGia giamGia;  // Assuming a GiamGia entity exists

    @ManyToOne
    @JoinColumn(name = "mahoadon")
    private HoaDon hoaDon;  // Assuming a HoaDon entity exists
}

