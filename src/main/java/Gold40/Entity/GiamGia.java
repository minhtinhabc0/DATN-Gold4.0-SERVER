package Gold40.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Giam_Gia")
public class GiamGia {

    @Id
    @Column(name = "magiamgia", length = 255, nullable = false)
    private String maGiamGia;

    @ManyToOne
    @JoinColumn(name = "masanpham", referencedColumnName = "masanpham", nullable = false)
    private SanPham sanPham;  // Mối quan hệ với bảng SanPham

    @Column(name = "giatri", nullable = false)
    private Float giaTri;

    @Column(name = "ngaybatdau", nullable = false)
    private java.sql.Date ngayBatDau;

    @Column(name = "ngayketthuc", nullable = false)
    private java.sql.Date ngayKetThuc;

    @Column(name = "soluong", nullable = false)
    private Integer soLuong;
}
