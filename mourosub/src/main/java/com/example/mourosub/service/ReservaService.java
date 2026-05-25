package com.example.mourosub.service;

import com.example.mourosub.model.*;
import com.example.mourosub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioReservaRepository usuarioReservaRepository;
    private final ActividadReservaRepository actividadReservaRepository;
    private final com.example.mourosub.repository.ActividadReservaUbicacionRepository aruRepository;

    public ReservaService(ReservaRepository reservaRepository,
            UsuarioReservaRepository usuarioReservaRepository,
            ActividadReservaRepository actividadReservaRepository,
            com.example.mourosub.repository.ActividadReservaUbicacionRepository aruRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioReservaRepository = usuarioReservaRepository;
        this.actividadReservaRepository = actividadReservaRepository;
        this.aruRepository = aruRepository;
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
            reserva.setFechaReserva(LocalDateTime.now());
        }
        return reservaRepository.save(reserva);
    }

    /**
     * Crea una reserva completa a partir de un usuario, una lista de actividades y
     * notas opcionales.
     * El estado inicial es siempre PENDIENTE.
     */
    public Reserva crearReserva(Usuario usuario, List<Actividad> actividades, String notas) {
        // 1. Crear cabecera de reserva
        Reserva reserva = new Reserva();
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setEstado("PENDIENTE");
        reserva.setNotas(notas);
        // El total se calculará cuando el admin confirme fechas y plazas
        reserva = reservaRepository.save(reserva);

        // 2. Vincular usuario a la reserva
        UsuarioReserva usuarioReserva = new UsuarioReserva();
        usuarioReserva.setIdReserva(reserva.getIdReserva());
        usuarioReserva.setDniUsuario(usuario.getDniUsuario());
        usuarioReserva.setCantidad(1);
        // Copia del estado buceador en el momento de la reserva
        usuarioReserva.setEsBuceador(Boolean.TRUE.equals(usuario.getEsBuceador()));
        usuarioReservaRepository.save(usuarioReserva);

        // 3. Vincular cada actividad a la reserva (sin fecha aún — el admin la
        // confirmará)
        for (Actividad actividad : actividades) {
            ActividadReserva ar = new ActividadReserva();
            ar.setIdReserva(reserva.getIdReserva());
            ar.setIdActividad(actividad.getIdActividad());
            ar.setPrecio(actividad.getPrecio());
            // fechaInicio y fechaFin se asignarán cuando el admin confirme
            actividadReservaRepository.save(ar);
        }

        return reserva;
    }

    /** Reservas de un usuario concreto (para Mi Cuenta) */
    public List<Reserva> findByDniUsuario(String dni) {
        return reservaRepository.findByDniUsuario(dni);
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

    /** Estados posibles (para los botones del panel admin) */
    public static List<String> getEstadosDisponibles() {
        return List.of("PENDIENTE", "CONFIRMADA", "CANCELADA", "REALIZADA");
    }

    // ----------------------------------------------------------------
    // Programación de Ubicaciones y Fechas
    // ----------------------------------------------------------------
    @Transactional
    public void programarActividad(Long idReserva, Long idActividad, Long idUbicacion, java.time.LocalDateTime fechaInicio) {
        ActividadReserva ar = actividadReservaRepository.findById(new com.example.mourosub.model.ActividadReservaId(idReserva, idActividad))
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Actividad de reserva no encontrada"));

        com.example.mourosub.model.Ubicacion ubicacion = new com.example.mourosub.model.Ubicacion();
        ubicacion.setIdUbicacion(idUbicacion);

        com.example.mourosub.model.ActividadReservaUbicacion aru = new com.example.mourosub.model.ActividadReservaUbicacion();
        aru.setIdReserva(idReserva);
        aru.setIdActividad(idActividad);
        aru.setActividadReserva(ar);
        aru.setUbicacion(ubicacion);
        aru.setFechaInicio(fechaInicio);

        // Calcular fecha fin usando la duración (minutos) de la actividad
        Integer duracion = ar.getActividad().getDuracion();
        if (duracion != null) {
            aru.setFechaFin(fechaInicio.plusMinutes(duracion));
        }

        ar.getProgramaciones().add(aru);
        actividadReservaRepository.save(ar);
    }

    @Transactional
    public void eliminarProgramacion(Long idProgramacion) {
        aruRepository.deleteById(idProgramacion);
    }
}
