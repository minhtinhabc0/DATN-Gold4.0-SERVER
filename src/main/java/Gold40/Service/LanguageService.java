package Gold40.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class LanguageService {

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, String> getLanguageData(String lang) throws IOException {
        // Sử dụng classpath để truy xuất tệp JSON
        ClassPathResource resource = new ClassPathResource("i18n/" + lang + ".json");
        return objectMapper.readValue(resource.getFile(), Map.class);
    }
}
