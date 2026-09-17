package com.app.restaurante.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.restaurante.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
