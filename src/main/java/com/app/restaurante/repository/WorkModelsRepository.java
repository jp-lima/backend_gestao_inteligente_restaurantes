package com.app.restaurante.repository;

import com.app.restaurante.entity.WorkModelsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkModelsRepository extends JpaRepository<WorkModelsEntity, Long> {
}