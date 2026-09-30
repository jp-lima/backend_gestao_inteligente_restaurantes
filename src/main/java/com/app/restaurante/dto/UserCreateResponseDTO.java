package com.app.restaurante.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
public class UserCreateResponseDTO {

    // Getters e Setters
    private UUID id;
    private String name;
    private String cpf;
    private LocalDate birth_date;
    private String gender;
    private String role;
    private String email;
    private LocalDateTime created_at;

    public UserCreateResponseDTO() {
    }

}