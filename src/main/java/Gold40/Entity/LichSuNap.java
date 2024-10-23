package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "Lich_Su_Nap")
public class LichSuNap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "malichsunap")
    private Integer maLichSuNap;

    @ManyToOne
    @JoinColumn(name = "manguoidung")
    private NguoiDung nguoiDung;

    @Column(name = "sotiennap", nullable = false)
    private Float soTienNap;

    @Column(name = "thoigiannap")
    private java.util.Date thoiGianNap;
}

