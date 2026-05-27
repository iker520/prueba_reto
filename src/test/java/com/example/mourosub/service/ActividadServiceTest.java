package com.example.mourosub.service;

import com.example.mourosub.model.Actividad;
import com.example.mourosub.repository.ActividadRepository;
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
import static org.mockito.Mockito.*;
/*Actividad*/
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de ActividadService")
class ActividadServiceTest {

    @Mock
    private ActividadRepository actividadRepository;

    @InjectMocks
    private ActividadService actividadService;

    private Actividad actividad;

    @BeforeEach
    void setUp() {
        actividad = new Actividad();
        actividad.setIdActividad(1L);
        actividad.setNombre("Open Water Diver");
        actividad.setTipo("curso");
        actividad.setActiva(true);
        actividad.setDestacada(true);
        actividad.setPrecio(350.0);
        actividad.setDuracion(2880); // 2 días
    }

    // ─── findAll ──────────────────────────────────────────────
    @Test
    @DisplayName("findAll devuelve todas las actividades")
    void findAll_debeRetornarTodasLasActividades() {
        when(actividadRepository.findAll()).thenReturn(List.of(actividad));

        List<Actividad> result = actividadService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Open Water Diver");
        verify(actividadRepository).findAll();
    }

    @Test
    @DisplayName("findAll devuelve lista vacía si no hay actividades")
    void findAll_debeRetornarListaVacia() {
        when(actividadRepository.findAll()).thenReturn(List.of());

        List<Actividad> result = actividadService.findAll();

        assertThat(result).isEmpty();
    }

    // ─── findAllActivas ───────────────────────────────────────
    @Test
    @DisplayName("findAllActivas devuelve solo actividades activas")
    void findAllActivas_debeRetornarSoloActivas() {
        when(actividadRepository.findByActivaTrue()).thenReturn(List.of(actividad));

        List<Actividad> result = actividadService.findAllActivas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActiva()).isTrue();
        verify(actividadRepository).findByActivaTrue();
    }

    // ─── searchActivas ────────────────────────────────────────
    @Test
    @DisplayName("searchActivas filtra por keyword correctamente")
    void searchActivas_debeRetornarActividadesFiltradas() {
        when(actividadRepository.findByNombreContainingIgnoreCaseAndActivaTrue("water"))
                .thenReturn(List.of(actividad));

        List<Actividad> result = actividadService.searchActivas("water");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).containsIgnoringCase("water");
    }

    @Test
    @DisplayName("searchActivas devuelve vacío si no hay coincidencias")
    void searchActivas_sinCoincidenciasDevuelveVacio() {
        when(actividadRepository.findByNombreContainingIgnoreCaseAndActivaTrue("xyz"))
                .thenReturn(List.of());

        List<Actividad> result = actividadService.searchActivas("xyz");

        assertThat(result).isEmpty();
    }

    // ─── findByTipo ───────────────────────────────────────────
    @Test
    @DisplayName("findByTipo retorna actividades del tipo indicado")
    void findByTipo_debeRetornarActividadesDelTipo() {
        when(actividadRepository.findByTipoAndActivaTrue("curso")).thenReturn(List.of(actividad));

        List<Actividad> result = actividadService.findByTipo("curso");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTipo()).isEqualTo("curso");
    }

    // ─── findDestacadas ───────────────────────────────────────
    @Test
    @DisplayName("findDestacadas retorna solo actividades destacadas y activas")
    void findDestacadas_debeRetornarSoloDestacadasActivas() {
        when(actividadRepository.findByDestacadaTrueAndActivaTrue()).thenReturn(List.of(actividad));

        List<Actividad> result = actividadService.findDestacadas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDestacada()).isTrue();
        assertThat(result.get(0).getActiva()).isTrue();
    }

    // ─── findById ─────────────────────────────────────────────
    @Test
    @DisplayName("findById retorna actividad cuando existe")
    void findById_debeRetornarActividadSiExiste() {
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));

        Optional<Actividad> result = actividadService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getIdActividad()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById retorna vacío cuando no existe")
    void findById_debeRetornarVacioSiNoExiste() {
        when(actividadRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Actividad> result = actividadService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ─── save ─────────────────────────────────────────────────
    @Test
    @DisplayName("save persiste y retorna la actividad")
    void save_debePersistirActividad() {
        when(actividadRepository.save(actividad)).thenReturn(actividad);

        Actividad result = actividadService.save(actividad);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Open Water Diver");
        verify(actividadRepository).save(actividad);
    }

    // ─── deleteById ───────────────────────────────────────────
    @Test
    @DisplayName("deleteById llama al repositorio con el id correcto")
    void deleteById_debeLlamarAlRepositorio() {
        doNothing().when(actividadRepository).deleteById(1L);

        actividadService.deleteById(1L);

        verify(actividadRepository).deleteById(1L);
    }

    // ─── count ────────────────────────────────────────────────
    @Test
    @DisplayName("count retorna el número total de actividades")
    void count_debeRetornarTotal() {
        when(actividadRepository.count()).thenReturn(5L);

        long result = actividadService.count();

        assertThat(result).isEqualTo(5L);
    }

    // ─── getTiposDisponibles ──────────────────────────────────
    @Test
    @DisplayName("getTiposDisponibles retorna los 4 tipos")
    void getTiposDisponibles_debeRetornar4Tipos() {
        List<String> tipos = ActividadService.getTiposDisponibles();

        assertThat(tipos).hasSize(4)
                .containsExactly("CURSO", "INMERSION", "ACTIVIDAD", "OFERTA");
    }

    // ─── getNivelesDisponibles ────────────────────────────────
    @Test
    @DisplayName("getNivelesDisponibles retorna los niveles correctos")
    void getNivelesDisponibles_debeContenerNiveles() {
        List<String> niveles = ActividadService.getNivelesDisponibles();

        assertThat(niveles).isNotEmpty()
                .contains("Open Water", "Advanced Open Water", "Divemaster/DiveGuide");
    }

    // ─── getDuracionFormateada ────────────────────────────────
    @Test
    @DisplayName("getDuracionFormateada formatea días correctamente")
    void getDuracionFormateada_diasCorrectos() {
        actividad.setDuracion(2880); // 2 días
        assertThat(actividad.getDuracionFormateada()).isEqualTo("2 días");
    }

    @Test
    @DisplayName("getDuracionFormateada formatea horas y minutos")
    void getDuracionFormateada_horasYMinutos() {
        actividad.setDuracion(90); // 1h 30min
        assertThat(actividad.getDuracionFormateada()).isEqualTo("1 h 30 min");
    }

    @Test
    @DisplayName("getDuracionFormateada formatea solo minutos")
    void getDuracionFormateada_soloMinutos() {
        actividad.setDuracion(45);
        assertThat(actividad.getDuracionFormateada()).isEqualTo("45 min");
    }

    @Test
    @DisplayName("getDuracionFormateada retorna N/A cuando es null")
    void getDuracionFormateada_nulo() {
        actividad.setDuracion(null);
        assertThat(actividad.getDuracionFormateada()).isEqualTo("N/A");
    }
}