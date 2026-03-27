package com.skillshare.skillshareapp.model;

import jakarta.persistence.*;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne
    private MentorshipSession session;

    @ManyToOne
    private Student reviewer;

    @ManyToOne
    private Student ratee;

    private int rating;
    private String comment;

    // ===== GETTERS =====

    public Long getFeedbackId() { return feedbackId; }

    public MentorshipSession getSession() { return session; }
    public void setSession(MentorshipSession session) { this.session = session; }

    public Student getReviewer() { return reviewer; }
    public void setReviewer(Student reviewer) { this.reviewer = reviewer; }

    public Student getRatee() { return ratee; }
    public void setRatee(Student ratee) { this.ratee = ratee; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}