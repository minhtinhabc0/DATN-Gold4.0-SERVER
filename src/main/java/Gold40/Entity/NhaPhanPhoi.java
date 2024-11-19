package Gold40.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


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
    @Column(name = "dl1", length = 255)
    private String dl1;
    @Column(name = "dl2", length = 255)
    private String dl2;
    @Column(name = "dl3", length = 255)
    private String dl3;



}

