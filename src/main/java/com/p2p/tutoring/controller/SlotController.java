package com.p2p.tutoring.controller;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.service.common.ServiceResult;
import com.p2p.tutoring.service.SessionUserService;
import com.p2p.tutoring.service.slot.SlotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SlotController {

    private final SessionUserService sessionUserService;
    private final SlotService slotService;

    public SlotController(SessionUserService sessionUserService, SlotService slotService) {
        this.sessionUserService = sessionUserService;
        this.slotService = slotService;
    }

    @GetMapping("/add_slot")
    public String addSlotForm(HttpSession session) {
        if (sessionUserService.getCurrentUser(session).isEmpty()) {
            return "redirect:/login";
        }
        return "add_slot";
    }

    @PostMapping("/add_slot")
    public String addSlot(
            @RequestParam("date") String date,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam(value = "subject", required = false) String subject,
            HttpSession session,
            Model model
    ) {
        User currentUser = sessionUserService.getCurrentUser(session).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        ServiceResult<Void> result = slotService.addSlot(currentUser, date, startTime, endTime, subject);
        if (result.isSuccess()) {
            model.addAttribute("success", "Slot added successfully.");
        } else {
            model.addAttribute("error", result.getMessage());
        }

        return "add_slot";
    }

    @GetMapping("/view_slots")
    public String viewSlots(Model model, HttpSession session) {
        List<Slot> availableSlots = slotService.findAvailableFutureSlots();

        Long currentUserId = sessionUserService.getCurrentUser(session).map(User::getId).orElse(null);

        model.addAttribute("slots", availableSlots);
        model.addAttribute("currentUserId", currentUserId);
        return "view_slots";
    }
}
