package com.example.mourosub.repository;

import com.example.mourosub.model.InstructorReserva;
import com.example.mourosub.model.InstructorReservaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InstructorReservaRepository extends JpaRepository<InstructorReserva, InstructorReservaId> {

    List<InstructorReserva> findByIdReservaAndIdActividad(Long idReserva, Long idActividad);

    /**
     * Devuelve los DNIs de instructores que tienen alguna asignación cuya fechaFin
     * es posterior a (fechaInicioNuevaSesion - 1h), es decir, que NO están libres aún.
     * Un instructor está OCUPADO si su última sesión termina dentro del margen de 1h.
     */
    @Query("""
        SELECT ir.dniInstructor FROM InstructorReserva ir
        WHERE ir.fechaFin IS NOT NULL
          AND ir.fechaFin > :margen
    """)
    List<String> findDniInstructoresOcupados(@Param("margen") LocalDateTime margen);
}
