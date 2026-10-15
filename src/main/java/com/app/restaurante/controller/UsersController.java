package com.app.restaurante.controller;

import com.app.restaurante.dto.RoleUpdateRequestDTO;
import com.app.restaurante.dto.UserCreateResponseDTO;
import com.app.restaurante.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Administração de usuários (admin)")
public class UsersController {

    private final AuthService authService;

    public UsersController(AuthService authService) {
        this.authService = authService;
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Promover/rebaixar usuário (só admin)")
    public ResponseEntity<UserCreateResponseDTO> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody RoleUpdateRequestDTO dto) {
        return ResponseEntity.ok(authService.updateRole(id, dto));
    }
}
