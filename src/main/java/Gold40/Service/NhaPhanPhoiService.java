package Gold40.Service;

import Gold40.DAO.NhaPhanPhoiDAO;
import Gold40.Entity.NhaPhanPhoi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NhaPhanPhoiService {
    @Autowired
    private NhaPhanPhoiDAO NPPDAO;

    public boolean existsByEmail(String email) {
        return NPPDAO.existsByEmail(email);
    }

    // Lưu nhà phân phối vào cơ sở dữ liệu
    public NhaPhanPhoi saveNhaPhanPhoi(NhaPhanPhoi nhaPhanPhoi) {
        return NPPDAO.save(nhaPhanPhoi);
    }

    public void save(NhaPhanPhoi nhaPhanPhoi) {
        NPPDAO.save(nhaPhanPhoi);
    }

    public boolean kiemTraNguoiDung(String maNhaPhanPhoi) {
        return NPPDAO.existsByMaNhaPhanPhoi(maNhaPhanPhoi);

    }



    public void deleteByTaiKhoanId(String id) {
    }

    public Optional<NhaPhanPhoi> findById(String id) {
        return NPPDAO.findById(id);
    }
}
