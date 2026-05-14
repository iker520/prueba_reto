package com.example.mourosub.service;

import com.example.mourosub.model.Noticia;
import com.example.mourosub.repository.NoticiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;

    public NoticiaService(NoticiaRepository noticiaRepository) {
        this.noticiaRepository = noticiaRepository;
    }

    public List<Noticia> findAll() {
        return noticiaRepository.findAll();
    }

    public List<Noticia> findAllPublicadas() {
        return noticiaRepository.findByPublicadaTrueOrderByFechaPublicacionDesc();
    }

    public List<Noticia> findUltimas3() {
        return noticiaRepository.findTop3ByPublicadaTrueOrderByFechaPublicacionDesc();
    }

    public List<Noticia> findByCategoria(String categoria) {
        return noticiaRepository.findByCategoriaAndPublicadaTrue(categoria);
    }

    public Optional<Noticia> findById(Long id) {
        return noticiaRepository.findById(id);
    }

    public Noticia save(Noticia noticia) {
        if (noticia.getFechaPublicacion() == null) {
            noticia.setFechaPublicacion(LocalDate.now());
        }
        return noticiaRepository.save(noticia);
    }

    public void deleteById(Long id) {
        noticiaRepository.deleteById(id);
    }

    public long count() {
        return noticiaRepository.count();
    }

    /** Categorías disponibles para el desplegable */
    public static List<String> getCategoriasDisponibles() {
        return List.of("EXPEDICION", "FORMACION", "ECOSISTEMA",
                       "TECNOLOGIA", "EVENTOS", "NOTICIAS");
    }
}
