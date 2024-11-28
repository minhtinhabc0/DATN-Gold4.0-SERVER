package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;


@Data
@Entity
@Table(name = "Lich_Su_Giao_Dich")
public class LichSuGiaoDich {
    @Id
    @Column(name = "malsgd")
    private String maLichSuGiaoDich;

    @OneToOne
    @JoinColumn(name = "mavang", referencedColumnName = "mavang")
    private VangMieng maVang;

    @Column(name = "thoigian")
    private Date thoiGian;

    @Column(name = "trangthai")
    private String trangThai;

    @Column(name = "soluong")
    private int soluong;

    @ManyToOne
    @JoinColumn(name = "manguoidung")
    private NguoiDung nguoiDung;
}

