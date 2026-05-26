package com.example.mourosub.repository;

import com.example.mourosub.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByEstado(String estado);
    List<Reserva> findByFechaReservaOrderByFechaReservaDesc(LocalDate fecha);
    List<Reserva> findAllByOrderByFechaReservaDesc();

    /** Reservas en las que participa un usuario concreto (via join table) */
    @Query("SELECT r FROM Reserva r JOIN r.usuarios ur WHERE ur.dniUsuario = :dni ORDER BY r.fechaReserva DESC")
    List<Reserva> findByDniUsuario(@org.springframework.data.repository.query.Param("dni") String dni);
}
