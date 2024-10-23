package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "Phan_Hoi")
public class PhanHoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maphanhoi")
    private Integer maPhanHoi;

    @Column(name = "noidung", length = 255, nullable = false)
    private String noiDung;

    @ManyToOne
    @JoinColumn(name = "manguoidung")
    private NguoiDung nguoiDung;

    @Column(name = "thoigian")
    private java.util.Date thoiGian;
}

