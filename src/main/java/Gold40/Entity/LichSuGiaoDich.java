package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Lich_Su_Giao_Dich")
public class LichSuGiaoDich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "malichsuduyet")
    private Integer maLichSuGiaoDich;

    @ManyToOne
    @JoinColumn(name = "manguoidung")
    private NguoiDung nguoiDung;

    @Column(name = "sotien", nullable = false)
    private Float soTien;

    @Column(name = "thoigian")
    private java.util.Date thoiGian;

    @Column(name = "noidung", length = 255)
    private String noiDung;
}

