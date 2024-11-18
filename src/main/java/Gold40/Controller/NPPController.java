package Gold40.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nppctrl")
public class NPPController {
    @GetMapping("listsp")
    public String listsp(){
return "listsp";
    }
}
