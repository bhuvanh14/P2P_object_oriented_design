package com.skillshare.skillshareapp.repository;

import com.skillshare.skillshareapp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Get mentors (year >= 3)
    List<Student> findByYearGreaterThanEqual(int year);
   
    Student findByStudentId(Long studentId);
    List<Student> findByRole(String role);
    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRole(
        String firstName,
        String lastName,
        String role
    );

}