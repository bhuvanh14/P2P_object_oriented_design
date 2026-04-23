package com.p2p.tutoring.controller;

import com.p2p.tutoring.model.User;
import com.p2p.tutoring.service.dashboard.DashboardService;
import com.p2p.tutoring.service.dashboard.DashboardView;
import com.p2p.tutoring.service.SessionUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private final SessionUserService sessionUserService;
    private final DashboardService dashboardService;

    public DashboardController(SessionUserService sessionUserService, DashboardService dashboardService) {
        this.sessionUserService = sessionUserService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model,
            @RequestParam(value = "error", required = false) String error
    ) {
        User currentUser = sessionUserService.getCurrentUser(session).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        DashboardView view = dashboardService.buildDashboard(currentUser);

        model.addAttribute("user", view.user());
        model.addAttribute("myTutorSlots", view.myTutorSlots());
        model.addAttribute("myLearnerSlots", view.myLearnerSlots());

        model.addAttribute("subjects", view.subjects());
        model.addAttribute("requests", view.requests());

        if (error != null && !error.isBlank()) {
            model.addAttribute("error", error);
        }

        return "dashboard";
    }
}
