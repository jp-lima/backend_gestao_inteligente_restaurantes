package com.app.restaurante.service;

import com.app.restaurante.dto.UserCreateRequestDTO;
import com.app.restaurante.dto.UserCreateResponseDTO;
import com.app.restaurante.entity.UserEntity;
import com.app.restaurante.mapper.UserMapper;
import com.app.restaurante.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // injeção via construtor (melhor que @Autowired em campo)
    public AuthService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserCreateResponseDTO createUser(UserCreateRequestDTO dto) {
        // 1. DTO → Entity
        UserEntity user = userMapper.toEntity(dto);

        // 2. salva no banco (o Hibernate gera o id e o created_at aqui)
        UserEntity saved = userRepository.save(user);

        // 3. Entity → ResponseDTO
        return userMapper.toResponseDTO(saved);
    }
}