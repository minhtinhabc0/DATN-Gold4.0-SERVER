package Gold40.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebConfig implements WebMvcConfigurer {

    // Đảm bảo cấu hình CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Chỉ áp dụng cho các API
                .allowedOrigins("http://127.0.0.1:5500","http://127.0.0.1:5501") // Địa chỉ frontend của bạn
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Các phương thức cho phép
                .allowCredentials(true); // Cho phép cookies
    }

    @Bean
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors() // Kích hoạt CORS
                .and()
                .csrf().disable() // Vô hiệu hóa CSRF để thử nghiệm
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/api/auth/**", "/api/profile/**", "/api/re/**", "/api/ad/**",
                                "/api/user/**", "/api/users/**", "/api/donhang/**", "/api/hoadon/**",
                                "/api/npp/**", "/api/npp/verify-otp", "/api/adctrl/**", "/api/nppctrl/**",
                                "/api/gold-prices", "/api/upload/**", "/api/products/**",
                                "/api/checkout/**", "/api/baocaonpp/**","/api/yeuthich/**"
                        ).permitAll() // Đường dẫn confirm-webhook không yêu cầu token
                        .anyRequest().authenticated() // Các yêu cầu khác phải được xác thực
                );

        return http.build();
    }


}
