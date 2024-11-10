package Gold40.Service;

import Gold40.DAO.NhaPhanPhoiDAO;
import Gold40.Entity.NhaPhanPhoi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NhaPhanPhoiService {
    @Autowired
    private NhaPhanPhoiDAO nhaPhanPhoiRepository;

    // Lưu nhà phân phối vào cơ sở dữ liệu
    public NhaPhanPhoi saveNhaPhanPhoi(NhaPhanPhoi nhaPhanPhoi) {
        return nhaPhanPhoiRepository.save(nhaPhanPhoi);
    }
}
