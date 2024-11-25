package Gold40.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Danh_Gia")
public class DanhGia {
    @Id
    @Column(name = "madanhgia")
    private String maDanhGia;

    @ManyToOne
    @JoinColumn(name = "manguoidung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "masanpham", nullable = false)
    private SanPham sanPham;
@Column(name = "diemdanhgia")
    private float diemDanhGia;
@Column(name = "noidung")
    private String noiDungDanhGia;
@Column(name = "thoigian")
    private LocalDateTime ngayDanhGia;
}
