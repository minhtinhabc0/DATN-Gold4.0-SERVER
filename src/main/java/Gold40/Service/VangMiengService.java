package Gold40.Service;

import Gold40.DAO.VangMiengDAO;
import Gold40.Entity.VangMieng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

    @Service
    public class VangMiengService {
        @Autowired
        private VangMiengDAO vangMiengDAO;

        public void save(VangMieng vangMieng) {
            vangMiengDAO.save(vangMieng);
        }

        public VangMieng findById(String maVang) {
            return vangMiengDAO.findByMaVang(maVang)
                    .orElseThrow(() -> new RuntimeException("Mã vàng không tồn tại"));
        }


    }
