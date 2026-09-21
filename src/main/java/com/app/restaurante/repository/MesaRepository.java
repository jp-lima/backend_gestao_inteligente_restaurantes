package com.app.restaurante.repository;

import com.app.restaurante.entity.MesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MesaRepository extends JpaRepository<MesaEntity, UUID> {
    boolean existsByNumero(Integer numero);
}
