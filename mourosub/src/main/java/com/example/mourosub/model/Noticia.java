package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "NOTICIAS")
@Data
@NoArgsConstructor
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_noticia")
    private Long idNoticia;

    @Column(name = "titulo", nullable = false, length = 300)
    private String titulo;

    @Column(name = "resumen", columnDefinition = "TEXT")
    private String resumen;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "cuerpo_html", columnDefinition = "LONGTEXT")
    private String cuerpoHtml;

    /**
     * Categoría como String: EXPEDICION, FORMACION, ECOSISTEMA, TECNOLOGIA, EVENTOS, NOTICIAS
     */
    @Column(name = "categoria", length = 100)
    private String categoria;



    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @Column(name = "publicada")
    private Boolean publicada = false;

    @PrePersist
    public void prePersist() {
        if (fechaPublicacion == null) {
            fechaPublicacion = LocalDate.now();
        }
    }
}
