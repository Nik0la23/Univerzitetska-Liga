package mk.ukim.finki.wp.liga.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.liga.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("bodyContent", "login");
        return "master_template";
    }

    @PostMapping("/login")
    public String login(@RequestParam String name,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        System.out.println("🌐 Login attempt from web interface - Username: " + name);
        
        return userService.login(name, password)
                .map(user -> {
                    System.out.println("✅ Web login successful for: " + user.getName());
                    session.setAttribute("user", user);
                    return "redirect:/";
                })
                .orElseGet(() -> {
                    System.out.println("❌ Web login failed for: " + name);
                    model.addAttribute("error", "Invalid credentials");
                    model.addAttribute("bodyContent", "login");
                    return "master_template";
                });
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("bodyContent", "register");
        return "master_template";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        try {
            userService.register(name, email, password);
            return "redirect:/auth/login";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("bodyContent", "register");
            return "master_template";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logoutGet(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Debug endpoint to check if users exist
    @GetMapping("/debug/users")
    public String debugUsers(Model model) {
        try {
            var admin = userService.findByName("admin");
            var user1 = userService.findByName("user1");
            
            model.addAttribute("adminExists", admin.isPresent());
            model.addAttribute("user1Exists", user1.isPresent());
            model.addAttribute("adminUser", admin.orElse(null));
            model.addAttribute("user1User", user1.orElse(null));
            
            return "debug_users";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "debug_users";
        }
    }
}


