package com.example.mourosub.service;

import com.example.mourosub.model.Instructor;
import com.example.mourosub.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public List<Instructor> findAll() {
        return instructorRepository.findAll();
    }

    public List<Instructor> findAllActivos() {
        return instructorRepository.findByActivoTrue();
    }

    public Optional<Instructor> findById(String dni) {
        return instructorRepository.findById(dni);
    }

    public Instructor save(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public void deleteById(String dni) {
        instructorRepository.deleteById(dni);
    }

    public long count() {
        return instructorRepository.count();
    }

    /** Títulos PADI para el desplegable */
    public static List<String> getTitulosDisponibles() {
        return List.of("Open Water Instructor", "Advanced Open Water Instructor",
                       "IDC Staff Instructor", "Master Scuba Diver Trainer",
                       "PADI Course Director", "Divemaster");
    }
}
