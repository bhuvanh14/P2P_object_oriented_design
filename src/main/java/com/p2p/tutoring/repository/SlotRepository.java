package com.p2p.tutoring.repository;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(LocalDate date);

    List<Slot> findByTutorOrderBySlotDateDescStartTimeDesc(User tutor);

    List<Slot> findByLearnerOrderBySlotDateDescStartTimeDesc(User learner);

    List<Slot> findByTutorAndSlotDateOrderByStartTimeAsc(User tutor, LocalDate date);
}
