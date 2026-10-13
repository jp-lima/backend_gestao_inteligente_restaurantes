package com.app.restaurante.repository;

import com.app.restaurante.entity.AbsencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsencesRepository extends JpaRepository<AbsencesEntity, Long> {
}
