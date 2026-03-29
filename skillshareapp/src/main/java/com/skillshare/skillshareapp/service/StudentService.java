package com.skillshare.skillshareapp.service;

import com.skillshare.skillshareapp.model.Student;
import com.skillshare.skillshareapp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getMentors() {
        return studentRepository.findByYearGreaterThanEqual(3);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}