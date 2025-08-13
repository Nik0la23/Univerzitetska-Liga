package mk.ukim.finki.wp.liga.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        return "redirect:/matches";
    }
}


