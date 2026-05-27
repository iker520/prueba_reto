package com.example.mourosub.service;

import com.example.mourosub.model.Ubicacion;
import com.example.mourosub.repository.UbicacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionService(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    public List<Ubicacion> findAll() {
        return ubicacionRepository.findAll();
    }

    public Optional<Ubicacion> findById(Long id) {
        return ubicacionRepository.findById(id);
    }

    public Ubicacion save(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    public void deleteById(Long id) {
        ubicacionRepository.deleteById(id);
    }

    public long count() {
        return ubicacionRepository.count();
    }

    public List<Ubicacion> findAllById(Iterable<Long> ids) {
        return ubicacionRepository.findAllById(ids);
    }
}
