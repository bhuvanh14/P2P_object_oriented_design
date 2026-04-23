package com.p2p.tutoring.controller;

import com.p2p.tutoring.model.User;
import com.p2p.tutoring.service.auth.AuthService;
import com.p2p.tutoring.service.common.ServiceResult;
import com.p2p.tutoring.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AuthService authService;
    private final SessionUserService sessionUserService;

    public AuthController(AuthService authService, SessionUserService sessionUserService) {
        this.authService = authService;
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
        ServiceResult<Void> result = authService.register(name, email, password);
        if (!result.isSuccess()) {
            model.addAttribute("error", result.getMessage());
            return "register1";
        }

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
        ServiceResult<User> result = authService.login(email, password);
        if (!result.isSuccess()) {
            model.addAttribute("error", result.getMessage());
            return "login";
        }

        sessionUserService.login(session, result.getPayload());
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        sessionUserService.logout(session);
        return "redirect:/";
    }
}
