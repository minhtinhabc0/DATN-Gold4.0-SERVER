package Gold40.Service;

import Gold40.DAO.LichSuGiaoDichNPPDAO;
import Gold40.DAO.NhaPhanPhoiDAO;
import Gold40.Entity.LichSuGiaoDichNPP;
import Gold40.Entity.NhaPhanPhoi;
import Gold40.Entity.VangMieng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LichSuGiaoDichNPPService {
    @Autowired
    private LichSuGiaoDichNPPDAO lichSuGiaoDichNPPDAO;

    @Autowired
    private VangMiengService vangMiengService;

    @Autowired
    private NhaPhanPhoiService nhaPhanPhoiService;



    public void save(LichSuGiaoDichNPP lichSuGiaoDichNPP) {
        lichSuGiaoDichNPPDAO.save(lichSuGiaoDichNPP);
    }
}