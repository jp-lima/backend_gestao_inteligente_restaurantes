package com.app.restaurante.service;

import com.app.restaurante.dto.TabCreateRequestDTO;
import com.app.restaurante.dto.TabResponseDTO;
import com.app.restaurante.entity.TabEntity;
import com.app.restaurante.mapper.TabMapper;
import com.app.restaurante.repository.EmployeeRepository;
import com.app.restaurante.repository.MesaRepository;
import com.app.restaurante.repository.TabRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TabService {

    private final TabRepository tabRepository;
    private final TabMapper tabMapper;
    private final EmployeeRepository employeeRepository;
    private final MesaRepository mesaRepository;

    public TabService(TabRepository tabRepository, TabMapper tabMapper,
                      EmployeeRepository employeeRepository, MesaRepository mesaRepository) {
        this.tabRepository = tabRepository;
        this.tabMapper = tabMapper;
        this.employeeRepository = employeeRepository;
        this.mesaRepository = mesaRepository;
    }

    public TabResponseDTO create(TabCreateRequestDTO dto) {
        validateReferences(dto);
        TabEntity saved = tabRepository.save(tabMapper.toEntity(dto));
        return tabMapper.toResponseDTO(saved);
    }

    public List<TabResponseDTO> findAll() {
        return tabRepository.findAll()
                .stream()
                .map(tabMapper::toResponseDTO)
                .toList();
    }

    public TabResponseDTO findById(Integer id) {
        return tabRepository.findById(id)
                .map(tabMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Comanda não encontrada pelo ID: " + id));
    }

    public TabResponseDTO update(Integer id, TabCreateRequestDTO dto) {
        TabEntity entity = tabRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comanda não encontrada pelo ID: " + id));

        validateReferences(dto);
        tabMapper.updateEntityFromDTO(dto, entity);
        return tabMapper.toResponseDTO(tabRepository.save(entity));
    }

    public void delete(Integer id) {
        if (!tabRepository.existsById(id)) {
            throw new EntityNotFoundException("Comanda não encontrada pelo ID: " + id);
        }
        tabRepository.deleteById(id);
    }

    private void validateReferences(TabCreateRequestDTO dto) {
        if (!mesaRepository.existsById(dto.getTableId())) {
            throw new EntityNotFoundException("Mesa não encontrada pelo ID: " + dto.getTableId());
        }
        if (dto.getWaiterId() != null
                && !employeeRepository.existsById(dto.getWaiterId().longValue())) {
            throw new EntityNotFoundException("Funcionário não encontrado pelo ID: " + dto.getWaiterId());
        }
    }
}
