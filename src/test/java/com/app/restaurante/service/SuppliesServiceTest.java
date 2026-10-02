package com.app.restaurante.service;

import com.app.restaurante.mapper.SuppliesMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.app.restaurante.dto.SuppliesCreateRequestDTO;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class SuppliesServiceTest {

    @InjectMocks
    private SuppliesService suppliesService;

    @InjectMocks
    SuppliesMapper suppliesMapper;

    //@InjectMocks
    //private SuppliesCreateRequestDTO suppliesDto;

    @Test
    @DisplayName("Smoke test")
    void smokeTest(){
        SuppliesCreateRequestDTO dto = new SuppliesCreateRequestDTO(
                "a",
                80,
                5,
                "L",
                false,
                LocalDate.now().plusDays(30)
        );

        System.out.println(dto.getName());

    }
}
