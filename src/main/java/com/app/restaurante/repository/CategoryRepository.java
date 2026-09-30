package com.app.restaurante.repository;

import com.app.restaurante.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
}
