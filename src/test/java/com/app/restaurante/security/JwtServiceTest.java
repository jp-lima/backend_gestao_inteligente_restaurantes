package com.app.restaurante.security;

import com.app.restaurante.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    JwtService jwtService;

    @Test
    void tokenValido() {
        UserEntity u = new UserEntity();
        u.setName("Teste");
        u.setCpf("12345678900");
        u.setPassword("encoded");
        u.setBirth_date(LocalDate.of(2000, 1, 1));
        u.setEmail("teste@mail.com");
        try {
            var f = UserEntity.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(u, java.util.UUID.randomUUID());
        } catch (Exception e) {
            fail(e);
        }

        String token = jwtService.generateToken(u);
        assertNotNull(jwtService.validate(token));
        assertNotNull(jwtService.getUserId(token));
    }

    @Test
    void tokenInvalidoFalha() {
        assertThrows(Exception.class, () -> jwtService.validate("invalido"));
    }
}
