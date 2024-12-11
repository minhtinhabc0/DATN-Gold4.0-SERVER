package Gold40.Controller;

import Gold40.Service.LanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/language")
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping("/{lang}")
    public ResponseEntity<Map<String, String>> getLanguage(@PathVariable String lang) {
        try {
            Map<String, String> languageData = languageService.getLanguageData(lang);
            return ResponseEntity.ok(languageData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error loading language file: " + lang));
        }
    }

}
