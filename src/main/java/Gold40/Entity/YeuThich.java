package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "Yeu_Thich")
public class YeuThich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mayeuthich")
    private Integer maYeuThich;

    @ManyToOne
    @JoinColumn(name = "manguoidung")
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "masanpham")
    private SanPham sanPham;
}

