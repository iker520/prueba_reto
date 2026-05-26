package com.example.mourosub.repository;

import com.example.mourosub.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, String> {
    List<Instructor> findByActivoTrue();
    Optional<Instructor> findByEmail(String email);
}
