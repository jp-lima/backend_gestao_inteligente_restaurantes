package com.app.restaurante.service;

import com.app.restaurante.dto.LoginRequestDTO;
import com.app.restaurante.dto.LoginResponseDTO;
import com.app.restaurante.dto.RefreshRequestDTO;
import com.app.restaurante.dto.UserCreateRequestDTO;
import com.app.restaurante.dto.UserCreateResponseDTO;
import com.app.restaurante.entity.UserEntity;
import com.app.restaurante.mapper.UserMapper;
import com.app.restaurante.repository.UserRepository;
import com.app.restaurante.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // injeção via construtor (melhor que @Autowired em campo)
    public AuthService(UserRepository userRepository, UserMapper userMapper,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserCreateResponseDTO createUser(UserCreateRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }
        // 1. DTO → Entity (role sempre "user": cadastro público nunca vira admin)
        UserEntity user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 2. salva no banco (o Hibernate gera o id e o created_at aqui)
        UserEntity saved = userRepository.save(user);

        // 3. Entity → ResponseDTO
        return userMapper.toResponseDTO(saved);
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        return new LoginResponseDTO(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user),
                jwtService.getAccessExpirationSeconds());
    }

    /**
     * V1 stateless: valida assinatura/expiração e o claim type=refresh,
     * depois emite um access novo. Sem tabela de revogação (fica para V2).
     */
    public LoginResponseDTO refresh(RefreshRequestDTO dto) {
        final Claims claims;
        try {
            claims = jwtService.validate(dto.getRefreshToken());
        } catch (JwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado");
        }
        if (!jwtService.isRefreshToken(claims)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token informado não é refresh token");
        }

        UserEntity user = userRepository.findById(jwtService.getUserId(dto.getRefreshToken()))
                .orElseThrow(() -> new EntityNotFoundException("Usuário do refresh token não encontrado"));

        return new LoginResponseDTO(
                jwtService.generateAccessToken(user),
                dto.getRefreshToken(),
                jwtService.getAccessExpirationSeconds());
    }
}