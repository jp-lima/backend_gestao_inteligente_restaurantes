package com.app.restaurante.repository;

import com.app.restaurante.entity.TabEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TabRepository extends JpaRepository<TabEntity, Integer> {

    @Query(value = "SELECT EXISTS(SELECT 1 FROM tables WHERE id = :tableId)", nativeQuery = true)
    boolean existsTableById(@Param("tableId") Integer tableId);
}
