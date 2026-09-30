package com.app.restaurante.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "supplies")

public class SuppliesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column (nullable = false)
    private String name;


    @Column (nullable = false)
    private int quantity;

    @Column (nullable = false)
    private int minimum_stock;


    @Column (nullable = false)
    private String unit;


    @Column (nullable = false)
    private boolean perishable;

    @Column
    private LocalDate expiration_date;

    @UpdateTimestamp
    @Column
    private OffsetDateTime updated_at;

}
