package com.skillshare.skillshareapp.repository;

import com.skillshare.skillshareapp.model.MentorshipSession;
import com.skillshare.skillshareapp.model.Student;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<MentorshipSession, Long> {

    // ✅ FETCH WITH ALL RELATIONS
    @Query("SELECT s FROM MentorshipSession s " +
           "JOIN FETCH s.mentor " +
           "JOIN FETCH s.mentee " +
           "JOIN FETCH s.skill " +
           "WHERE s.sessionId = :id")
    Optional<MentorshipSession> findByIdWithDetails(@Param("id") Long id);

    // ✅ FIXED (use entity relationships)
    List<MentorshipSession> findByMentor(Student mentor);

    List<MentorshipSession> findByMentee(Student mentee);
}