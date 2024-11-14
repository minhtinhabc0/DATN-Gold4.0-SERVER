package Gold40.Config;

import Gold40.Util.JwtAuthenticationFilter;
import Gold40.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity // Ensure Spring scans the Gold40 package for security configurations
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;  // Autowire JwtAuthenticationFilter

    // CORS Configuration - This will allow requests from the frontend to the API
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Apply only to /api/** endpoints
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:4200")  // Frontend URLs (add more if needed)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")  // Allowed HTTP methods (include PATCH if needed)
                .allowCredentials(true);  // Allow credentials (cookies, etc.)
    }

    // Security Filter Configuration
    @Bean
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()  // Enable CORS
                .and()
                .csrf().disable()  // Disable CSRF protection (if you're using JWT, this is typically fine)
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        // Define public routes
                        .requestMatchers("/api/auth/**", "/api/profile/**", "/api/re/**",
                                "/api/ad/**", "/api/user/**", "/api/users/**",
                                "/api/gold-prices", "/api/upload/**", "/api/products/**","/api/npp/**")
                        .permitAll()



                        // All other routes require authentication
                        .anyRequest().authenticated()
                )
                // Add the JWT filter before the UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
