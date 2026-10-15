package com.app.restaurante.security;

import com.app.restaurante.entity.UserEntity;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    JwtService jwtService;

    private UserEntity user() {
        UserEntity u = new UserEntity();
        u.setName("Teste");
        u.setCpf("12345678900");
        u.setPassword("encoded");
        u.setBirth_date(LocalDate.of(2000, 1, 1));
        u.setGender("M");
        u.setEmail("teste@mail.com");
        return u;
    }

    @Test
    void accessTokenValido() {
        // sem id (transient) não dá: simula id via reflexão simples
        UserEntity u = user();
        // força um UUID sem banco
        try {
            var f = UserEntity.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(u, java.util.UUID.randomUUID());
        } catch (Exception e) {
            fail(e);
        }

        String access = jwtService.generateAccessToken(u);
        String refresh = jwtService.generateRefreshToken(u);

        Claims ac = jwtService.validate(access);
        Claims rc = jwtService.validate(refresh);

        assertTrue(jwtService.isAccessToken(ac));
        assertTrue(jwtService.isRefreshToken(rc));
    }

    @Test
    void tokenInvalidoFalha() {
        assertThrows(Exception.class, () -> jwtService.validate("invalido"));
    }
}
