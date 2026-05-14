package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "ACTIVIDADES")
@Data
@NoArgsConstructor
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Long idActividad;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Descripción larga en HTML generada por TipTap.
     * Se renderiza con th:utext en las vistas públicas.
     */
    @Column(name = "descripcion_html", columnDefinition = "LONGTEXT")
    private String descripcionHtml;

    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;

    /**
     * Tipo de actividad como String: CURSO, INMERSION, ACTIVIDAD, OFERTA
     */
    @Column(name = "tipo", length = 50)
    private String tipo;

    // Campos extra para el frontend
    @Column(name = "nivel", length = 100)
    private String nivel;

    @Column(name = "duracion", length = 100)
    private String duracion;

    @Column(name = "plazas_maximas")
    private Integer plazasMaximas;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "destacada")
    private Boolean destacada = false;

    @Column(name = "activa")
    private Boolean activa = true;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActividadUbicacion> ubicaciones;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActividadReserva> reservas;
}
