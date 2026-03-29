package com.skillshare.skillshareapp.controller;

import com.skillshare.skillshareapp.model.Student;
import com.skillshare.skillshareapp.repository.StudentRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private StudentRepository studentRepo;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam Long studentId,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Student user = studentRepo.findByStudentId(studentId);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/home";
        }

        model.addAttribute("error", "Invalid credentials");
        return "login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}