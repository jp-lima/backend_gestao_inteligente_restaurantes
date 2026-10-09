package com.app.restaurante.service;

import com.app.restaurante.dto.EmployeeCreateRequestDTO;
import com.app.restaurante.dto.EmployeeResponseDTO;
import com.app.restaurante.entity.EmployeeEntity;
import com.app.restaurante.mapper.EmployeeMapper;
import com.app.restaurante.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    public EmployeeResponseDTO create(EmployeeCreateRequestDTO dto) {
        EmployeeEntity saved = employeeRepository.save(employeeMapper.toEntity(dto));
        return employeeMapper.toResponseDTO(saved);
    }

    public List<EmployeeResponseDTO> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponseDTO)
                .toList();
    }

    public EmployeeResponseDTO findById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado pelo ID: " + id));
    }

    public EmployeeResponseDTO update(Long id, EmployeeCreateRequestDTO dto) {
        EmployeeEntity entity = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado pelo ID: " + id));

        employeeMapper.updateEntityFromDTO(dto, entity);
        return employeeMapper.toResponseDTO(employeeRepository.save(entity));
    }

    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Funcionário não encontrado pelo ID: " + id);
        }
        employeeRepository.deleteById(id);
    }
}
