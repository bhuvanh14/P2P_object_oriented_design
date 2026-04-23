package com.p2p.tutoring.controller;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.service.booking.BookingService;
import com.p2p.tutoring.service.common.ServiceResult;
import com.p2p.tutoring.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookingController {

    private final SessionUserService sessionUserService;
    private final BookingService bookingService;

    public BookingController(SessionUserService sessionUserService, BookingService bookingService) {
        this.sessionUserService = sessionUserService;
        this.bookingService = bookingService;
    }

    @GetMapping("/book/{slotId}")
    public String bookConfirmation(@PathVariable Long slotId, HttpSession session, Model model) {
        User currentUser = sessionUserService.getCurrentUser(session).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        ServiceResult<Slot> result = bookingService.getBookableSlot(slotId, currentUser);
        if (!result.isSuccess()) {
            model.addAttribute("message", result.getMessage());
            return "book_error";
        }

        model.addAttribute("slot", result.getPayload());
        return "book_confirm";
    }

    @PostMapping("/book/{slotId}")
    public String bookSlot(@PathVariable Long slotId, HttpSession session, Model model) {
        User currentUser = sessionUserService.getCurrentUser(session).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        ServiceResult<Void> result = bookingService.bookSlot(slotId, currentUser);
        if (!result.isSuccess()) {
            model.addAttribute("message", result.getMessage());
            return "book_error";
        }

        return "redirect:/dashboard";
    }
}
