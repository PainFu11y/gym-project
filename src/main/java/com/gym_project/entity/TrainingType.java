package com.gym_project.entity;


import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "training_types")
@Getter
@Setter
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_type_name", nullable = false)
    private String trainingTypeName;

    @OneToMany(mappedBy = "trainingType")
    private Set<Training> trainings;
}