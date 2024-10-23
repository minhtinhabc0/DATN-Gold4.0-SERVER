package Gold40.Controller;

import Gold40.Entity.NguoiDung;
import Gold40.Service.NguoiDungService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
// Import các thư viện cần thiết

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final Cloudinary cloudinary;

    @Autowired
    private NguoiDungService nguoiDungService; // Tiêm service vào controller

    public UploadController() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcr0bghdp",
                "api_key", "324571541243998",
                "api_secret", "9qkIzV4MT0uRXvlVwSFkpZrHUEo"));
    }

    @PostMapping("/{manguoidung}")
    public ResponseEntity<String> uploadImage(@PathVariable("manguoidung") String maNguoiDung, @RequestParam("file") MultipartFile file) {
        try {
            // Tải ảnh lên Cloudinary
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) result.get("url"); // Nhận URL của ảnh đã upload

            // Lưu URL vào NguoiDung
            NguoiDung nguoiDung = nguoiDungService.findByMaNguoiDung(maNguoiDung);
            if (nguoiDung != null) {
                nguoiDung.setAvt(imageUrl); // Lưu URL vào trường avt
                nguoiDungService.save(nguoiDung); // Lưu lại NguoiDung với URL mới
                return ResponseEntity.ok(imageUrl); // Trả về URL
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
