package com.skillshare.skillshareapp.service;

import com.skillshare.skillshareapp.model.Notification;
import com.skillshare.skillshareapp.model.Student;
import com.skillshare.skillshareapp.repository.NotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // ✅ SEND NOTIFICATION
    public void sendNotification(Student student, String message) {

        Notification notification = new Notification();
        notification.setStudent(student);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setStatus(Notification.Status.UNREAD);

        notificationRepository.save(notification);
    }

    // ✅ GET USER NOTIFICATIONS
    public List<Notification> getNotifications(Student student) {
        return notificationRepository.findByStudent(student);
    }
}