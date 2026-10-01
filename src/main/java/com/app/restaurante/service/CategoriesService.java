package com.app.restaurante.service;

import com.app.restaurante.dto.CategoriesCreateRequestDTO;
import com.app.restaurante.dto.CategoriesResponseDTO;
import com.app.restaurante.entity.CategoriesEntity;
import com.app.restaurante.mapper.CategoriesMapper;
import com.app.restaurante.repository.CategoriesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final CategoriesMapper categoriesMapper;

    public CategoriesService(CategoriesRepository categoriesRepository, CategoriesMapper categoriesMapper) {
        this.categoriesRepository = categoriesRepository;
        this.categoriesMapper = categoriesMapper;
    }

    public CategoriesResponseDTO create(CategoriesCreateRequestDTO dto) {
        CategoriesEntity entity = categoriesMapper.toEntity(dto);
        CategoriesEntity saved = categoriesRepository.save(entity);
        return categoriesMapper.toResponseDTO(saved);
    }

    public List<CategoriesResponseDTO> findAll() {
        return categoriesRepository.findAll()
                .stream()
                .map(categoriesMapper::toResponseDTO)
                .toList();
    }

    public CategoriesResponseDTO findById(Long id) {
        CategoriesEntity entity = categoriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada para o ID: " + id));

        return categoriesMapper.toResponseDTO(entity);
    }

    public CategoriesResponseDTO update(Long id, CategoriesCreateRequestDTO dto) {
        CategoriesEntity entity = categoriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada para o ID: " + id));

        categoriesMapper.updateEntityFromDTO(dto, entity);

        CategoriesEntity updated = categoriesRepository.save(entity);
        return categoriesMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        if (!categoriesRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoria não encontrada para o ID: " + id);
        }
        categoriesRepository.deleteById(id);
    }
}