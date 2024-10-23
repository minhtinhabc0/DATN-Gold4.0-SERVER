package Gold40.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "Danh_Muc")
public class DanhMuc {
    @Id
    @Column(name = "madanhmuc", length = 20, nullable = false)
    private String maDanhMuc;

    @Column(name = "tendanhmuc", length = 255, nullable = false)
    private String tenDanhMuc;
}

