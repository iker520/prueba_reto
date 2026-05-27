package com.example.mourosub.service;

import com.example.mourosub.model.Certificacion;
import com.example.mourosub.model.Usuario;
import com.example.mourosub.repository.CertificacionRepository;
import com.example.mourosub.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificacionServiceTest {

    @Mock
    private CertificacionRepository certificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CertificacionService certificacionService;

    private Certificacion certificacion;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setDniUsuario("12345678A");
        usuario.setNombre("Ana");
        usuario.setEmail("ana@test.com");

        certificacion = new Certificacion();
        certificacion.setNumCertificacion(1L);
        certificacion.setUsuario(usuario);
        certificacion.setTitulo("Open Water Diver");
        certificacion.setEntidadCertificadora("PADI");
        certificacion.setRevisada(false);
        certificacion.setValidada(false);
        certificacion.setExpedidaPorMourosub(false);
    }

    // --- findAll ---

    @Test
    void findAll_debeRetornarTodasLasCertificaciones() {
        when(certificacionRepository.findAll()).thenReturn(List.of(certificacion));

        List<Certificacion> resultado = certificacionService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Open Water Diver");
        verify(certificacionRepository).findAll();
    }

    // --- findById ---

    @Test
    void findById_cuandoExiste_debeRetornarCertificacion() {
        when(certificacionRepository.findById(1L)).thenReturn(Optional.of(certificacion));

        Optional<Certificacion> resultado = certificacionService.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTitulo()).isEqualTo("Open Water Diver");
    }

    @Test
    void findById_cuandoNoExiste_debeRetornarVacio() {
        when(certificacionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Certificacion> resultado = certificacionService.findById(99L);

        assertThat(resultado).isEmpty();
    }

    // --- findByUsuario ---

    @Test
    void findByUsuario_debeRetornarCertificacionesDelUsuario() {
        when(certificacionRepository.findByUsuarioDniUsuario("12345678A"))
                .thenReturn(List.of(certificacion));

        List<Certificacion> resultado = certificacionService.findByUsuario("12345678A");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuario().getDniUsuario()).isEqualTo("12345678A");
    }

    // --- findPropiasByUsuario ---

    @Test
    void findPropiasByUsuario_debeRetornarSoloLasNoPropiasDeMouro() {
        when(certificacionRepository.findByUsuarioDniUsuarioAndExpedidaPorMourosubFalse("12345678A"))
                .thenReturn(List.of(certificacion));

        List<Certificacion> resultado = certificacionService.findPropiasByUsuario("12345678A");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getExpedidaPorMourosub()).isFalse();
    }

    // --- findMouroSubByUsuario ---

    @Test
    void findMouroSubByUsuario_debeRetornarSoloLasExpedidasPorMouro() {
        certificacion.setExpedidaPorMourosub(true);
        when(certificacionRepository.findByUsuarioDniUsuarioAndExpedidaPorMourosubTrue("12345678A"))
                .thenReturn(List.of(certificacion));

        List<Certificacion> resultado = certificacionService.findMouroSubByUsuario("12345678A");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getExpedidaPorMourosub()).isTrue();
    }

    // --- findPendientesRevision ---

    @Test
    void findPendientesRevision_debeRetornarCertificacionesSinRevisar() {
        when(certificacionRepository.findByRevisadaFalseAndExpedidaPorMourosubFalse())
                .thenReturn(List.of(certificacion));

        List<Certificacion> resultado = certificacionService.findPendientesRevision();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRevisada()).isFalse();
    }

    // --- countPendientesRevision ---

    @Test
    void countPendientesRevision_debeRetornarElConteo() {
        when(certificacionRepository.countByRevisadaFalseAndExpedidaPorMourosubFalse()).thenReturn(3L);

        long conteo = certificacionService.countPendientesRevision();

        assertThat(conteo).isEqualTo(3L);
    }

    // --- save ---

    @Test
    void save_cuandoNoTieneFechaRegistro_debeAsignarFechaActual() {
        certificacion.setFechaRegistro(null);
        when(certificacionRepository.save(any(Certificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Certificacion guardada = certificacionService.save(certificacion);

        assertThat(guardada.getFechaRegistro()).isNotNull();
        verify(certificacionRepository).save(certificacion);
    }

    @Test
    void save_cuandoYaTieneFechaRegistro_noDebeModificarla() {
        LocalDateTime fechaOriginal = LocalDateTime.of(2024, 1, 15, 10, 0);
        certificacion.setFechaRegistro(fechaOriginal);
        when(certificacionRepository.save(any(Certificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Certificacion guardada = certificacionService.save(certificacion);

        assertThat(guardada.getFechaRegistro()).isEqualTo(fechaOriginal);
    }

    // --- deleteById ---

    @Test
    void deleteById_debeInvocarElRepositorio() {
        doNothing().when(certificacionRepository).deleteById(1L);

        certificacionService.deleteById(1L);

        verify(certificacionRepository).deleteById(1L);
    }

    // --- revisar ---

    @Test
    void revisar_cuandoExiste_debeActualizarEstadoYRetornarTrue() {
        when(certificacionRepository.findById(1L)).thenReturn(Optional.of(certificacion));
        when(certificacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        boolean resultado = certificacionService.revisar(1L, true, "Todo correcto");

        assertThat(resultado).isTrue();
        assertThat(certificacion.getRevisada()).isTrue();
        assertThat(certificacion.getValidada()).isTrue();
        assertThat(certificacion.getNotasAdmin()).isEqualTo("Todo correcto");
    }

    @Test
    void revisar_cuandoNoExiste_debeRetornarFalse() {
        when(certificacionRepository.findById(99L)).thenReturn(Optional.empty());

        boolean resultado = certificacionService.revisar(99L, false, "");

        assertThat(resultado).isFalse();
        verify(certificacionRepository, never()).save(any());
    }

    // --- crearCertificacionMouro ---

    @Test
    void crearCertificacionMouro_cuandoUsuarioExiste_debeCrearConFlagsCorrectos() {
        when(usuarioRepository.findById("12345678A")).thenReturn(Optional.of(usuario));
        when(certificacionRepository.save(any(Certificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Certificacion nueva = new Certificacion();
        nueva.setTitulo("Divemaster");

        Certificacion resultado = certificacionService.crearCertificacionMouro("12345678A", nueva);

        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getExpedidaPorMourosub()).isTrue();
        assertThat(resultado.getRevisada()).isTrue();
        assertThat(resultado.getValidada()).isTrue();
        assertThat(resultado.getFechaRegistro()).isNotNull();
    }

    @Test
    void crearCertificacionMouro_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findById("NOEXISTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificacionService.crearCertificacionMouro("NOEXISTE", new Certificacion()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("NOEXISTE");
    }
}