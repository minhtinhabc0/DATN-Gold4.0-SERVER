package Gold40.Service;

import Gold40.DAO.NguoiDungDAO;
import Gold40.DAO.TaiKhoanDAO;
import Gold40.Entity.TaiKhoan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
public class TaiKhoanService {

    @Autowired
    private TaiKhoanDAO taiKhoanRepository;

    @Autowired
    private NguoiDungDAO nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder instead of creating it manually

    private SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "0123456789";

    // Phương thức đăng nhập
    public TaiKhoan login(String tenTK, String matKhau) {
        TaiKhoan user = taiKhoanRepository.findByTaikhoan(tenTK)
                .orElseThrow(() -> new RuntimeException("Tên tài khoản không tồn tại"));

        // Sử dụng passwordEncoder để kiểm tra mật khẩu đã mã hóa
        if (!passwordEncoder.matches(matKhau, user.getMatkhau())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        return user;
    }

    // Phương thức đăng ký
    public TaiKhoan register(String taikhoan, String matkhau, String maNguoiDung, int vaitro) {
        if (taiKhoanRepository.findByTaikhoan(taikhoan).isPresent()) {
            throw new RuntimeException("Tài khoản đã tồn tại");
        }

        if (!nguoiDungRepository.existsByMaNguoiDung(maNguoiDung)) {
            throw new RuntimeException("Mã người dùng không tồn tại");
        }

        TaiKhoan newTaiKhoan = new TaiKhoan();
        newTaiKhoan.setId(generateRandomId());
        newTaiKhoan.setTaikhoan(taikhoan);

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(matkhau);
        newTaiKhoan.setMatkhau(encodedPassword);

        newTaiKhoan.setVaitro(vaitro);
        if (vaitro == 0) {
            newTaiKhoan.setManguoidung(maNguoiDung);
        } else if (vaitro == 1) {
            newTaiKhoan.setMaadmin(generateCode());
        } else if (vaitro == 2) {
            newTaiKhoan.setManhaphanphoi(generateCode());
        } else {
            throw new RuntimeException("Vai trò không hợp lệ");
        }

        return taiKhoanRepository.save(newTaiKhoan);
    }

    private String generateRandomId() {
        StringBuilder id = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            id.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return id.toString();
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    public boolean checkPassword(TaiKhoan user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getMatkhau());
    }

    public void save(TaiKhoan user) {
        taiKhoanRepository.save(user);
    }

    public TaiKhoan findByTaikhoan(String taikhoan) {
        return taiKhoanRepository.findByTaikhoan(taikhoan).orElse(null);
    }

    public boolean existsByTaikhoan(String taikhoan) {
        return taiKhoanRepository.existsByTaikhoan(taikhoan);
    }
}
