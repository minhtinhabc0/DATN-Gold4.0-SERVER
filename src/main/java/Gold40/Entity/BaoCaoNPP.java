package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Doanh_Thu")
public class BaoCaoNPP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "madoanhthu")
    private Integer maDoanhThu;

    @ManyToOne
    @JoinColumn(name = "manhaphanphoi", nullable = false)
    private NhaPhanPhoi manhaPhanphoi;  // Assuming a NhaPhanPhoi entity exists

    @ManyToOne
    @JoinColumn(name = "masanpham", nullable = false)
    private SanPham masanPham;  // Assuming a SanPham entity exists

    @Column(name = "soluong", nullable = false)
    private Integer soLuong;

    @Column(name = "doanhthu", nullable = false)
    private Float doanhThu;

    @Column(name = "loinhuan", nullable = false)
    private Float loiNhuan;

    @Column(name = "thoigian", nullable = false)
    private String thoiGian;  // Format yyyy-MM
}
