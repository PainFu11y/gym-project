package com.gym_project.dto.create.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class TrainerCreateResponseDto {

    private String username;
    private String password;
}