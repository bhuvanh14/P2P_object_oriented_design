package com.skillshare.skillshareapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MentorshipSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @ManyToOne
    private Student mentor;

    @ManyToOne
    private Student mentee;

    @ManyToOne
    private Skill skill;

    private LocalDateTime requestTime;
    private LocalDateTime scheduleTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED,
        CANCELLED,
        COMPLETED
    }

    // ===== GETTERS & SETTERS =====

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Student getMentor() { return mentor; }
    public void setMentor(Student mentor) { this.mentor = mentor; }

    public Student getMentee() { return mentee; }
    public void setMentee(Student mentee) { this.mentee = mentee; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }

    public LocalDateTime getScheduleTime() { return scheduleTime; }
    public void setScheduleTime(LocalDateTime scheduleTime) { this.scheduleTime = scheduleTime; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}