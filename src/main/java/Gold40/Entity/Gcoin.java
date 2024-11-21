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
@Table(name = "G_Coin")
public class Gcoin {
    @Id
    @Column(name = "magcoin")
    private String magcoin; // Assuming magcoin is a String, adjust type if necessary

    @Column(name = "malichsunap")
    private String malichsunap; // Adjust type as needed

    @Column(name = "sogcoin")
    private int sogcoin; // Assuming sogcoin is an integer

    @Column(name = "tigiahientai")
    private int tigiahientai; // Changed type to int
}
