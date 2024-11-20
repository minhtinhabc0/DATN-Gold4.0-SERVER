package Gold40.Controller;

import Gold40.Entity.LichSuNap;
import Gold40.Entity.NguoiDung;
import Gold40.Entity.PaymentRequest;
import Gold40.Service.PaymentService;
import Gold40.Service.TaiKhoanService;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
    @RequestMapping(value = "/create-payment-link", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> checkout(@RequestBody PaymentRequest paymentRequest, @RequestHeader("Authorization") String token) {
        token = extractToken(token);
        if (token == null) {
            Map<String, String> unauthorizedResponse = new HashMap<>();
            unauthorizedResponse.put("error", "Bạn không có quyền truy cập");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedResponse);
        }

        try {
            final String productName = paymentRequest.getProductName();
            final String description = "Thanh toán đơn hàng";
            String successUrl = "http://127.0.0.1:5500/api/checkout/success.html";
            String cancelUrl = "http://127.0.0.1:5501/user/index.html#!/user/spvang";
            final int quantity = paymentRequest.getQuantity();
            final int price = paymentRequest.getPrice();

            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

            ItemData item = ItemData.builder().name(productName).quantity(quantity).price(price).build();
            PaymentData paymentData = PaymentData.builder().orderCode(orderCode).amount(price).description(description)
                    .returnUrl(successUrl).cancelUrl(cancelUrl).item(item).build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            // Lưu vào lịch sử nạp với trạng thái "đang xử lý"
            String taikhoan = jwtUtil.extractUsername(token);
            String maNguoiDung = taiKhoanService.findByTaikhoan(taikhoan).getManguoidung();
            System.out.println(maNguoiDung);
            paymentService.savePaymentHistory(maNguoiDung, "đang xử lý", quantity, price,orderCode);

            // Trả về checkoutUrl để chuyển hướng người dùng
            String checkoutUrl = data.getCheckoutUrl();
            Map<String, String> response = new HashMap<>();
            response.put("checkoutUrl", checkoutUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error creating payment link");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
