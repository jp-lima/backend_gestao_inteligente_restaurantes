package com.app.restaurante.service;

import com.app.restaurante.dto.PratoCreateRequestDTO;
import com.app.restaurante.dto.PratoResponseDTO;
import com.app.restaurante.entity.CategoryEntity;
import com.app.restaurante.entity.PratoEntity;
import com.app.restaurante.mapper.PratoMapper;
import com.app.restaurante.repository.CategoryRepository;
import com.app.restaurante.repository.PratoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PratoService {

    private final PratoRepository pratoRepository;
    private final PratoMapper pratoMapper;
    private final CategoryRepository categoryRepository;

    public PratoService(
            PratoRepository pratoRepository,
            PratoMapper pratoMapper,
            CategoryRepository categoryRepository) {
        this.pratoRepository = pratoRepository;
        this.pratoMapper = pratoMapper;
        this.categoryRepository = categoryRepository;
    }

    public List<PratoResponseDTO> findAll() {
        return pratoMapper.toResponseDTOList(pratoRepository.findAll());
    }

    public Optional<PratoResponseDTO> findById(Integer id) {
        return pratoRepository.findById(id).map(pratoMapper::toResponseDTO);
    }

    public PratoResponseDTO create(PratoCreateRequestDTO dto) {
        CategoryEntity category = findCategory(dto.getCategoryId());
        PratoEntity prato = pratoMapper.toEntity(dto, category);
        return pratoMapper.toResponseDTO(pratoRepository.save(prato));
    }

    public Optional<PratoResponseDTO> update(Integer id, PratoCreateRequestDTO dto) {
        CategoryEntity category = findCategory(dto.getCategoryId());
        return pratoRepository.findById(id).map(existing -> {
            pratoMapper.updateEntity(existing, dto, category);
            return pratoMapper.toResponseDTO(pratoRepository.save(existing));
        });
    }

    private CategoryEntity findCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Categoria não encontrada: " + categoryId));
    }

    public boolean delete(Integer id) {
        if (!pratoRepository.existsById(id)) {
            return false;
        }
        pratoRepository.deleteById(id);
        return true;
    }
}
