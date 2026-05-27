package com.example.mourosub.service;

import com.example.mourosub.model.*;
import com.example.mourosub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioReservaRepository usuarioReservaRepository;
    private final ActividadReservaRepository actividadReservaRepository;
    private final com.example.mourosub.repository.ActividadReservaUbicacionRepository aruRepository;
    private final com.example.mourosub.repository.UsuarioRepository usuarioRepository;
    private final InstructorReservaRepository instructorReservaRepository;
    private final InstructorRepository instructorRepository;

    public ReservaService(ReservaRepository reservaRepository,
            UsuarioReservaRepository usuarioReservaRepository,
            ActividadReservaRepository actividadReservaRepository,
            com.example.mourosub.repository.ActividadReservaUbicacionRepository aruRepository,
            com.example.mourosub.repository.UsuarioRepository usuarioRepository,
            InstructorReservaRepository instructorReservaRepository,
            InstructorRepository instructorRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioReservaRepository = usuarioReservaRepository;
        this.actividadReservaRepository = actividadReservaRepository;
        this.aruRepository = aruRepository;
        this.usuarioRepository = usuarioRepository;
        this.instructorReservaRepository = instructorReservaRepository;
        this.instructorRepository = instructorRepository;
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

    /**
     * Actualiza las actividades de una reserva existente en estado PENDIENTE.
     * Elimina las actividades actuales e inserta las nuevas.
     */
    @Transactional
    public void actualizarActividadesReserva(Long idReserva, List<Actividad> nuevasActividades, String notas) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Reserva no encontrada"));
        
        if (!"PENDIENTE".equals(reserva.getEstado())) {
            throw new IllegalStateException("Solo se pueden editar reservas en estado PENDIENTE.");
        }

        // Actualizar notas
        reserva.setNotas(notas);
        reservaRepository.save(reserva);

        // Borrar actividades actuales (hibernate borrará en cascada programaciones e instructores si existieran)
        // Como es PENDIENTE no deberían existir, pero lo hacemos por seguridad
        actividadReservaRepository.deleteAll(reserva.getActividades());
        reserva.getActividades().clear();

        // Insertar nuevas actividades
        for (Actividad actividad : nuevasActividades) {
            ActividadReserva ar = new ActividadReserva();
            ar.setIdReserva(reserva.getIdReserva());
            ar.setIdActividad(actividad.getIdActividad());
            ar.setPrecio(actividad.getPrecio());
            actividadReservaRepository.save(ar);
        }
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

    /**
     * Cambia el usuario de una reserva. Elimina la fila (idReserva, dniActual)
     * en USUARIOS_RESERVAS e inserta una nueva con el nuevo usuario,
     * copiando el campo esBuceador del perfil actual del nuevo usuario.
     */
    @Transactional
    public void cambiarUsuarioReserva(Long idReserva, String dniActual, String dniNuevo) {
        // Eliminar la fila del usuario actual
        usuarioReservaRepository.deleteById(new com.example.mourosub.model.UsuarioReservaId(idReserva, dniActual));

        // Obtener el nuevo usuario para copiar esBuceador
        com.example.mourosub.model.Usuario nuevoUsuario = usuarioRepository.findById(dniNuevo)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Usuario no encontrado: " + dniNuevo));

        // Insertar nueva fila
        UsuarioReserva nueva = new UsuarioReserva();
        nueva.setIdReserva(idReserva);
        nueva.setDniUsuario(dniNuevo);
        nueva.setCantidad(1);
        nueva.setEsBuceador(Boolean.TRUE.equals(nuevoUsuario.getEsBuceador()));
        usuarioReservaRepository.save(nueva);
    }

    // ----------------------------------------------------------------
    // Gestión de Instructores en Reservas
    // ----------------------------------------------------------------

    /**
     * Asigna un instructor a una actividad de una reserva.
     * La fechaFin se calcula automáticamente: fechaInicio + duración
     * de la actividad específica vinculada a la reserva.
     * Si ya existe la asignación (misma PK), la actualiza.
     */
    @Transactional
    public void asignarInstructor(Long idReserva, Long idActividad, String dniInstructor,
                                  LocalDateTime fechaInicio) {
        ActividadReserva ar = actividadReservaRepository.findById(new com.example.mourosub.model.ActividadReservaId(idReserva, idActividad))
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Actividad de reserva no encontrada"));

        int totalMinutos = 0;
        if (ar.getActividad() != null && ar.getActividad().getDuracion() != null) {
            totalMinutos = ar.getActividad().getDuracion();
        }
        LocalDateTime fechaFin = totalMinutos > 0
                ? fechaInicio.plusMinutes(totalMinutos)
                : null;

        InstructorReservaId pk = new InstructorReservaId(idReserva, idActividad, dniInstructor);
        InstructorReserva ir = instructorReservaRepository.findById(pk)
                .orElseGet(() -> {
                    InstructorReserva n = new InstructorReserva();
                    n.setIdReserva(idReserva);
                    n.setIdActividad(idActividad);
                    n.setDniInstructor(dniInstructor);
                    return n;
                });
        ir.setFechaInicio(fechaInicio);
        ir.setFechaFin(fechaFin);
        instructorReservaRepository.save(ir);
    }

    /**
     * Desasigna un instructor de una actividad de la reserva.
     */
    @Transactional
    public void desasignarInstructor(Long idReserva, Long idActividad, String dniInstructor) {
        instructorReservaRepository.deleteById(new InstructorReservaId(idReserva, idActividad, dniInstructor));
    }

    /**
     * Devuelve instructores activos disponibles para una reserva dada una
     * fechaInicio propuesta.
     * Un instructor está disponible si NO tiene ninguna sesión en INSTRUCTORES_RESERVAS
     * cuya fechaFin sea posterior a (fechaInicio - 1h), excluyendo la propia reserva.
     */
    public List<Instructor> getInstructoresDisponibles(LocalDateTime fechaInicioPropuesta) {
        // El margen es: fechaInicio - 1h. Si alguna sesión del instructor termina
        // después de ese instante, el instructor está ocupado.
        LocalDateTime margen = fechaInicioPropuesta.minusHours(1);
        Set<String> ocupados = new java.util.HashSet<>(
                instructorReservaRepository.findDniInstructoresOcupados(margen));
        return instructorRepository.findByActivoTrue().stream()
                .filter(i -> !ocupados.contains(i.getDniInstructor()))
                .toList();
    }
}
