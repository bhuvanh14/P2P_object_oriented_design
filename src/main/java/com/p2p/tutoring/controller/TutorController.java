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

@Controller
public class TutorController {

    private final SessionUserService sessionUserService;
    private final UserRepository userRepository;

    public TutorController(SessionUserService sessionUserService, UserRepository userRepository) {
        this.sessionUserService = sessionUserService;
        this.userRepository = userRepository;
    }

    @GetMapping("/become_tutor")
    public String becomeTutorForm(HttpSession session) {
        if (sessionUserService.getCurrentUser(session).isEmpty()) {
            return "redirect:/login";
        }
        return "become_tutor";
    }

    @PostMapping("/become_tutor")
    public String becomeTutor(
            @RequestParam String subjects,
            @RequestParam String contact,
            HttpSession session,
            Model model
    ) {
        User currentUser = sessionUserService.getCurrentUser(session).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        String normalizedSubjects = subjects == null ? "" : subjects.trim();
        String normalizedContact = contact == null ? "" : contact.trim();

        if (normalizedSubjects.isBlank() || normalizedContact.isBlank()) {
            model.addAttribute("error", "Subjects and contact details are required.");
            return "become_tutor";
        }

        currentUser.setSubjects(normalizedSubjects);
        currentUser.setContact(normalizedContact);
        currentUser.setRole("tutor");
        userRepository.save(currentUser);

        return "redirect:/dashboard";
    }
}
