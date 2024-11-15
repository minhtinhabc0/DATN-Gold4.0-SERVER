package Gold40.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Hoa_Don")
public class HoaDon {

    @Id
    @Column(name = "mahoadon", nullable = false)
    private Integer maHoaDon;

    @Column(name = "ngayinhoadon", nullable = false)
    private java.sql.Date ngayInHoaDon;

    @Column(name = "manguoidung", length = 20, nullable = false)
    private String maNguoiDung;

    @Column(name = "manhaphanphoi", length = 20, nullable = false)
    private String maNhaPhanPhoi;

    @Column(name = "tongtien", nullable = false)
    private Float tongTien;
}
