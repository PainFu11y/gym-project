package com.gym_project.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trainer extends User{

    @Column(nullable = false)
    private String specialization;

    @OneToMany(mappedBy = "trainer",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Training> trainings = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    private Set<Trainee> trainees = new HashSet<>();
}