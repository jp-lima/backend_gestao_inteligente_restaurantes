package com.app.restaurante.repository;

import com.app.restaurante.entity.PratoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PratoRepository extends JpaRepository<PratoEntity, Integer> {
}
