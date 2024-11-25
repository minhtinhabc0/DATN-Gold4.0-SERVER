package Gold40.Service;

import Gold40.DAO.DanhGiaDAO;
import Gold40.Entity.DanhGia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DanhGiaService {

    @Autowired
    private DanhGiaDAO danhGiaDAO;

    public List<DanhGia> getDanhGiaBySanPham(Integer masanpham) {
        return danhGiaDAO.getDanhGiaBySanPham(masanpham);
    }

    public DanhGia createDanhGia(DanhGia danhGia) {
        return danhGiaDAO.save(danhGia);
    }
}
