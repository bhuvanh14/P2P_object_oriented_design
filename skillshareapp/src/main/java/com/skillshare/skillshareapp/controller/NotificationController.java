package com.skillshare.skillshareapp.controller;

import com.skillshare.skillshareapp.model.Student;
import com.skillshare.skillshareapp.repository.StudentRepository;
import com.skillshare.skillshareapp.service.NotificationService;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/send")
    public String sendNotification(
            @RequestParam Long studentId,
            @RequestParam String message
    ) {

        Student student = studentRepository.findById(studentId).orElseThrow();

        notificationService.sendNotification(student, message);

        return "redirect:/";
    }
    @GetMapping
    public String viewNotifications(HttpSession session, Model model) {

        Student user = (Student) session.getAttribute("user");

        if (user == null) return "redirect:/login";

        model.addAttribute(
                "notifications",
                notificationService.getNotifications(user)
        );

        return "notifications";
    }
    
}