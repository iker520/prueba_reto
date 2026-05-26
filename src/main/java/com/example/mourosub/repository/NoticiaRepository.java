package com.example.mourosub.repository;

import com.example.mourosub.model.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    List<Noticia> findByPublicadaTrueOrderByFechaPublicacionDesc();

    List<Noticia> findByCategoriaAndPublicadaTrue(String categoria);

    List<Noticia> findTop3ByPublicadaTrueOrderByFechaPublicacionDesc();

    List<Noticia> findByTituloContainingIgnoreCaseAndPublicadaTrue(String titulo);

    List<Noticia> findByHashtagsContainingIgnoreCaseAndPublicadaTrue(String hashtag);
}