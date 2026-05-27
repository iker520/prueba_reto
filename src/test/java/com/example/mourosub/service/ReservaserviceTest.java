package com.example.mourosub.service;

import com.example.mourosub.model.*;
import com.example.mourosub.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/*reserva*/
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de ReservaService")
class ReservaServiceTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private UsuarioReservaRepository usuarioReservaRepository;
    @Mock private ActividadReservaRepository actividadReservaRepository;
    @Mock private ActividadReservaUbicacionRepository aruRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private InstructorReservaRepository instructorReservaRepository;
    @Mock private InstructorRepository instructorRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reserva;
    private Usuario usuario;
    private Actividad actividad;

    @BeforeEach
    void setUp() {
        reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setEstado("PENDIENTE");
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setNotas("Sin notas");

        usuario = new Usuario();
        usuario.setDniUsuario("12345678A");
        usuario.setEmail("user@mourosub.com");
        usuario.setEsBuceador(false);

        actividad = new Actividad();
        actividad.setIdActividad(1L);
        actividad.setNombre("Open Water Diver");
        actividad.setActiva(true);
    }

    // ─── findAll ──────────────────────────────────────────────
    @Test
    @DisplayName("findAll retorna todas las reservas ordenadas por fecha")
    void findAll_retornaTodasOrdenadas() {
        when(reservaRepository.findAllByOrderByFechaReservaDesc()).thenReturn(List.of(reserva));

        List<Reserva> result = reservaService.findAll();

        assertThat(result).hasSize(1);
        verify(reservaRepository).findAllByOrderByFechaReservaDesc();
    }

    // ─── findByEstado ─────────────────────────────────────────
    @Test
    @DisplayName("findByEstado filtra correctamente por PENDIENTE")
    void findByEstado_filtraPendientes() {
        when(reservaRepository.findByEstado("PENDIENTE")).thenReturn(List.of(reserva));

        List<Reserva> result = reservaService.findByEstado("PENDIENTE");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    @DisplayName("findByEstado retorna vacío si no hay reservas con ese estado")
    void findByEstado_retornaVacioSiNoHay() {
        when(reservaRepository.findByEstado("CANCELADA")).thenReturn(List.of());

        List<Reserva> result = reservaService.findByEstado("CANCELADA");

        assertThat(result).isEmpty();
    }

    // ─── findById ─────────────────────────────────────────────
    @Test
    @DisplayName("findById retorna reserva si existe")
    void findById_retornaReservaSiExiste() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        Optional<Reserva> result = reservaService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getIdReserva()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById retorna vacío si no existe")
    void findById_retornaVacioSiNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Reserva> result = reservaService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ─── save ─────────────────────────────────────────────────
    @Test
    @DisplayName("save establece fechaReserva si es null")
    void save_estableceFechaReservaSiEsNull() {
        reserva.setFechaReserva(null);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        reservaService.save(reserva);

        assertThat(reserva.getFechaReserva()).isNotNull();
        verify(reservaRepository).save(reserva);
    }

    @Test
    @DisplayName("save no sobreescribe fechaReserva si ya tiene valor")
    void save_noSobreescribeFechaExistente() {
        LocalDateTime fechaOriginal = LocalDateTime.of(2025, 6, 1, 10, 0);
        reserva.setFechaReserva(fechaOriginal);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        reservaService.save(reserva);

        assertThat(reserva.getFechaReserva()).isEqualTo(fechaOriginal);
    }

    // ─── crearReserva ─────────────────────────────────────────
    @Test
    @DisplayName("crearReserva crea reserva con estado PENDIENTE")
    void crearReserva_estadoInicialEsPendiente() {
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);
        when(usuarioReservaRepository.save(any(UsuarioReserva.class)))
                .thenReturn(new UsuarioReserva());
        when(actividadReservaRepository.save(any(ActividadReserva.class)))
                .thenReturn(new ActividadReserva());

        Reserva result = reservaService.crearReserva(usuario, List.of(actividad), "Sin notas");

        assertThat(result.getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    @DisplayName("crearReserva asigna fecha automáticamente")
    void crearReserva_asignaFechaAutomaticamente() {
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);
        when(usuarioReservaRepository.save(any(UsuarioReserva.class)))
                .thenReturn(new UsuarioReserva());
        when(actividadReservaRepository.save(any(ActividadReserva.class)))
                .thenReturn(new ActividadReserva());

        Reserva result = reservaService.crearReserva(usuario, List.of(actividad), null);

        assertThat(result.getFechaReserva()).isNotNull();
    }

    @Test
    @DisplayName("crearReserva llama a repositorios de relaciones")
    void crearReserva_llamaRepositoriosRelaciones() {
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);
        when(usuarioReservaRepository.save(any(UsuarioReserva.class)))
                .thenReturn(new UsuarioReserva());
        when(actividadReservaRepository.save(any(ActividadReserva.class)))
                .thenReturn(new ActividadReserva());

        reservaService.crearReserva(usuario, List.of(actividad), "Notas de prueba");

        verify(usuarioReservaRepository, atLeastOnce()).save(any(UsuarioReserva.class));
        verify(actividadReservaRepository, atLeastOnce()).save(any(ActividadReserva.class));
    }

    @Test
    @DisplayName("crearReserva para un usuario buceador guarda esBuceador=true")
    void crearReserva_usuarioBuceador_guardaEsBuceadorTrue() {
        usuario.setEsBuceador(true);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        UsuarioReserva ur = new UsuarioReserva();
        when(usuarioReservaRepository.save(any(UsuarioReserva.class))).thenAnswer(inv -> {
            UsuarioReserva saved = inv.getArgument(0);
            ur.setEsBuceador(saved.getEsBuceador());
            return saved;
        });
        when(actividadReservaRepository.save(any(ActividadReserva.class)))
                .thenReturn(new ActividadReserva());

        reservaService.crearReserva(usuario, List.of(actividad), null);

        assertThat(ur.getEsBuceador()).isTrue();
    }
}