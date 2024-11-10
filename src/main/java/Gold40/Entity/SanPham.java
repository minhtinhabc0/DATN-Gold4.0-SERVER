package Gold40.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "San_Pham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "masanpham", nullable = false)
    private Integer maSanPham;

    @Column(name = "tensanpham", length = 255, nullable = false)
    private String tenSanPham;

    @Column(name = "gia", nullable = false)
    private Integer gia;

    @Column(name = "chitiet", length = 255, nullable = false)
    private String chiTiet;

    @Column(name = "loai", length = 255, nullable = false)
    private String loai;

    @Column(name = "hinhanh", length = 255, nullable = false)
    private String hinhAnh;

    @Column(name = "kichco", length = 255, nullable = false)
    private String kichCo;

    @Column(name = "loaivang", length = 255, nullable = false)
    private String loaiVang;

    @Column(name = "trongluong", nullable = false)
    private Float trongLuong;

    @Column(name = "loaida", length = 255, nullable = false)
    private String loaiDa;


    @Column(name = "soluong")
    private int soLuong;

    @ManyToOne
    @JoinColumn(name = "manhaphanphoi")
    private NhaPhanPhoi nhaPhanPhoi;
}

