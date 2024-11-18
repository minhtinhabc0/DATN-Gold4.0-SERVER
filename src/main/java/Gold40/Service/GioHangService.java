package Gold40.Service;

import Gold40.DAO.GioHangDAO;
import Gold40.DAO.NguoiDungDAO;
import Gold40.Entity.GioHang;
import Gold40.Entity.NguoiDung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GioHangService {
    @Autowired
    private NguoiDungDAO nguoiDungDAO;
    @Autowired
    private GioHangDAO GioHangDAO;
    public void save(GioHang gioHang) {
        GioHangDAO.save(gioHang);

    }

}
