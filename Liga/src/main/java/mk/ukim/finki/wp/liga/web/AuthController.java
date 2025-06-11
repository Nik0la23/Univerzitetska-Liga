package mk.ukim.finki.wp.liga.web;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mk.ukim.finki.wp.liga.model.Role;
import mk.ukim.finki.wp.liga.service.AuthService;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        // Basic validation
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Username is required");
            return "register";
        }
        
        if (password == null || password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long");
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        try {
            Set<String> roles = new HashSet<>();
            roles.add("ROLE_" + Role.USER.name());
            authService.register(username, password, roles);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}

@Controller
@RequestMapping("/api/auth")
class AuthRestController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerApi(@RequestParam String username, @RequestParam String password) {
        try {
            Set<String> roles = new HashSet<>();
            roles.add("ROLE_" + Role.USER.name());
            String token = authService.register(username, password, roles);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginApi(@RequestParam String username, @RequestParam String password) {
        try {
            String token = authService.login(username, password);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
