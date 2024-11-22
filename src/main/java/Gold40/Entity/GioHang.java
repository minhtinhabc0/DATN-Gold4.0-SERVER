package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
@Table(name = "Gio_Hang")
public class GioHang {
    @Id
    @Column(name = "magiohang")
    private Integer maGioHang;

    @ManyToOne
    @JoinColumn(name = "manguoidung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "masanpham", nullable = false)
    private SanPham sanPham;

    @Column(name = "soluong")
    private Integer soLuong;

    @Column(name = "kichthuoc")
    private String kichThuoc;



}

