package Gold40.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
@RequestMapping
    public String login() {
    return "login";
}
}
