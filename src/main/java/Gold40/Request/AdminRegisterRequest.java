package Gold40.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdminRegisterRequest {
    private String maAdmin; // Mã admin
    private String hoTen; // Họ tên admin
    private String email; // Địa chỉ email admin
    private String taiKhoan; // Tên tài khoản
    private String matKhau; // Mật khẩu
    private String maPin; // Mã pin
    private String recaptchaResponse; // Để xác thực reCAPTCHA
}
