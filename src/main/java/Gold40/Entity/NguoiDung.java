package Gold40.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {
    @Id
    @Column(name = "manguoidung", nullable = false, unique = true, length = 20)
    private String maNguoiDung;

    @Column(name = "hoten", nullable = false, length = 255)
    private String hoTen;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "sdt", nullable = false, length = 10)
    private String sdt;

    @Column(name = "avt", length = 255)
    private String avt;

    @Column(name = "magcoin", length = 255)
    private String maGCoin;
}
