package Gold40.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
    @Entity
    @Table(name = "Lich_Su_Giao_Dich_NPP")
    public class LichSuGiaoDichNPP {
        @Id
        @Column(name = "malsgdnpp")
        private String maLsgdnpp;

        @Column(name = "thoigian")
        private LocalDateTime thoiGian;

        @Column(name = "trangthai")
        private String trangThai;

        @ManyToOne
        @JoinColumn(name = "mavang", referencedColumnName = "mavang")
        private VangMieng maVang;
        @OneToOne
        @JoinColumn(name = "manhaphanphoi", referencedColumnName = "manhaphanphoi")
        private NhaPhanPhoi maNhaPhanPhoi;

    }
