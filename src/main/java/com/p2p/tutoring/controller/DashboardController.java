package com.p2p.tutoring.controller;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.SlotRepository;
import com.p2p.tutoring.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final SessionUserService sessionUserService;
    private final SlotRepository slotRepository;

    public DashboardController(SessionUserService sessionUserService, SlotRepository slotRepository) {
        this.sessionUserService = sessionUserService;
        this.slotRepository = slotRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User currentUser = sessionUserService.getCurrentUser(session).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Slot> myTutorSlots = slotRepository.findByTutorOrderBySlotDateDescStartTimeDesc(currentUser);
        List<Slot> myLearnerSlots = slotRepository.findByLearnerOrderBySlotDateDescStartTimeDesc(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("myTutorSlots", myTutorSlots);
        model.addAttribute("myLearnerSlots", myLearnerSlots);

        model.addAttribute("subjects", currentUser.getSubjects() == null ? "Not set" : currentUser.getSubjects());
        model.addAttribute("requests", "No request workflow is wired yet.");

        return "dashboard";
    }
}
