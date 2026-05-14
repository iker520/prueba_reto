package com.example.mourosub.service;

import com.example.mourosub.model.Actividad;
import com.example.mourosub.repository.ActividadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActividadService {

    private final ActividadRepository actividadRepository;

    public ActividadService(ActividadRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    public List<Actividad> findAll() {
        return actividadRepository.findAll();
    }

    public List<Actividad> findAllActivas() {
        return actividadRepository.findByActivaTrue();
    }

    public List<Actividad> findByTipo(String tipo) {
        return actividadRepository.findByTipoAndActivaTrue(tipo);
    }

    public List<Actividad> findDestacadas() {
        return actividadRepository.findByDestacadaTrueAndActivaTrue();
    }

    public Optional<Actividad> findById(Long id) {
        return actividadRepository.findById(id);
    }

    public Actividad save(Actividad actividad) {
        return actividadRepository.save(actividad);
    }

    public void deleteById(Long id) {
        actividadRepository.deleteById(id);
    }

    public long count() {
        return actividadRepository.count();
    }

    /** Valores posibles para el desplegable de tipo */
    public static List<String> getTiposDisponibles() {
        return List.of("CURSO", "INMERSION", "ACTIVIDAD", "OFERTA");
    }

    /** Valores posibles para el desplegable de nivel */
    public static List<String> getNivelesDisponibles() {
        return List.of("Sin experiencia", "Open Water", "Advanced", "Rescue Diver",
                       "Divemaster", "Instructor", "Técnico");
    }
}
