package Gold40.Service;


import Gold40.DAO.NguoiDungDAO;
import Gold40.Entity.NguoiDung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class NguoiDungService {
    @Autowired
    private NguoiDungDAO nguoiDungDAO;
//    private final BCryptPasswordEncoder passwordEncoder;

    public void save(NguoiDung nguoiDung) {
        nguoiDungDAO.save(nguoiDung);
    }
    public boolean existsByEmail(String email) {
        return nguoiDungDAO.existsByEmail(email);
    }


    public boolean kiemTraNguoiDung(String maNguoiDung) {
        return nguoiDungDAO.existsByMaNguoiDung(maNguoiDung);
    }

    public NguoiDung findByMaNguoiDung(String maNguoiDung) {
        return nguoiDungDAO.findByMaNguoiDung(maNguoiDung);
    }

    public boolean emailExists(String newEmail) {
        return nguoiDungDAO.existsByEmail(newEmail);
    }

//    @Autowired
//    public NguoiDungService(BCryptPasswordEncoder PinPasswordEncoder) {
//        this.passwordEncoder = PinPasswordEncoder;
//    }

}
