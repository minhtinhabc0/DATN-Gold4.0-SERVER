package Gold40.Controller;

import Gold40.Entity.LichSuNap;
import Gold40.Entity.PaymentRequest;
import Gold40.Service.PaymentService;
import Gold40.Service.TaiKhoanService;
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
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final PayOS payOS;
    private final PaymentService paymentService;
    private final JwtUtil jwtUtil;
    private final TaiKhoanService taiKhoanService;

    @Autowired
    public CheckoutController(PayOS payOS, PaymentService paymentService, JwtUtil jwtUtil, TaiKhoanService taiKhoanService) {
        this.payOS = payOS;
        this.paymentService = paymentService;
        this.jwtUtil = jwtUtil;
        this.taiKhoanService = taiKhoanService;
    }

    private String extractToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
    }

    @CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:9999"})
    @PostMapping(path = "/create")
    public ResponseEntity<Map<String, String>> createPaymentLink(@RequestBody PaymentRequest paymentRequest, @RequestHeader("Authorization") String token) {
        token = extractToken(token);
        if (token == null) {
            Map<String, String> unauthorizedResponse = new HashMap<>();
            unauthorizedResponse.put("error", "Bạn không có quyền truy cập");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedResponse);
        }

        try {
            final String productName = paymentRequest.getProductName();
            final String description = "Thanh toán đơn hàng";
            String returnUrl = "http://localhost:9999/api/checkout/status";
            String cancelUrl = "http://localhost:9999/api/checkout/status";
            final int quantity = paymentRequest.getQuantity();
            final int price = paymentRequest.getPrice();

            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

            ItemData item = ItemData.builder().name(productName).quantity(quantity).price(price).build();
            PaymentData paymentData = PaymentData.builder().orderCode(orderCode).description(description).amount(price)
                    .item(item).returnUrl(returnUrl).cancelUrl(cancelUrl).build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            // Lưu vào lịch sử nạp với trạng thái "đang xử lý"
            String taikhoan = jwtUtil.extractUsername(token);
            String maNguoiDung = taiKhoanService.findByTaikhoan(taikhoan).getManguoidung();
            paymentService.savePaymentHistory(maNguoiDung, "đang xử lý", quantity, price, orderCode);

            // Trả về dữ liệu thanh toán và checkoutUrl
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> response = new HashMap<>();
            response.put("checkoutUrl", data.getCheckoutUrl());  // Đưa URL thanh toán vào response

            // Lưu toàn bộ dữ liệu trả về từ PayOS vào "data"
            response.put("data", objectMapper.writeValueAsString(data));  // Chuyển đối tượng thành chuỗi JSON

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
                // Tìm đơn hàng theo orderCode
                LichSuNap lichSuNap = paymentService.findByOrderCode(orderCode);

                if (lichSuNap != null) {
                    // Cập nhật trạng thái thành công mà không tạo hóa đơn mới
                    lichSuNap.setTrangThai("thành công");
                    paymentService.updatePaymentHistory(lichSuNap);  // Cập nhật thay vì lưu mới

                    // Trả về mã trạng thái 200 OK và chuyển hướng
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(URI.create("http://127.0.0.1:5501/user/index.html#!/user/spvang"))
                            .build();
                } else {
                    // Nếu không tìm thấy đơn hàng, trả về lỗi
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Đơn hàng không tồn tại");
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
                // Tìm đơn hàng theo orderCode
                LichSuNap lichSuNap = paymentService.findByOrderCode(orderCode);

                if (lichSuNap != null) {
                    // Cập nhật trạng thái hủy mà không tạo hóa đơn mới
                    lichSuNap.setTrangThai("hủy");
                    paymentService.updatePaymentHistory(lichSuNap);  // Cập nhật thay vì lưu mới

                    // Trả về mã trạng thái 200 OK và chuyển hướng
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(URI.create("http://127.0.0.1:5501/user/index.html#!/user/spvang"))
                            .build();
                } else {
                    // Nếu không tìm thấy đơn hàng, trả về lỗi
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Đơn hàng không tồn tại");
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



