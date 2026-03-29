package com.p2p.tutoring.controller;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.SlotRepository;
import com.p2p.tutoring.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class BookingController {

    private final SessionUserService sessionUserService;
    private final SlotRepository slotRepository;

    public BookingController(SessionUserService sessionUserService, SlotRepository slotRepository) {
        this.sessionUserService = sessionUserService;
        this.slotRepository = slotRepository;
    }

    @GetMapping("/book/{slotId}")
    public String bookConfirmation(@PathVariable Long slotId, HttpSession session, Model model) {
        User currentUser = sessionUserService.getCurrentUser(session).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Slot slot = slotRepository.findById(slotId).orElse(null);
        if (slot == null) {
            model.addAttribute("message", "Slot not found.");
            return "book_error";
        }

        if (slot.isBooked()) {
            model.addAttribute("message", "This slot is already booked.");
            return "book_error";
        }

        if (!LocalDateTime.of(slot.getSlotDate(), slot.getStartTime()).isAfter(LocalDateTime.now())) {
            model.addAttribute("message", "Only future slots can be booked.");
            return "book_error";
        }

        if (slot.getTutor().getId().equals(currentUser.getId())) {
            model.addAttribute("message", "Tutors cannot book their own slots.");
            return "book_error";
        }

        model.addAttribute("slot", slot);
        return "book_confirm";
    }

    @PostMapping("/book/{slotId}")
    public String bookSlot(@PathVariable Long slotId, HttpSession session, Model model) {
        User currentUser = sessionUserService.getCurrentUser(session).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Slot slot = slotRepository.findById(slotId).orElse(null);
        if (slot == null) {
            model.addAttribute("message", "Slot not found.");
            return "book_error";
        }

        if (slot.isBooked()) {
            model.addAttribute("message", "This slot is already booked.");
            return "book_error";
        }

        if (slot.getTutor().getId().equals(currentUser.getId())) {
            model.addAttribute("message", "Tutors cannot book their own slots.");
            return "book_error";
        }

        slot.setBooked(true);
        slot.setLearner(currentUser);
        slotRepository.save(slot);

        return "redirect:/dashboard";
    }
}
