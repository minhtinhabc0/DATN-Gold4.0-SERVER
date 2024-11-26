package Gold40.Controller;

import Gold40.DAO.GcoinDAO;
import Gold40.Entity.*;
import Gold40.Service.*;
import Gold40.Util.JwtUtil;
import com.twilio.twiml.voice.Pay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/checkoutTS")
public class CheckoutTSController {

    private final PayOS payOS;
    private final PaymentTSService paymentTSService;
    private final JwtUtil jwtUtil;
    private final TaiKhoanService taiKhoanService;
    private final GioHangService gioHangService;
    private final NguoiDungService nguoiDungService;

    @Autowired
    public CheckoutTSController(PayOS payOS, PaymentTSService paymentTSService, JwtUtil jwtUtil, TaiKhoanService taiKhoanService,GioHangService gioHangService,NguoiDungService nguoiDungService) {
        this.payOS = payOS;
        this.paymentTSService = paymentTSService;
        this.jwtUtil = jwtUtil;
        this.taiKhoanService = taiKhoanService;
        this.nguoiDungService = nguoiDungService;
         this.gioHangService=gioHangService;
    }

    private String extractToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
    }

    @PostMapping(path = "/create")
    public ResponseEntity<Map<String, String>> createPaymentLink(@RequestHeader("Authorization") String token) {
        token = extractToken(token);
        if (token == null) {
            Map<String, String> unauthorizedResponse = new HashMap<>();
            unauthorizedResponse.put("error", "Bạn không có quyền truy cập");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedResponse);
        }

        try {
            // Lấy tài khoản từ token
            String taikhoan = jwtUtil.extractUsername(token);
            String maNguoiDung = taiKhoanService.findByTaikhoan(taikhoan).getManguoidung();

            // Lấy danh sách sản phẩm trong giỏ hàng
            List<GioHang> gioHangList = gioHangService.findByNguoiDung(maNguoiDung); // Thêm phương thức này trong service
            String maNhaPhanPhoi = String.valueOf(gioHangList.get(0).getSanPham().getNhaPhanPhoi().getMaNhaPhanPhoi());
            if (gioHangList.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Giỏ hàng trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Tính tổng số tiền và tạo danh sách các ItemData
            int totalAmount = 0;
            List<ItemData> items = new ArrayList<>();
            for (GioHang item : gioHangList) {
                int itemPrice = item.getSanPham().getGia().intValue() + item.getSanPham().getTienCong();
                int quantity = item.getSoLuong();
                totalAmount += itemPrice * quantity;
                String kichThuoc = item.getSanPham().getKichCo(); // Lấy kích thước của sản phẩm
                items.add(ItemData.builder()
                        .name(item.getSanPham().getTenSanPham() + "Kích Cỡ "+ item.getSanPham().getKichCo())
                        .quantity(quantity)
                        .price(itemPrice)
                        .build());
            }

            // Tạo orderCode dựa trên thời gian hiện tại
            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

            // Tạo dữ liệu thanh toán
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .description("Thanh toán giỏ hàng")
                    .amount(totalAmount)
                    .items(items)
                    .returnUrl("http://localhost:9999/api/checkoutTS/status")
                    .cancelUrl("http://localhost:9999/api/checkoutTS/status")
                    .build();



            // In dữ liệu sẽ lưu vào bảng Hoa_Don
            System.out.println("=== Dữ liệu chuẩn bị lưu vào bảng Hoa_Don ===");
            System.out.println("Mã hóa đơn (mahoadon): " + orderCode); // Mã hóa đơn
            System.out.println("Ngày in hóa đơn (ngayinhoadon): " + new Date()); // Ngày tạo hóa đơn
            System.out.println("Mã người dùng (manguoidung): " + maNguoiDung); // Mã người dùng
            System.out.println("Mã nhà phân phối (manhaphanphoi): " + maNhaPhanPhoi); // Mã nhà phân phối
            System.out.println("Tổng tiền (tongtien): " + totalAmount); // Tổng tiền
            System.out.println("Phương thức thanh toán (phuongthuc): " + "Chuyển khoản"); // Phương thức thanh toán
            System.out.println("Trạng thái (trangthai): " + "đang xử lý"); // Trạng thái thanh toán

            // In giá, số lượng và tên sản phẩm cho từng sản phẩm
            for (GioHang item : gioHangList) {
                System.out.println("Giá sản phẩm (gia): " + item.getSanPham().getGia());
                System.out.println("Số lượng (soluong): " + item.getSoLuong());
                System.out.println("Tên sản phẩm (tensanpham): " + item.getSanPham().getTenSanPham());
                System.out.println("Kích Thước sản phẩm (tensanpham): " + item.getSanPham().getKichCo());
            }
            int totalQuantity = gioHangList.stream().mapToInt(GioHang::getSoLuong).sum();
            System.out.println("Tổng số lượng (tongsoluong): " + totalQuantity); // Tổng số lượng sản phẩm
            // Gọi PayOS API để tạo liên kết thanh toán
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            // Lưu lịch sử thanh toán với trạng thái "đang xử lý"
            paymentTSService.savePaymentGioHangToHoaDon(maNguoiDung, "Chuyển khoản", "đang xử lý", orderCode);

            // Trả về dữ liệu thanh toán
            Map<String, String> response = new HashMap<>();
            response.put("checkoutUrl", data.getCheckoutUrl());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi tạo liên kết thanh toán");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getPaymentStatus(@RequestParam String code, @RequestParam String id,
                                                   @RequestParam String status, @RequestParam long orderCode) {
        // Kiểm tra trạng thái thanh toán là PAID (thành công)
        if ("PAID".equals(status)) {
            try {
                // Tìm hóa đơn theo orderCode
                HoaDon hoaDon = paymentTSService.findByOrderCode(orderCode);

                if (hoaDon != null) {
                    // Cập nhật trạng thái thành công
                    hoaDon.setTrangThai("thành công");
                    paymentTSService.updatePaymentHistory(hoaDon);
                    paymentTSService.addProductsToOrder(hoaDon.getMaNguoiDung(), orderCode);
                    // Trả về mã trạng thái 200 OK và chuyển hướng
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(URI.create("http://127.0.0.1:5501/user/index.html#!/user/profileuser#pills-order"))
                            .build();
                } else {
                    // Nếu không tìm thấy hóa đơn, trả về lỗi
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Hóa đơn không tồn tại");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse.toString());
                }
            } catch (Exception e) {
                // Log lỗi và trả về lỗi 500
                e.printStackTrace();
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Lỗi khi xử lý đơn hàng");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse.toString());
            }
        }

        // Kiểm tra trạng thái thanh toán là CANCEL (hủy)
        else if ("CANCELLED".equals(status)) {
            try {
                // Tìm hóa đơn theo orderCode
                HoaDon hoaDon = paymentTSService.findByOrderCode(orderCode);

                if (hoaDon != null) {
                    // Cập nhật trạng thái hủy
                    hoaDon.setTrangThai("hủy");

                    paymentTSService.updatePaymentHistory(hoaDon);

                    // Trả về mã trạng thái 200 OK và chuyển hướng
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(URI.create("http://127.0.0.1:5501/user/index.html#!/user/giohang"))
                            .build();
                } else {
                    // Nếu không tìm thấy hóa đơn, trả về lỗi
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Hóa đơn không tồn tại");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse.toString());
                }
            } catch (Exception e) {
                // Log lỗi và trả về lỗi 500
                e.printStackTrace();
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Lỗi khi xử lý đơn hàng");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse.toString());
            }
        } else {
            // Trạng thái không hợp lệ, trả về lỗi
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Trạng thái không hợp lệ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.toString());
        }
    }
}



