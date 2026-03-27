package com.skillshare.skillshareapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notification_id;   // ✅ ONLY ONE ID

    private String message;
    private boolean readStatus = false;

    @ManyToOne
    private Student student;

    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        UNREAD,
        READ
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() { return notification_id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}