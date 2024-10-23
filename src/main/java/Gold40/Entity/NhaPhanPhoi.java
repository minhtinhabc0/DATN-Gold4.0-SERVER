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
@Table(name = "Nha_Phan_Phoi")
public class NhaPhanPhoi {
    @Id
    @Column(name = "manhaphanphoi", length = 20, nullable = false)
    private String maNhaPhanPhoi;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "tencuahang", length = 255, nullable = false)
    private String tenCuaHang;

    @Column(name = "sdt", length = 10, nullable = false)
    private String sdt;

    @Column(name = "diachi", length = 255, nullable = false)
    private String diaChi;
}

