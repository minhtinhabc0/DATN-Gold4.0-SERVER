package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "Hoa_Don")
public class HoaDon {
    @Id
    @Column(name = "mahoadon", length = 255, nullable = false)
    private String maHoaDon;

    @ManyToOne
    @JoinColumn(name = "manguoidung")
    private NguoiDung nguoiDung;

    @Column(name = "tongtien", nullable = false)
    private Float tongTien;

    @Column(name = "ngaytao")
    private java.util.Date ngayTao;

    @Column(name = "trangthai", length = 255, nullable = false)
    private String trangThai;

    @Column(name = "hinhanh", length = 255)
    private String hinhAnh;
}
