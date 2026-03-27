package com.skillshare.skillshareapp.service;

import com.skillshare.skillshareapp.model.Feedback;
import com.skillshare.skillshareapp.model.MentorshipSession;
import com.skillshare.skillshareapp.model.Student;
import com.skillshare.skillshareapp.repository.FeedbackRepository;
import com.skillshare.skillshareapp.repository.SessionRepository;
import com.skillshare.skillshareapp.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ✅ GET ALL FEEDBACKS
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    // ✅ SAVE FEEDBACK (FULL LOGIC)
    public void saveFeedback(Long sessionId, Long reviewerId, Long rateeId, int rating, String comment) {

        boolean exists = feedbackRepository
                .existsBySession_SessionIdAndReviewer_StudentId(sessionId, reviewerId);

        if (exists) return;

        MentorshipSession session = sessionRepository.findById(sessionId).orElse(null);
        Student reviewer = studentRepository.findById(reviewerId).orElse(null);
        Student ratee = studentRepository.findById(rateeId).orElse(null);

        if (session == null || reviewer == null || ratee == null) return;

        Feedback feedback = new Feedback();
        feedback.setSession(session);
        feedback.setReviewer(reviewer);
        feedback.setRatee(ratee);
        feedback.setRating(rating);
        feedback.setComment(comment);

        feedbackRepository.save(feedback);

        // update avg rating
        double sum = 0;
        int count = 0;

        for (Feedback f : feedbackRepository.findAll()) {
            if (f.getRatee() != null &&
                f.getRatee().getStudentId().equals(rateeId)) {
                sum += f.getRating();
                count++;
            }
        }

        if (count > 0) {
            ratee.setAvgRating(sum / count);
            studentRepository.save(ratee);
        }
    }
}