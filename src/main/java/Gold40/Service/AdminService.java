package Gold40.Service;

import Gold40.DAO.AdminDAO;
import Gold40.DAO.TaiKhoanDAO;
import Gold40.Entity.Admin;
import Gold40.Entity.TaiKhoan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminDAO adminDAO;
    public void save(Admin admin) {
        adminDAO.save(admin);
    }
    public boolean existsByEmail(String email) {
        return adminDAO.existsByEmail(email);
    }
    public boolean kiemTraNguoiDung(String maAdmin) {
        return adminDAO.existsByMaAdmin(maAdmin);
    }


}
