package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Gio_Hang")
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magiohang")
    private Integer maGioHang;

    @ManyToOne
    @JoinColumn(name = "manguoidung")
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "masanpham")
    private SanPham sanPham;

    @Column(name = "soluong")
    private Integer soLuong;
}

