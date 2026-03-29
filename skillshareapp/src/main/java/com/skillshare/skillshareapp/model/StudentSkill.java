package com.skillshare.skillshareapp.model;

import jakarta.persistence.*;

@Entity
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Skill skill;
}