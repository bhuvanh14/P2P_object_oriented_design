package com.p2p.tutoring.controller;

import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.UserRepository;
import com.p2p.tutoring.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.regex.Pattern;

@Controller
public class AuthController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;
    private final SessionUserService sessionUserService;

    public AuthController(UserRepository userRepository, SessionUserService sessionUserService) {
        this.userRepository = userRepository;
        this.sessionUserService = sessionUserService;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register1";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        String normalizedName = name == null ? "" : name.trim();
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        String normalizedPassword = password == null ? "" : password.trim();

        if (normalizedName.isBlank() || normalizedEmail.isBlank() || normalizedPassword.isBlank()) {
            model.addAttribute("error", "Name, email, and password are required.");
            return "register1";
        }

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            model.addAttribute("error", "Invalid email format.");
            return "register1";
        }

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            model.addAttribute("error", "Email already exists.");
            return "register1";
        }

        User user = new User();
        user.setName(normalizedName);
        user.setEmail(normalizedEmail);
        user.setPassword(normalizedPassword);

        userRepository.save(user);
        model.addAttribute("success", "Registration successful. Please login.");
        return "login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        String normalizedPassword = password == null ? "" : password.trim();

        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .filter(user -> user.getPassword().equals(normalizedPassword))
                .map(user -> {
                    sessionUserService.login(session, user);
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invalid email or password.");
                    return "login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        sessionUserService.logout(session);
        return "redirect:/";
    }
}
