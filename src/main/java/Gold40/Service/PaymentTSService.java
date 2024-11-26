package Gold40.Service;

import Gold40.DAO.*;
import Gold40.Entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;

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

}
