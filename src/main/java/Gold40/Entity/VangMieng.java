package Gold40.Entity;
import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "Vang_Mieng")
public class VangMieng {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mavang")
    private Integer maVang;

    @Column(name = "tenvang", length = 255, nullable = false)
    private String tenVang;

    @Column(name = "trongluong", nullable = false)
    private Float trongLuong;

    @Column(name = "gia", nullable = false)
    private Float gia;

    @Column(name = "hinhanh", length = 255)
    private String hinhAnh;

    @Column(name = "thongtin", length = 255)
    private String thongTin;
}

