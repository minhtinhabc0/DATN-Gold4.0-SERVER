package Gold40.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;



@Data
@Entity
@Table(name = "Giam_Gia")
public class GiamGia {
    @Id
    @Column(name = "magiamgia", length = 20, nullable = false)
    private String maGiamGia;

    @Column(name = "phantramgiam", nullable = false)
    private Float phanTramGiam;

    @Column(name = "ngaybatdau")
    private java.util.Date ngayBatDau;

    @Column(name = "ngayketthuc")
    private java.util.Date ngayKetThuc;

    @Column(name = "trangthai", length = 20, nullable = false)
    private String trangThai;
}

