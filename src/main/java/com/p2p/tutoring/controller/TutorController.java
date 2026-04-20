package com.p2p.tutoring.controller;

import com.p2p.tutoring.model.User;
import com.p2p.tutoring.service.common.ServiceResult;
import com.p2p.tutoring.service.SessionUserService;
import com.p2p.tutoring.service.profile.TutorProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TutorController {

    private final SessionUserService sessionUserService;
    private final TutorProfileService tutorProfileService;

    public TutorController(SessionUserService sessionUserService, TutorProfileService tutorProfileService) {
        this.sessionUserService = sessionUserService;
        this.tutorProfileService = tutorProfileService;
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

        ServiceResult<Void> result = tutorProfileService.becomeTutor(currentUser, subjects, contact);
        if (!result.isSuccess()) {
            model.addAttribute("error", result.getMessage());
            return "become_tutor";
        }

        sessionUserService.updateRole(session, "tutor");

        return "redirect:/dashboard";
    }
}
