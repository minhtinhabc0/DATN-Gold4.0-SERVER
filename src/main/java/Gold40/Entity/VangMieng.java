package Gold40.Entity;
import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "Vang_Mieng")
public class VangMieng {
    @Id
    @Column(name = "mavang")
    private String maVang;

    @Column(name = "loaivang", length = 255)
    private String loaiVang;

    @Column(name = "tenvang")
    private String tenVang;

    @ManyToOne
    @JoinColumn(name = "manhaphanphoi", referencedColumnName = "manhaphanphoi")
    private NhaPhanPhoi maNhaPhanPhoi;

    @Column(name = "giavang", nullable = false)
    private Float giaVang;

}

