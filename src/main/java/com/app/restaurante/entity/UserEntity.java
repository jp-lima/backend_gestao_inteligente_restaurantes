package com.app.restaurante.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Setter
    private String name;
    @Setter
    private String cpf;
    @Setter
    private String password;
    @Setter
    private LocalDate birth_date;
    @Setter
    private String gender;
    @Setter
    private String email;
    private String role = "user";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    public UserEntity(UUID id, String name, String cpf, String password,
       LocalDate birth_date, String gender, String role, LocalDateTime created_at) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.password = password;
        this.birth_date = birth_date;
        this.gender = gender;
        this.role = role;
        this.created_at = created_at;
    }
    public UserEntity(){}

}
