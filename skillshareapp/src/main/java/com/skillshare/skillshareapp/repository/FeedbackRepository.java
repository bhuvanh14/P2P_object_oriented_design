package com.skillshare.skillshareapp.repository;

import com.skillshare.skillshareapp.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // ✅ Check if feedback exists for a session
    boolean existsBySession_SessionId(Long sessionId);

    // (keep this also if you want)
    boolean existsBySession_SessionIdAndReviewer_StudentId(Long sessionId, Long reviewerId);
}