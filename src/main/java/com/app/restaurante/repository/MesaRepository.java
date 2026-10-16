package com.app.restaurante.repository;

import com.app.restaurante.entity.MesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MesaRepository extends JpaRepository<MesaEntity, Integer> {
    boolean existsByNumber(Integer number);
}
