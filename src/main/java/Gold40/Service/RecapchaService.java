package Gold40.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Service
public class RecapchaService {

    @Value("${recaptcha.secret}")
    private String secret;

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyRecaptcha(String token) {
        RestTemplate restTemplate = new RestTemplate();
        String url = RECAPTCHA_VERIFY_URL + "?secret=" + secret + "&response=" + token;

        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
        System.out.println("Response from Google: " + response.getBody()); // Log phản hồi từ Google
        // Kiểm tra nếu phản hồi từ Google hợp lệ
        if (response.getBody() != null) {
            Boolean success = (Boolean) response.getBody().get("success");
            return success != null && success; // Trả về true nếu success không null và true
        }
        return false; // Nếu không có phản hồi hợp lệ, trả về false
    }
}
