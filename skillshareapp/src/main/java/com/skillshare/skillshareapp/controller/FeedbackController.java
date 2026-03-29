package com.skillshare.skillshareapp.controller;

import com.skillshare.skillshareapp.model.Feedback;
import com.skillshare.skillshareapp.model.MentorshipSession;
import com.skillshare.skillshareapp.model.Student;
import com.skillshare.skillshareapp.service.FeedbackService;
import com.skillshare.skillshareapp.repository.SessionRepository;
import com.skillshare.skillshareapp.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ✅ SHOW ALL FEEDBACKS (USES YOUR feedback-list.html)
    @GetMapping("/feedbacks")
    public String showAllFeedbacks(Model model) {

        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        model.addAttribute("feedbacks", feedbacks);

        return "feedback-list";   // ✅ use existing file
    }

    // ✅ SUBMIT FEEDBACK (CLEANED)
    @PostMapping("/feedback")
    public String submitFeedback(
            @RequestParam Long sessionId,
            @RequestParam Long reviewerId,
            @RequestParam Long rateeId,
            @RequestParam int rating,
            @RequestParam String comment
    ) {

        feedbackService.saveFeedback(sessionId, reviewerId, rateeId, rating, comment);

        return "redirect:/sessions";
    }
}