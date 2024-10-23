package Gold40.DAO;

import Gold40.Entity.Gcoin;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface GcoinDAO extends JpaRepository<Gcoin,String> {
}
