package com.app.restaurante.mapper;

import com.app.restaurante.dto.UserCreateRequestDTO;
import com.app.restaurante.dto.UserCreateResponseDTO;
import com.app.restaurante.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserCreateRequestDTO dto) {
        UserEntity user = new UserEntity();
        user.setName(dto.getName());
        user.setCpf(dto.getCpf());
        user.setPassword(dto.getPassword());   // ⚠️ depois precisa criptografar!
        user.setBirth_date(dto.getBirth_date());
        user.setGender(dto.getGender());
        user.setEmail(dto.getEmail());
        return user;
    }

    public UserCreateResponseDTO toResponseDTO(UserEntity entity) {
        UserCreateResponseDTO dto = new UserCreateResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCpf(entity.getCpf());
        dto.setBirth_date(entity.getBirth_date());
        dto.setGender(entity.getGender());
        dto.setRole(entity.getRole());
        dto.setCreated_at(entity.getCreated_at());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}