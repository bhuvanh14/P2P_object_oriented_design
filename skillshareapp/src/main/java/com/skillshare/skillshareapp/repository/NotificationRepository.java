package com.skillshare.skillshareapp.repository;

import com.skillshare.skillshareapp.model.Notification;
import com.skillshare.skillshareapp.model.Student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStudent(Student student);
}