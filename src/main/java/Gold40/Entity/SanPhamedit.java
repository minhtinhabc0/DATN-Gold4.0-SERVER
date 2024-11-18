package Gold40.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "San_Pham")
public class SanPhamedit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "masanpham", nullable = false)
    private Long maSanPham;

    @Column(name = "tensanpham", length = 255, nullable = false)
    private String tenSanPham;

    @Column(name = "gia", nullable = false)
    private Integer gia;
    @Column(name = "tiencong", length = 255)
    private Integer tienCong;

    @Column(name = "chitiet", length = 255)
    private String chiTiet;

    @Column(name = "loai", length = 255)
    private String loai;

    @Column(name = "hinhanh", length = 255)
    private String hinhAnh;

    @Column(name = "kichco", length = 255)
    private String kichCo;

    @Column(name = "loaivang", length = 255)
    private String loaiVang;

    @Column(name = "trongluong", length = 255 , nullable = false)
    private double trongLuong;

    @Column(name = "loaida", length = 255)
    private String loaiDa;

    @Column(name = "soluong", nullable = false)
    private Integer soLuong;

    @Column(name = "manhaphanphoi", length = 255)
    private String maNhaPhanPhoi;
}
