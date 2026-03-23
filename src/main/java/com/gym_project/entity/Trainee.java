package com.gym_project.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
