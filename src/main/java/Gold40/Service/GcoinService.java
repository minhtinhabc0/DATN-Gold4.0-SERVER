package Gold40.Service;


import Gold40.DAO.GcoinDAO;
import Gold40.Entity.Gcoin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GcoinService {

    private static GcoinDAO gcoinDAO = null;

    @Autowired
    public GcoinService(GcoinDAO gcoinDAO) {
        this.gcoinDAO = gcoinDAO;
    }

    public static void save(Gcoin gcoin) {
        gcoinDAO.save(gcoin);
    }

    public static Gcoin findByMagcoin(String magcoin) {
        return gcoinDAO.findById(magcoin).orElse(null);
    }
}
