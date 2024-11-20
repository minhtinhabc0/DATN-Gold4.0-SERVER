package Gold40.Service;

import Gold40.DAO.LichSuNapDAO;
import Gold40.DAO.NguoiDungDAO;
import Gold40.Entity.LichSuNap;
import Gold40.Entity.NguoiDung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
public class PaymentService {

    private final LichSuNapDAO lichSuNapRepository;
    private final NguoiDungDAO nguoiDungRepository;

    @Autowired
    public PaymentService(LichSuNapDAO lichSuNapRepository, NguoiDungDAO nguoiDungRepository) {
        this.lichSuNapRepository = lichSuNapRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }
    private int generateRandomProductCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;  // Generate a 6-digit number between 100000 and 999999
    }
    public void savePaymentHistory(String maNguoiDung, String trangThai, int soGcoin, float soTienNap, long orderCode) {
        // Lấy thông tin người dùng từ mã người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findByMaNguoiDung(maNguoiDung);
        System.out.println(nguoiDung);
        if (nguoiDung != null) {
            LichSuNap lichSuNap = new LichSuNap();
            int randomProductCode = generateRandomProductCode();
            lichSuNap.setMaLichSuNap(randomProductCode);
            lichSuNap.setNguoiDung(nguoiDung);
            lichSuNap.setTrangThai(trangThai);
            lichSuNap.setSoGcoin(soGcoin);
            lichSuNap.setSoTienNap(soTienNap);
            lichSuNap.setThoiGianNap(new Date()); // Thời gian nạp là thời điểm hiện tại
            lichSuNap.setOrderCode(orderCode); // Set the orderCode

            // Lưu vào cơ sở dữ liệu
            lichSuNapRepository.save(lichSuNap);
        }
        System.out.println("ronggg");
    }
}
