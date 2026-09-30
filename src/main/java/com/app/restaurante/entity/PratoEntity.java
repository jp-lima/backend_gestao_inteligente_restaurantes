package com.app.restaurante.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "menu")
public class PratoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Setter
    private String description;

    @Setter
    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal price;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String category;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Setter
    private CategoryEntity categoryEntity;

    @Column(nullable = false)
    @Setter
    private boolean available;

    @Setter
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
