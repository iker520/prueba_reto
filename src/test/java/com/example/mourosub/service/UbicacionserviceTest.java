package com.example.mourosub.service;

import com.example.mourosub.model.Ubicacion;
import com.example.mourosub.repository.UbicacionRepository;
import org.junit.jupiter.api.BeforeEach;
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
class UbicacionServiceTest {

    @Mock
    private UbicacionRepository ubicacionRepository;

    @InjectMocks
    private UbicacionService ubicacionService;

    private Ubicacion ubicacion;

    @BeforeEach
    void setUp() {
        ubicacion = new Ubicacion();
        ubicacion.setIdUbicacion(1L);
        ubicacion.setNombre("La Herradura");
        ubicacion.setTipoFondo("Arena y roca");
        ubicacion.setProfundidadMax(30);
        ubicacion.setNivelBuceo("Open Water");
    }

    // --- findAll ---

    @Test
    void findAll_debeRetornarTodasLasUbicaciones() {
        when(ubicacionRepository.findAll()).thenReturn(List.of(ubicacion));

        List<Ubicacion> resultado = ubicacionService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("La Herradura");
        verify(ubicacionRepository).findAll();
    }

    @Test
    void findAll_cuandoNoHayUbicaciones_debeRetornarListaVacia() {
        when(ubicacionRepository.findAll()).thenReturn(List.of());

        List<Ubicacion> resultado = ubicacionService.findAll();

        assertThat(resultado).isEmpty();
    }

    // --- findById ---

    @Test
    void findById_cuandoExiste_debeRetornarUbicacion() {
        when(ubicacionRepository.findById(1L)).thenReturn(Optional.of(ubicacion));

        Optional<Ubicacion> resultado = ubicacionService.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("La Herradura");
        assertThat(resultado.get().getProfundidadMax()).isEqualTo(30);
    }

    @Test
    void findById_cuandoNoExiste_debeRetornarVacio() {
        when(ubicacionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Ubicacion> resultado = ubicacionService.findById(99L);

        assertThat(resultado).isEmpty();
    }

    // --- save ---

    @Test
    void save_debeGuardarYRetornarLaUbicacion() {
        when(ubicacionRepository.save(any(Ubicacion.class))).thenReturn(ubicacion);

        Ubicacion guardada = ubicacionService.save(ubicacion);

        assertThat(guardada).isNotNull();
        assertThat(guardada.getNombre()).isEqualTo("La Herradura");
        verify(ubicacionRepository).save(ubicacion);
    }

    @Test
    void save_debeRetornarLaUbicacionConDatosCompletos() {
        when(ubicacionRepository.save(any(Ubicacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Ubicacion nueva = new Ubicacion();
        nueva.setNombre("Cabo de Gata");
        nueva.setTipoFondo("Roca");
        nueva.setProfundidadMax(18);
        nueva.setNivelBuceo("Advanced Open Water");

        Ubicacion resultado = ubicacionService.save(nueva);

        assertThat(resultado.getNombre()).isEqualTo("Cabo de Gata");
        assertThat(resultado.getTipoFondo()).isEqualTo("Roca");
        assertThat(resultado.getProfundidadMax()).isEqualTo(18);
    }

    // --- deleteById ---

    @Test
    void deleteById_debeInvocarElRepositorio() {
        doNothing().when(ubicacionRepository).deleteById(1L);

        ubicacionService.deleteById(1L);

        verify(ubicacionRepository).deleteById(1L);
        verify(ubicacionRepository, times(1)).deleteById(1L);
    }

    // --- count ---

    @Test
    void count_debeRetornarElTotalDeUbicaciones() {
        when(ubicacionRepository.count()).thenReturn(7L);

        long total = ubicacionService.count();

        assertThat(total).isEqualTo(7L);
        verify(ubicacionRepository).count();
    }

    @Test
    void count_cuandoNoHayUbicaciones_debeRetornarCero() {
        when(ubicacionRepository.count()).thenReturn(0L);

        long total = ubicacionService.count();

        assertThat(total).isZero();
    }

    // --- findAllById ---

    @Test
    void findAllById_debeRetornarUbicacionesPorIds() {
        Ubicacion segunda = new Ubicacion();
        segunda.setIdUbicacion(2L);
        segunda.setNombre("Punta de la Mona");

        List<Long> ids = List.of(1L, 2L);
        when(ubicacionRepository.findAllById(ids)).thenReturn(List.of(ubicacion, segunda));

        List<Ubicacion> resultado = ubicacionService.findAllById(ids);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Ubicacion::getNombre)
                .containsExactlyInAnyOrder("La Herradura", "Punta de la Mona");
    }

    @Test
    void findAllById_cuandoIdsNoExisten_debeRetornarListaVacia() {
        List<Long> ids = List.of(99L, 100L);
        when(ubicacionRepository.findAllById(ids)).thenReturn(List.of());

        List<Ubicacion> resultado = ubicacionService.findAllById(ids);

        assertThat(resultado).isEmpty();
    }
}