package Gold40.Service;

import Gold40.DAO.LichSuGiaoDichDAO;
import Gold40.Entity.LichSuGiaoDich;
import Gold40.Entity.VangMieng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LichSuGiaoDichService {
    @Autowired
    private LichSuGiaoDichDAO lichSuGiaoDichDAO;

    public void save(LichSuGiaoDich lichSu) {
        lichSuGiaoDichDAO.save(lichSu);
    }
    public List<LichSuGiaoDich> getTransactionsByUser(String maNguoiDung) {
        return lichSuGiaoDichDAO.findAllByNguoiDung_MaNguoiDung(maNguoiDung);
    }
    public LichSuGiaoDich findById(String maLichSuGiaoDich) {
        return lichSuGiaoDichDAO.findById(maLichSuGiaoDich).orElse(null);
    }

    public LichSuGiaoDich findByMaVang(VangMieng maVang) {
        return lichSuGiaoDichDAO.findByMaVang(maVang);
    }


}
