package com.skillshare.skillshareapp.controller;

import com.skillshare.skillshareapp.model.Student;
import com.skillshare.skillshareapp.repository.StudentRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class MentorController {
    
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/mentors")
    public String showMentors(HttpSession session, Model model) {

        Student user = (Student) session.getAttribute("user");

        if (user == null || !"STUDENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<Student> mentors = studentRepository.findByRole("MENTOR");

        model.addAttribute("mentors", mentors);

        return "mentors";
    }
}