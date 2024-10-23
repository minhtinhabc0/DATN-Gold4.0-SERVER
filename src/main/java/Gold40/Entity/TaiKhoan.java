package Gold40.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tai_khoan")
public class TaiKhoan {
    @Id
    private String id;

    @Column(name = "taikhoan", length = 100, nullable = false, unique = true)
    private String taikhoan;

    @Column(name = "matkhau", length = 100, nullable = false)
    private String matkhau;

    @Column(name = "mapin")
    private String mapin;

    @Column(name = "vaitro", nullable = false)
    private int vaitro;

    @Column(name = "maadmin", length = 10)
    private String maadmin;

    @Column(name = "manguoidung", length = 10) // Đảm bảo trường này ánh xạ chính xác với NguoiDung
    private String manguoidung;

    @Column(name = "manhaphanphoi", length = 10) // Đảm bảo trường này ánh xạ chính xác với NguoiDung
    private String manhaphanphoi;

    // Thiết lập quan hệ một-một với NguoiDung
    @OneToOne
    @JoinColumn(name = "manguoidung", referencedColumnName = "manguoidung", insertable = false, updatable = false)
    private NguoiDung nguoiDung;


}
