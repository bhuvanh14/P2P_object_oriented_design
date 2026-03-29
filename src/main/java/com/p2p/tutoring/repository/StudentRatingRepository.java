package com.p2p.tutoring.repository;

import com.p2p.tutoring.model.StudentRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRatingRepository extends JpaRepository<StudentRating, Long> {
}
