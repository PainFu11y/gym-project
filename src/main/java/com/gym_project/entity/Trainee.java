package com.gym_project.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "trainees")
@Getter
@Setter
public class Trainee extends User{

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String address;

    @OneToMany(mappedBy = "trainee",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Training> trainings;

    @ManyToMany(mappedBy = "trainees")
    private Set<Trainer> trainers;

}
