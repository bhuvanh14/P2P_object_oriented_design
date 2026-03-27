package com.skillshare.skillshareapp.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.skillshare.skillshareapp.model.Student;
import com.skillshare.skillshareapp.repository.StudentRepository;

@Service
public class SearchService {

    @Autowired
    private StudentRepository repo;

    public List<Student> getAll() {
        return repo.findAll();
    }
}