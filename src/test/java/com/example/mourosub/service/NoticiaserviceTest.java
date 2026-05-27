package com.example.mourosub.service;

import com.example.mourosub.model.Noticia;
import com.example.mourosub.repository.NoticiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/*Noticia*/
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de NoticiaService")
class NoticiaServiceTest {

    @Mock
    private NoticiaRepository noticiaRepository;

    @InjectMocks
    private NoticiaService noticiaService;

    private Noticia noticia;

    @BeforeEach
    void setUp() {
        noticia = new Noticia();
        noticia.setIdNoticia(1L);
        noticia.setTitulo("Nueva expedición al Mediterráneo");
        noticia.setCategoria("Expedicion");
        noticia.setHashtags("buceo,mar,expedicion");
        noticia.setPublicada(true);
        noticia.setFechaPublicacion(LocalDate.now());
    }

    // ─── findAll ──────────────────────────────────────────────
    @Test
    @DisplayName("findAll retorna todas las noticias")
    void findAll_retornaTodasLasNoticias() {
        when(noticiaRepository.findAll()).thenReturn(List.of(noticia));

        List<Noticia> result = noticiaService.findAll();

        assertThat(result).hasSize(1);
        verify(noticiaRepository).findAll();
    }

    // ─── findAllPublicadas ────────────────────────────────────
    @Test
    @DisplayName("findAllPublicadas retorna solo las publicadas")
    void findAllPublicadas_retornaSoloPublicadas() {
        when(noticiaRepository.findByPublicadaTrueOrderByFechaPublicacionDesc())
                .thenReturn(List.of(noticia));

        List<Noticia> result = noticiaService.findAllPublicadas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPublicada()).isTrue();
    }

    // ─── findUltimas3 ─────────────────────────────────────────
    @Test
    @DisplayName("findUltimas3 retorna máximo 3 noticias")
    void findUltimas3_retornaMaximo3() {
        Noticia n2 = new Noticia();
        n2.setIdNoticia(2L);
        n2.setPublicada(true);
        Noticia n3 = new Noticia();
        n3.setIdNoticia(3L);
        n3.setPublicada(true);

        when(noticiaRepository.findTop3ByPublicadaTrueOrderByFechaPublicacionDesc())
                .thenReturn(List.of(noticia, n2, n3));

        List<Noticia> result = noticiaService.findUltimas3();

        assertThat(result).hasSize(3);
    }

    // ─── findByCategoria ──────────────────────────────────────
    @Test
    @DisplayName("findByCategoria filtra por categoría correctamente")
    void findByCategoria_filtraCorrecamente() {
        when(noticiaRepository.findByCategoriaAndPublicadaTrue("Expedicion"))
                .thenReturn(List.of(noticia));

        List<Noticia> result = noticiaService.findByCategoria("Expedicion");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoria()).isEqualTo("Expedicion");
    }

    @Test
    @DisplayName("findByCategoria retorna vacío si no hay noticias de esa categoría")
    void findByCategoria_retornaVacioSiNoHay() {
        when(noticiaRepository.findByCategoriaAndPublicadaTrue("Tecnologia"))
                .thenReturn(List.of());

        List<Noticia> result = noticiaService.findByCategoria("Tecnologia");

        assertThat(result).isEmpty();
    }

    // ─── findByHashtag ────────────────────────────────────────
    @Test
    @DisplayName("findByHashtag busca por hashtag correctamente")
    void findByHashtag_buscaCorrecamente() {
        when(noticiaRepository.findByHashtagsContainingIgnoreCaseAndPublicadaTrue("buceo"))
                .thenReturn(List.of(noticia));

        List<Noticia> result = noticiaService.findByHashtag("buceo");

        assertThat(result).hasSize(1);
    }

    // ─── findById ─────────────────────────────────────────────
    @Test
    @DisplayName("findById retorna noticia si existe")
    void findById_retornaNoticiaSiExiste() {
        when(noticiaRepository.findById(1L)).thenReturn(Optional.of(noticia));

        Optional<Noticia> result = noticiaService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTitulo()).isEqualTo("Nueva expedición al Mediterráneo");
    }

    // ─── save ─────────────────────────────────────────────────
    @Test
    @DisplayName("save establece fechaPublicacion si es null")
    void save_estableceFechaPublicacionSiEsNull() {
        noticia.setFechaPublicacion(null);
        when(noticiaRepository.save(any(Noticia.class))).thenReturn(noticia);

        noticiaService.save(noticia);

        assertThat(noticia.getFechaPublicacion()).isNotNull();
        verify(noticiaRepository).save(noticia);
    }

    @Test
    @DisplayName("save no sobreescribe fechaPublicacion si ya tiene valor")
    void save_noSobreescribeFechaExistente() {
        LocalDate fechaOriginal = LocalDate.of(2025, 1, 15);
        noticia.setFechaPublicacion(fechaOriginal);
        when(noticiaRepository.save(any(Noticia.class))).thenReturn(noticia);

        noticiaService.save(noticia);

        assertThat(noticia.getFechaPublicacion()).isEqualTo(fechaOriginal);
    }

    // ─── deleteById ───────────────────────────────────────────
    @Test
    @DisplayName("deleteById llama al repositorio")
    void deleteById_llamaAlRepositorio() {
        doNothing().when(noticiaRepository).deleteById(1L);

        noticiaService.deleteById(1L);

        verify(noticiaRepository).deleteById(1L);
    }

    // ─── count ────────────────────────────────────────────────
    @Test
    @DisplayName("count retorna el total de noticias")
    void count_retornaTotal() {
        when(noticiaRepository.count()).thenReturn(12L);

        long result = noticiaService.count();

        assertThat(result).isEqualTo(12L);
    }

    // ─── getCategoriasDisponibles ─────────────────────────────
    @Test
    @DisplayName("getCategoriasDisponibles retorna las 6 categorías")
    void getCategoriasDisponibles_retorna6Categorias() {
        List<String> categorias = NoticiaService.getCategoriasDisponibles();

        assertThat(categorias).hasSize(6)
                .contains("Expedicion", "Formacion", "Ecosistema",
                        "Tecnologia", "Eventos", "Noticias");
    }

    // ─── getHashtagsPopulares ─────────────────────────────────
    @Test
    @DisplayName("getHashtagsPopulares retorna los hashtags más usados")
    void getHashtagsPopulares_retornaOrdenadosPorFrecuencia() {
        Noticia n1 = new Noticia();
        n1.setHashtags("buceo,mar");
        n1.setPublicada(true);
        Noticia n2 = new Noticia();
        n2.setHashtags("buceo,coral");
        n2.setPublicada(true);

        when(noticiaRepository.findByPublicadaTrueOrderByFechaPublicacionDesc())
                .thenReturn(List.of(n1, n2));

        List<String> result = noticiaService.getHashtagsPopulares(3);

        // "buceo" aparece 2 veces → debe ser el primero
        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).isEqualTo("buceo");
    }

    @Test
    @DisplayName("getHashtagsPopulares ignora hashtags nulos o vacíos")
    void getHashtagsPopulares_ignoraNulosYVacios() {
        Noticia n1 = new Noticia();
        n1.setHashtags(null);
        n1.setPublicada(true);
        Noticia n2 = new Noticia();
        n2.setHashtags("  ");
        n2.setPublicada(true);

        when(noticiaRepository.findByPublicadaTrueOrderByFechaPublicacionDesc())
                .thenReturn(List.of(n1, n2));

        List<String> result = noticiaService.getHashtagsPopulares(5);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getHashtagsPopulares respeta el límite indicado")
    void getHashtagsPopulares_respetaLimite() {
        Noticia n1 = new Noticia();
        n1.setHashtags("a,b,c,d,e");
        n1.setPublicada(true);

        when(noticiaRepository.findByPublicadaTrueOrderByFechaPublicacionDesc())
                .thenReturn(List.of(n1));

        List<String> result = noticiaService.getHashtagsPopulares(2);

        assertThat(result).hasSize(2);
    }
}