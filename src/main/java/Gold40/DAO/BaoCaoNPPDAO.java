package Gold40.DAO;

import Gold40.Entity.BaoCaoNPP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BaoCaoNPPDAO extends JpaRepository<BaoCaoNPP, Integer> {
    @Query("SELECT bc FROM BaoCaoNPP bc WHERE bc.manhaPhanphoi.maNhaPhanPhoi = :maNhaPhanPhoi")
    List<BaoCaoNPP> findBaoCaoByMaNhaPhanPhoi(@Param("maNhaPhanPhoi") String maNhaPhanPhoi);

    @Query("SELECT b FROM BaoCaoNPP b WHERE b.manhaPhanphoi.maNhaPhanPhoi = :maNhaPhanPhoi AND SUBSTRING(b.thoiGian, 1, 4) = :year")
    List<BaoCaoNPP> findBaoCaoByMaNhaPhanPhoiAndYear(@Param("maNhaPhanPhoi") String maNhaPhanPhoi, @Param("year") String year);

    @Query("SELECT b FROM BaoCaoNPP b WHERE SUBSTRING(b.thoiGian, 1, 4) = :year")
    List<BaoCaoNPP> findBaoCaoByYear(@Param("year") String year);

}

