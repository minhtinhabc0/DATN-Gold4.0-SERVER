package Gold40.Service;

import Gold40.DAO.*;
import Gold40.Entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class PaymentTSService {
    @Autowired
    private DonHangDAO donHangDao;
    private final HoaDonDAO hoaDonRepository;
    private final NguoiDungDAO nguoiDungRepository;
    private final GioHangService gioHangService;
    @Autowired
    private EmailService emailService;
    @Autowired
    public PaymentTSService(HoaDonDAO hoaDonRepository, NguoiDungDAO nguoiDungRepository, GioHangService gioHangService) {
        this.hoaDonRepository = hoaDonRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.gioHangService = gioHangService;
    }

    private int generateRandomProductCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;  // Generate a 6-digit number between 100000 and 999999
    }
    @SneakyThrows
    @Transactional
    public void savePaymentGioHangToHoaDon(String maNguoiDung, String phuongthuc, String trangThai, long orderCode) {
        // Lấy thông tin người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findByMaNguoiDung(maNguoiDung);
        if (nguoiDung != null) {
            // Lấy danh sách giỏ hàng
            List<GioHang> gioHangList = gioHangService.findByNguoiDung(maNguoiDung);
            int tongSoLuong = 0;
            float tongTien = 0;

            // Khởi tạo các danh sách để chứa thông tin sản phẩm
            List<String> tenSanPhamList = new ArrayList<>();
            List<String> soLuongList = new ArrayList<>();
            List<String> maNhaPhanPhoiList = new ArrayList<>();
            List<String> giaList = new ArrayList<>();
            List<String> kichThuocList = new ArrayList<>();
            List<String> hinhAnhList = new ArrayList<>();
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            // Duyệt qua từng sản phẩm trong giỏ hàng
            for (GioHang gioHang : gioHangList) {
                // Cộng dồn tổng tiền và tổng số lượng
                tongSoLuong += gioHang.getSoLuong();
                tongTien += gioHang.getSoLuong() * gioHang.getSanPham().getGia().floatValue();
                String hinhAnh = gioHang.getSanPham().getHinhAnh();
                // Định dạng giá trị gia
                String giaFormatted = decimalFormat.format(gioHang.getSanPham().getGia());

                // Thêm các thông tin sản phẩm vào danh sách
                tenSanPhamList.add(gioHang.getSanPham().getTenSanPham());
                soLuongList.add(String.valueOf(gioHang.getSoLuong()));
                maNhaPhanPhoiList.add(gioHang.getSanPham().getNhaPhanPhoi().getMaNhaPhanPhoi().toString());
                giaList.add(giaFormatted);
                kichThuocList.add(gioHang.getKichThuoc());
                hinhAnhList.add(hinhAnh);
            }

            // Tạo đối tượng ObjectMapper để chuyển danh sách thành chuỗi JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String tenSanPhamJson = objectMapper.writeValueAsString(tenSanPhamList);
            String soLuongJson = objectMapper.writeValueAsString(soLuongList);
            String maNhaPhanPhoiJson = objectMapper.writeValueAsString(maNhaPhanPhoiList);
            String giaJson = objectMapper.writeValueAsString(giaList);
            String kichThuocJson = objectMapper.writeValueAsString(kichThuocList);
            String hinhAnhJson = objectMapper.writeValueAsString(hinhAnhList);
            // Tạo hóa đơn mới
            HoaDon hoaDon = new HoaDon();
            int randomInvoiceCode = generateRandomProductCode();

            // Thiết lập giá trị cho hóa đơn
            hoaDon.setMaHoaDon(randomInvoiceCode); // Mã hóa đơn
            hoaDon.setNgayInHoaDon(new Date());    // Ngày tạo hóa đơn
            hoaDon.setMaNguoiDung(maNguoiDung);   // Mã người dùng
            hoaDon.setPhuongThuc(phuongthuc);     // Phương thức thanh toán
            hoaDon.setTrangThai(trangThai);       // Trạng thái thanh toán
            hoaDon.setOrderCode(orderCode);       // Mã đơn hàng
            hoaDon.setTenSanPham(tenSanPhamJson); // Các tên sản phẩm
            hoaDon.setSoLuong(soLuongJson);       // Các số lượng sản phẩm
            hoaDon.setMaNhaPhanPhoi(maNhaPhanPhoiJson); // Các mã nhà phân phối
            hoaDon.setGia(giaJson);               // Các giá trị sản phẩm
            hoaDon.setKichThuoc(kichThuocJson);   // Các kích thước
            hoaDon.setHinhAnh(hinhAnhJson);
            // Lưu hóa đơn vào cơ sở dữ liệu
            hoaDonRepository.save(hoaDon);

            // Cập nhật tổng số lượng và tổng tiền cho tất cả hóa đơn cùng orderCode
            hoaDonRepository.updateTongSoLuongAndTongTien(orderCode, tongSoLuong, tongTien);


        }
    }
    private String generateRandomMaDonHang() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder maDonHang = new StringBuilder();
        Random random = new Random();

        // Tạo mã gồm 10 ký tự ngẫu nhiên
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            maDonHang.append(characters.charAt(index));
        }

        return maDonHang.toString();
    }

    @Transactional
    public void addProductsToOrder(String maNguoiDung, long orderCode) {
        // Lấy hóa đơn từ orderCode
        HoaDon hoaDon = hoaDonRepository.findByOrderCode(orderCode);

        if (hoaDon == null) {
            throw new IllegalArgumentException("Hóa đơn không tồn tại với orderCode: " + orderCode);
        }

        // Lấy danh sách giỏ hàng của người dùng
        List<GioHang> gioHangList = gioHangService.findByNguoiDung(maNguoiDung);

        if (gioHangList.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng rỗng, không thể thêm sản phẩm vào đơn hàng");
        }

        // Duyệt qua danh sách giỏ hàng để tạo đơn hàng
        for (GioHang gioHang : gioHangList) {
            DonHang donHang = new DonHang();

            // Gán thông tin cho đơn hàng
            donHang.setMaDonHang(generateRandomMaDonHang()); // Mã đơn hàng ngẫu nhiên
            donHang.setSanPham(gioHang.getSanPham());        // Sản phẩm
            donHang.setNguoiDung(gioHang.getNguoiDung());    // Người dùng
            donHang.setSoLuong(gioHang.getSoLuong());        // Số lượng sản phẩm
            donHang.setDonGia(gioHang.getSanPham().getGia().floatValue()); // Đơn giá
            donHang.setTongTien(gioHang.getSanPham().getGia().floatValue() * gioHang.getSoLuong()); // Tổng tiền
            donHang.setTrangThai("Đang xử lý");              // Trạng thái mặc định
            donHang.setThoiGian(new Date());                 // Thời gian tạo đơn hàng
            donHang.setHoaDon(hoaDon);                       // Liên kết với hóa đơn
            donHang.setKichThuoc(gioHang.getKichThuoc());
            // Lưu đơn hàng vào database
            donHangDao.save(donHang);
        }

        // Xóa các sản phẩm đã được thanh toán khỏi giỏ hàng
        gioHangService.clearCart(maNguoiDung);
    }



    public HoaDon findByOrderCode(long orderCode) {
        return hoaDonRepository.findByOrderCode(orderCode);
    }

    public void updatePaymentHistory(HoaDon hoaDon) {
        hoaDonRepository.save(hoaDon); // Lưu lại bản ghi đã cập nhật
    }

    public void sendInvoiceToEmail(String maNguoiDung, long orderCode) throws MessagingException {
        // Lấy hóa đơn
        HoaDon hoaDon = hoaDonRepository.findByOrderCode(orderCode);
        if (hoaDon == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn với orderCode: " + orderCode);
        }

        // Lấy thông tin người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findByMaNguoiDung(maNguoiDung);
        if (nguoiDung == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với mã: " + maNguoiDung);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(hoaDon.getNgayInHoaDon());
        DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");
        String formattedAmount = currencyFormat.format(hoaDon.getTongTien());
        // Tạo nội dung email với toàn bộ thông tin hóa đơn
        // Tách các chuỗi thành mảng
        String[] tenSanPhamArray = hoaDon.getTenSanPham().replaceAll("[\\[\\]\"]", "").split(",");
        String[] soLuongArray = hoaDon.getSoLuong().replaceAll("[\\[\\]\"]", "").split(",");
        String[] kichThuocArray = hoaDon.getKichThuoc().replaceAll("[\\[\\]\"]", "").split(",");
        String[] giaArray = hoaDon.getGia().replaceAll("[\\[\\]\"]", "").split(",");
        String[] maNhaPhanPhoiArray = hoaDon.getMaNhaPhanPhoi().replaceAll("[\\[\\]\"]", "").split(",");

// Đảm bảo rằng các mảng có cùng chiều dài
        int maxLength = Math.min(tenSanPhamArray.length, Math.min(soLuongArray.length, Math.min(kichThuocArray.length, Math.min(giaArray.length, maNhaPhanPhoiArray.length))));

// Cấu trúc email
        String emailBody = "<!DOCTYPE html>" +
                "<html lang='vi'>" +
                "<head>" +
                " <meta charset='UTF-8'>" +
                " <title>Hóa đơn điện tử từ Gold40</title>" +
                " <style>" +
                " body {" +
                " font-family: Arial, sans-serif;" +
                " margin: 0;" +
                " padding: 0;" +
                " background-color: #f4f4f4;" +
                " }" +
                " .container {" +
                " max-width: 800px;" +
                " max-height: 1200px;" +
                " margin: 20px auto;" +
                " background-color: #fff;" +
                " padding: 20px;" +
                " border: 1px solid #ddd;" +
                " box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);" +
                " }" +
                " h1 {" +
                " color: #333;" +
                " font-size: 24px;" +
                " text-align: center;" +
                " }" +
                " p {" +
                " color: #666;" +
                " line-height: 1.6;" +
                " }" +
                " table {" +
                " width: 100%;" +
                " border-collapse: collapse;" +
                " margin: 20px 0;" +
                " }" +
                " th, td {" +
                " padding: 10px;" +
                " border: 1px solid #ddd;" +
                " text-align: left;" +
                " }" +
                " th {" +
                " background-color: #f8f8f8;" +
                " font-weight: bold;" +
                " }" +
                " .left-column {" +
                " width: 100%;" +
                " float: left;" +
                " padding-right: 20px;" +
                " }" +
                " .right-column {" +
                " width: 100%;" +
                " float: left;" +
                " }" +
                " </style>" +
                "</head>" +
                "<body>" +
                " <div class='container'>" +
                " <h1>Hóa đơn điện tử từ Gold40</h1>" +
                " <p>Xin chào <strong>" + nguoiDung.getHoTen() + "</strong>,</p>" +
                " <p>Cảm ơn bạn đã mua hàng tại Gold40. Dưới đây là thông tin hóa đơn của bạn:</p>" +

                // Bảng bên trái: Thông tin hóa đơn
                " <div class='left-column'>" +
                " <table>" +
                " <tr><th>Mã hóa đơn</th><td>" + hoaDon.getMaHoaDon() + "</td></tr>" +
                " <tr><th>Ngày in hóa đơn</th><td>" + formattedDate + "</td></tr>" +
                " <tr><th>Mã order code</th><td>" + hoaDon.getOrderCode() + "</td></tr>" +
                " <tr><th>Mã người dùng</th><td>" + hoaDon.getMaNguoiDung() + "</td></tr>" +
                " <tr><th>Tổng tiền</th><td>" + formattedAmount + "</td></tr>" +
                " <tr><th>Phương thức thanh toán</th><td>" + hoaDon.getPhuongThuc() + "</td></tr>" +
                " <tr><th>Trạng thái</th><td>" + hoaDon.getTrangThai() + "</td></tr>" +

                " </table>" +
                " </div>" +

                // Bảng bên phải: Thông tin sản phẩm
                " <div class='right-column'>" +
                " <table>" +
                " <tr><th colspan='2'> Thông tin sản phẩm </th></tr>";


        // Thông tin sản phẩm
        for (int i = 0; i < maxLength; i++) {
            double gia = Double.parseDouble(giaArray[i].trim());
            double soLuong = Double.parseDouble(soLuongArray[i].trim());
            double tongTien = gia * soLuong; // Tính tổng tiền cho từng sản phẩm

            emailBody +=
                    " <tr>" +
                            " <td>Sản Phẩm</td><td>" + (i+1) + "</td>" +
                            " </tr>" +
                    " <tr>" +
                            " <td>Mã nhà phân phối</td><td>" + maNhaPhanPhoiArray[i].trim() + "</td>" +
                            " </tr>" +
                            " <tr>" +
                            " <td>Tên sản phẩm</td><td>" + tenSanPhamArray[i].trim() + "</td>" +
                            " </tr>" +
                            " <tr>" +
                            " <td>Số lượng</td><td>" + soLuongArray[i].trim() + "</td>" +
                            " </tr>" +
                            " <tr>" +
                            " <td>Kích thước</td><td>" + kichThuocArray[i].trim() + "</td>" +
                            " </tr>" +
                            " <tr>" +
                            " <td>Giá</td><td>" + giaArray[i].trim() + " VND</td>" +
                            " </tr>" +
                            " <tr>" +
                            " <td>Tổng tiền</td><td>" + String.format("%.0f", tongTien) + " VND</td>" +
                            " </tr>";
        }

        // Đóng các phần của HTML
        emailBody +=
                " </table>" +
                        " </div>" +
                        " <p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email hoặc hotline hỗ trợ. Chúc bạn một ngày tốt lành!</p>" +
                        " </div>" +
                        "</body>" +
                        "</html>";



        // Gửi email
        emailService.sendEmail(nguoiDung.getEmail(), "Hóa đơn từ Gold40", emailBody, true);
    }



}
