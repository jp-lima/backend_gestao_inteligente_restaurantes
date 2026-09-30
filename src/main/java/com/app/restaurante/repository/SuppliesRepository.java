package com.app.restaurante.repository;

import com.app.restaurante.entity.SuppliesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuppliesRepository extends JpaRepository<SuppliesEntity, Long> {
}