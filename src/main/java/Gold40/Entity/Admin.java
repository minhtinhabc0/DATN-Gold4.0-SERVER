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
@Table(name = "Admin")
public class Admin {
    @Id
    @Column(name = "maadmin", length = 20, nullable = false)
    private String maAdmin;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "hoten", length = 255, nullable = false)
    private String hoTen;

}

