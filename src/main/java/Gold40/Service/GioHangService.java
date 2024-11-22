package Gold40.Service;

import Gold40.DAO.GioHangDAO;
import Gold40.Entity.GioHang;
import Gold40.Entity.SanPham;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GioHangService {

    @Autowired
    private GioHangDAO GioHangDAO;
    public void save(GioHang gioHang) {
        GioHangDAO.save(gioHang);

    }

}
