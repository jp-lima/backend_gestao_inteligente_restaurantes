package com.app.restaurante.test.service;

import com.app.restaurante.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @InjectMocks
    AuthService authService;

    @Test
    void cadastrarUsuario(){

    }

    @Test
    void simplesTeste(){
        System.out.println("TESTe");
    }


}
