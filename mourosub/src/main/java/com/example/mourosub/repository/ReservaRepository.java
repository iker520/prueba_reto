package com.example.mourosub.repository;

import com.example.mourosub.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByEstado(String estado);
    List<Reserva> findByFechaReservaOrderByFechaReservaDesc(LocalDate fecha);
    List<Reserva> findAllByOrderByFechaReservaDesc();
}
