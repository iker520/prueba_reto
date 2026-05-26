package com.example.mourosub.service;

import com.example.mourosub.model.Noticia;
import com.example.mourosub.repository.NoticiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<Noticia> findByHashtag(String hashtag) {
        return noticiaRepository.findByHashtagsContainingIgnoreCaseAndPublicadaTrue(hashtag);
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

    public static List<String> getCategoriasDisponibles() {
        return List.of("Expedicion", "Formacion", "Ecosistema",
                "Tecnologia", "Eventos", "Noticias");
    }

    // Devuelve los hashtags más usados ordenados por frecuencia, cn el limite como maximo
    public List<String> getHashtagsPopulares(int limite) {
        return noticiaRepository.findByPublicadaTrueOrderByFechaPublicacionDesc()
                .stream()
                .map(Noticia::getHashtags)
                .filter(h -> h != null && !h.isBlank())
                .flatMap(h -> Arrays.stream(h.split(",")))
                .map(String::trim)
                .filter(h -> !h.isEmpty())
                .collect(Collectors.groupingBy(h -> h, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limite)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}