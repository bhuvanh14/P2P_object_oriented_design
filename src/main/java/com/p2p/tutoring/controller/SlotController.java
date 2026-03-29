package com.p2p.tutoring.controller;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.SlotRepository;
import com.p2p.tutoring.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
public class SlotController {

    private final SessionUserService sessionUserService;
    private final SlotRepository slotRepository;

    public SlotController(SessionUserService sessionUserService, SlotRepository slotRepository) {
        this.sessionUserService = sessionUserService;
        this.slotRepository = slotRepository;
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

        try {
            LocalDate slotDate = LocalDate.parse(date.trim());
            LocalTime start = LocalTime.parse(startTime.trim());
            LocalTime end = LocalTime.parse(endTime.trim());

            if (!end.isAfter(start)) {
                model.addAttribute("error", "End time must be after start time.");
                return "add_slot";
            }

            LocalDateTime startDateTime = LocalDateTime.of(slotDate, start);
            if (!startDateTime.isAfter(LocalDateTime.now())) {
                model.addAttribute("error", "Slot must be scheduled in the future.");
                return "add_slot";
            }

            List<Slot> sameDaySlots = slotRepository.findByTutorAndSlotDateOrderByStartTimeAsc(currentUser, slotDate);
            boolean overlaps = sameDaySlots.stream()
                    .anyMatch(existing -> start.isBefore(existing.getEndTime()) && end.isAfter(existing.getStartTime()));

            if (overlaps) {
                model.addAttribute("error", "This slot overlaps an existing slot on the same date.");
                return "add_slot";
            }

            Slot slot = new Slot();
            slot.setTutor(currentUser);
            slot.setSlotDate(slotDate);
            slot.setStartTime(start);
            slot.setEndTime(end);
            slot.setSubject(subject == null ? "" : subject.trim());
            slot.setBooked(false);

            slotRepository.save(slot);
            model.addAttribute("success", "Slot added successfully.");
            return "add_slot";
        } catch (DateTimeParseException ex) {
            model.addAttribute("error", "Invalid date/time format. Use YYYY-MM-DD and HH:MM.");
            return "add_slot";
        }
    }

    @GetMapping("/view_slots")
    public String viewSlots(Model model, HttpSession session) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<Slot> availableSlots = slotRepository
                .findByBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(today)
                .stream()
                .filter(slot -> LocalDateTime.of(slot.getSlotDate(), slot.getStartTime()).isAfter(now))
                .toList();

        Long currentUserId = sessionUserService.getCurrentUser(session).map(User::getId).orElse(null);

        model.addAttribute("slots", availableSlots);
        model.addAttribute("currentUserId", currentUserId);
        return "view_slots";
    }
}
