package Gold40.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcr0bghdp", // Thay bằng Cloud Name của bạn
                "api_key", "324571541243998",       // Thay bằng API Key của bạn
                "api_secret", "9qkIzV4MT0uRXvlVwSFkpZrHUEo" // Thay bằng API Secret của bạn
        ));
    }

    public String uploadFile(MultipartFile file) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
        return uploadResult.get("url").toString(); // Trả về URL của file đã tải lên
    }
}
