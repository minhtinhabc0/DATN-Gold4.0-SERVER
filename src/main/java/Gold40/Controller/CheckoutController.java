package Gold40.Controller;

import Gold40.Entity.PaymentRequest;
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
    @Autowired
    private JwtUtil jwtUtil;
    private String extractToken(String token) {
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
    }

    public CheckoutController(PayOS payOS) {
        this.payOS = payOS;
    }
    @CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
    @RequestMapping(value = "/create-payment-link", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> checkout(@RequestBody PaymentRequest paymentRequest,@RequestHeader("Authorization") String token) {
        token = extractToken(token);
        if (token == null) {
            // Đặt thông báo lỗi vào Map để trả về đúng kiểu dữ liệu
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

            String checkoutUrl = data.getCheckoutUrl();

            // Trả về JSON thay vì plain text
            Map<String, String> response = new HashMap<>();
            response.put("checkoutUrl", checkoutUrl);

            return ResponseEntity.ok(response);  // Trả về Map dạng JSON
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error creating payment link");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

