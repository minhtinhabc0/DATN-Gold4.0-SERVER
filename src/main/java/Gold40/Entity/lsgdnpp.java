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
@Table(name = "Lich_Su_Giao_Dich_NPP")
public class lsgdnpp {
    @Id
    @Column(name = "malsgdnpp", nullable = false, unique = true, length = 255)
    private String maLsgdNpp;

    @Column(name = "malsgd", nullable = false, length = 255)
    private String maLsgd;

    @Column(name = "thoigian")
    private java.util.Date thoiGian;

    @Column(name = "manhaphanphoi", nullable = false, length = 20)
    private String maNhaPhanPhoi;

    @Column(name = "trangthai", length = 255)
    private String trangThai;
}

