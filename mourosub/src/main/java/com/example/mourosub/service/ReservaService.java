package com.example.mourosub.service;

import com.example.mourosub.model.Reserva;
import com.example.mourosub.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> findAll() {
        return reservaRepository.findAllByOrderByFechaReservaDesc();
    }

    public List<Reserva> findByEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }

    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva save(Reserva reserva) {
        if (reserva.getFechaReserva() == null) {
            reserva.setFechaReserva(LocalDate.now());
        }
        return reservaRepository.save(reserva);
    }

    public void cambiarEstado(Long id, String nuevoEstado) {
        reservaRepository.findById(id).ifPresent(r -> {
            r.setEstado(nuevoEstado);
            reservaRepository.save(r);
        });
    }

    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }

    public long count() {
        return reservaRepository.count();
    }

    public long countByEstado(String estado) {
        return reservaRepository.findByEstado(estado).size();
    }

    /** Estados posibles para el desplegable */
    public static List<String> getEstadosDisponibles() {
        return List.of("PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA");
    }
}
