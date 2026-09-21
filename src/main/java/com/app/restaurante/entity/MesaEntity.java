package com.app.restaurante.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;
@Setter
@Getter
@Entity
@Table(name = "mesas")
public class MesaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(nullable = false, unique = true)
    private Integer numero;

    @Setter
    @Column(nullable = false)
    private Integer capacidade;

    @Setter
    @Column(nullable = false)
    private String status = "LIVRE";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    public MesaEntity() {
    }

    public MesaEntity(UUID id, Integer numero, Integer capacidade, String status,
                      LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.numero = numero;
        this.capacidade = capacidade;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
