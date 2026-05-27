package com.example.mourosub.service;

import com.example.mourosub.model.Contacto;
import com.example.mourosub.repository.ContactoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de ContactoService")
class ContactoServiceTest {

    @Mock
    private ContactoRepository contactoRepository;

    @InjectMocks
    private ContactoService contactoService;

    private Contacto contacto;

    @BeforeEach
    void setUp() {
        contacto = new Contacto();
        contacto.setIdContacto(1L);
        contacto.setNombre("María");
        contacto.setEmail("maria@email.com");
        contacto.setMensaje("Me interesa el curso Open Water");
        contacto.setEstado("NUEVA");
    }

    // ─── guardar ──────────────────────────────────────────────
    @Test
    @DisplayName("guardar persiste el contacto y lo devuelve")
    void guardar_persisteYDevuelveContacto() {
        when(contactoRepository.save(contacto)).thenReturn(contacto);

        Contacto result = contactoService.guardar(contacto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("maria@email.com");
        verify(contactoRepository).save(contacto);
    }

    // ─── findAll ──────────────────────────────────────────────
    @Test
    @DisplayName("findAll retorna todos los contactos ordenados por fecha")
    void findAll_retornaTodosOrdenadosPorFecha() {
        when(contactoRepository.findAllByOrderByFechaEnvioDesc()).thenReturn(List.of(contacto));

        List<Contacto> result = contactoService.findAll();

        assertThat(result).hasSize(1);
        verify(contactoRepository).findAllByOrderByFechaEnvioDesc();
    }

    // ─── findByEstado ─────────────────────────────────────────
    @Test
    @DisplayName("findByEstado filtra contactos por estado")
    void findByEstado_filtrarPorNueva() {
        when(contactoRepository.findByEstadoOrderByFechaEnvioDesc("NUEVA"))
                .thenReturn(List.of(contacto));

        List<Contacto> result = contactoService.findByEstado("NUEVA");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo("NUEVA");
    }

    @Test
    @DisplayName("findByEstado retorna vacío si no hay contactos con ese estado")
    void findByEstado_retornaVacioSiNoHay() {
        when(contactoRepository.findByEstadoOrderByFechaEnvioDesc("RESPONDIDA"))
                .thenReturn(List.of());

        List<Contacto> result = contactoService.findByEstado("RESPONDIDA");

        assertThat(result).isEmpty();
    }

    // ─── findById ─────────────────────────────────────────────
    @Test
    @DisplayName("findById retorna contacto si existe")
    void findById_retornaContactoSiExiste() {
        when(contactoRepository.findById(1L)).thenReturn(Optional.of(contacto));

        Optional<Contacto> result = contactoService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("María");
    }

    @Test
    @DisplayName("findById retorna vacío si no existe")
    void findById_retornaVacioSiNoExiste() {
        when(contactoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Contacto> result = contactoService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ─── cambiarEstado ────────────────────────────────────────
    @Test
    @DisplayName("cambiarEstado actualiza el estado del contacto")
    void cambiarEstado_actualizaEstado() {
        when(contactoRepository.findById(1L)).thenReturn(Optional.of(contacto));
        when(contactoRepository.save(any(Contacto.class))).thenReturn(contacto);

        contactoService.cambiarEstado(1L, "LEIDA");

        assertThat(contacto.getEstado()).isEqualTo("LEIDA");
        verify(contactoRepository).save(contacto);
    }

    @Test
    @DisplayName("cambiarEstado no hace nada si el contacto no existe")
    void cambiarEstado_noHaceNadaSiNoExiste() {
        when(contactoRepository.findById(99L)).thenReturn(Optional.empty());

        contactoService.cambiarEstado(99L, "LEIDA");

        verify(contactoRepository, never()).save(any());
    }

    // ─── deleteById ───────────────────────────────────────────
    @Test
    @DisplayName("deleteById llama al repositorio con el id correcto")
    void deleteById_llamaAlRepositorio() {
        doNothing().when(contactoRepository).deleteById(1L);

        contactoService.deleteById(1L);

        verify(contactoRepository).deleteById(1L);
    }

    // ─── countNuevas ─────────────────────────────────────────
    @Test
    @DisplayName("countNuevas retorna el número de contactos nuevos")
    void countNuevas_retornaConteoNuevas() {
        when(contactoRepository.countByEstado("NUEVA")).thenReturn(7L);

        long result = contactoService.countNuevas();

        assertThat(result).isEqualTo(7L);
    }

    // ─── getEstadosDisponibles ────────────────────────────────
    @Test
    @DisplayName("getEstadosDisponibles retorna los 3 estados")
    void getEstadosDisponibles_retornaLos3Estados() {
        List<String> estados = ContactoService.getEstadosDisponibles();

        assertThat(estados).hasSize(3)
                .containsExactly("NUEVA", "LEIDA", "RESPONDIDA");
    }
}