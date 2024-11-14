package Gold40.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcr0bghdp",
                "api_key", "324571541243998",
                "api_secret", "9qkIzV4MT0uRXvlVwSFkpZrHUEo"));
    }
}
