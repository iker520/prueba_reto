package com.example.mourosub.service;

import com.example.mourosub.model.Instructor;
import com.example.mourosub.repository.InstructorRepository;
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
/*Instructor*/
@ExtendWith(MockitoExtension.class)
class InstructorServiceTest {

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private InstructorService instructorService;

    private Instructor instructor;

    @BeforeEach
    void setUp() {
        instructor = new Instructor();
        instructor.setDniInstructor("87654321B");
        instructor.setNombre("Carlos");
        instructor.setApellido1("Martínez");
        instructor.setEmail("carlos@mourosub.com");
        instructor.setTituloPadi("Open Water Instructor");
        instructor.setActivo(true);
    }

    // --- findAll ---

    @Test
    void findAll_debeRetornarTodosLosInstructores() {
        when(instructorRepository.findAll()).thenReturn(List.of(instructor));

        List<Instructor> resultado = instructorService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Carlos");
        verify(instructorRepository).findAll();
    }

    @Test
    void findAll_cuandoNoHayInstructores_debeRetornarListaVacia() {
        when(instructorRepository.findAll()).thenReturn(List.of());

        List<Instructor> resultado = instructorService.findAll();

        assertThat(resultado).isEmpty();
    }

    // --- findAllActivos ---

    @Test
    void findAllActivos_debeRetornarSoloLosActivos() {
        Instructor inactivo = new Instructor();
        inactivo.setDniInstructor("11111111C");
        inactivo.setActivo(false);

        when(instructorRepository.findByActivoTrue()).thenReturn(List.of(instructor));

        List<Instructor> resultado = instructorService.findAllActivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActivo()).isTrue();
        verify(instructorRepository).findByActivoTrue();
    }

    // --- findById ---

    @Test
    void findById_cuandoExiste_debeRetornarInstructor() {
        when(instructorRepository.findById("87654321B")).thenReturn(Optional.of(instructor));

        Optional<Instructor> resultado = instructorService.findById("87654321B");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDniInstructor()).isEqualTo("87654321B");
    }

    @Test
    void findById_cuandoNoExiste_debeRetornarVacio() {
        when(instructorRepository.findById("NOEXISTE")).thenReturn(Optional.empty());

        Optional<Instructor> resultado = instructorService.findById("NOEXISTE");

        assertThat(resultado).isEmpty();
    }

    // --- save ---

    @Test
    void save_debeGuardarYRetornarElInstructor() {
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor guardado = instructorService.save(instructor);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getEmail()).isEqualTo("carlos@mourosub.com");
        verify(instructorRepository).save(instructor);
    }

    // --- deleteById ---

    @Test
    void deleteById_debeInvocarElRepositorio() {
        doNothing().when(instructorRepository).deleteById("87654321B");

        instructorService.deleteById("87654321B");

        verify(instructorRepository).deleteById("87654321B");
    }

    // --- count ---

    @Test
    void count_debeRetornarElNumeroDeTotalDeInstructores() {
        when(instructorRepository.count()).thenReturn(5L);

        long total = instructorService.count();

        assertThat(total).isEqualTo(5L);
    }

    // --- getTitulosDisponibles (método estático) ---

    @Test
    void getTitulosDisponibles_debeRetornarListaNoVacia() {
        List<String> titulos = InstructorService.getTitulosDisponibles();

        assertThat(titulos).isNotEmpty();
        assertThat(titulos).contains("Open Water Instructor", "PADI Course Director", "Divemaster");
    }

    @Test
    void getTitulosDisponibles_debeContenerSeisOpciones() {
        List<String> titulos = InstructorService.getTitulosDisponibles();

        assertThat(titulos).hasSize(6);
    }

    // --- getNombreCompleto (método del modelo) ---

    @Test
    void getNombreCompleto_conAmbosPellidos_debeFormatearCorrectamente() {
        instructor.setApellido2("López");

        String nombreCompleto = instructor.getNombreCompleto();

        assertThat(nombreCompleto).isEqualTo("Carlos Martínez López");
    }

    @Test
    void getNombreCompleto_sinSegundoApellido_debeFormatearCorrectamente() {
        instructor.setApellido2(null);

        String nombreCompleto = instructor.getNombreCompleto();

        assertThat(nombreCompleto).isEqualTo("Carlos Martínez");
    }

    @Test
    void getNombreCompleto_sinApellidos_debeRetornarSoloNombre() {
        instructor.setApellido1(null);
        instructor.setApellido2(null);

        String nombreCompleto = instructor.getNombreCompleto();

        assertThat(nombreCompleto).isEqualTo("Carlos");
    }
}