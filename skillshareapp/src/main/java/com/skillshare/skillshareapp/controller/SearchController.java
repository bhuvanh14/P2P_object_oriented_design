package com.skillshare.skillshareapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.skillshare.skillshareapp.model.MentorshipSession;
import com.skillshare.skillshareapp.model.Student;
import com.skillshare.skillshareapp.repository.StudentRepository;
import com.skillshare.skillshareapp.service.NotificationService;
import com.skillshare.skillshareapp.service.SearchService;
import com.skillshare.skillshareapp.service.SessionService;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import com.skillshare.skillshareapp.repository.StudentRepository;

@Controller
public class SearchController {

    @Autowired
    private SearchService service;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private StudentRepository studentRepository;
    
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String query,
            Model model,
            HttpSession session
    ) {

        Student user = (Student) session.getAttribute("user");

        if (user == null) return "redirect:/login";

        List<Student> mentors;

        if (query == null || query.isEmpty()) {
            mentors = studentRepository.findByRole("MENTOR");
        } else {
            mentors = studentRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRole(
                    query, query, "MENTOR"
                );
        }

        model.addAttribute("mentors", mentors);
        model.addAttribute("query", query);

        return "search";
    }
    @PostMapping("/respond")
    public String respondSession(
            @RequestParam Long sessionId,
            @RequestParam String status
    ) {

        sessionService.respondSession(
                sessionId,
                MentorshipSession.Status.valueOf(status)
        );

        return "redirect:/sessions";
    }
}