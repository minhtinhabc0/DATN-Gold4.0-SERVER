package Gold40.Service;

import Gold40.DAO.NguoiDungDAO;
import Gold40.DAO.TaiKhoanDAO;
import Gold40.Entity.TaiKhoan;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Phương thức đăng ký cho người dùng
    public TaiKhoan registerForUser(String taikhoan, String matkhau, String maNguoiDung) {
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
        newTaiKhoan.setVaitro(0); // Người dùng có vaitro = 0

        newTaiKhoan.setManguoidung(maNguoiDung); // Lưu mã người dùng

        return taiKhoanRepository.save(newTaiKhoan);
    }

    // Phương thức đăng ký cho admin
    public TaiKhoan registerForAdmin(String taikhoan, String matkhau, String maAdmin) {
        if (taiKhoanRepository.findByTaikhoan(taikhoan).isPresent()) {
            throw new RuntimeException("Tài khoản đã tồn tại");
        }

        TaiKhoan newTaiKhoan = new TaiKhoan();
        newTaiKhoan.setId(generateRandomId());
        newTaiKhoan.setTaikhoan(taikhoan);

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(matkhau);
        newTaiKhoan.setMatkhau(encodedPassword);
        newTaiKhoan.setVaitro(1); // Admin có vaitro = 1

        // Tạo mã admin mới và lưu vào maadmin
        newTaiKhoan.setMaadmin(maAdmin);

        return taiKhoanRepository.save(newTaiKhoan);
    }

    // Phương thức đăng ký cho nhà phân phối
    public TaiKhoan registerForDistributor(String taikhoan, String matkhau, String maNguoiDung) {
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
        newTaiKhoan.setVaitro(2); // Nhà phân phối có vaitro = 2

        // Tạo mã nhà phân phối và lưu vào manhaphanphoi
        newTaiKhoan.setManhaphanphoi(newTaiKhoan.getManhaphanphoi());

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
        return UUID.randomUUID().toString().substring(0, 10); // Create a random UUID and use the first 10 characters
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
