package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "Lich_Su_Nap")
public class LichSuNap {
    @Id
    @Column(name = "malichsunap")
    private Integer maLichSuNap;

    @ManyToOne
    @JoinColumn(name = "manguoidung")
    private NguoiDung nguoiDung;

    @Column(name="ordercode")
    private long orderCode;
    @Column(name="phuongthuc")
    private String phuongThuc;
    @Column(name="trangthai")
    private String trangThai;

    @Column(name="sogcoin")
    private Integer soGcoin;

    @Column(name = "sotien", nullable = false)
    private Float soTienNap;

    @Column(name = "thoigiannap")
    private java.util.Date thoiGianNap;
}

