package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

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



    @Column(name = "precio")
    private Double precio;

    /**
     * Tipo de actividad como String: Curso, Inmersion, Actividad, Oferta
     */
    @Column(name = "tipo", length = 50)
    private String tipo;

    /**
     * Subtipo para categorizar dentro de un tipo (ej. "Apnea", "Formación" para CURSO u OFERTA)
     */
    @Column(name = "subtipo", length = 100)
    private String subtipo;

    // Campos extra para el frontend
    @Column(name = "nivel", length = 100)
    private String nivel;

    @Column(name = "duracion_minutos")
    private Integer duracion;

    @Column(name = "plazas_maximas")
    private Integer plazasMaximas;



    @Column(name = "destacada")
    private Boolean destacada = false;

    @Column(name = "activa")
    private Boolean activa = true;

    @Column(name = "descripcion_html", columnDefinition = "LONGTEXT")
    private String descripcionHtml;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;


    @ManyToMany
    @JoinTable(
            name = "actividades_ubicaciones",
            joinColumns = @JoinColumn(name = "id_actividad"),
            inverseJoinColumns = @JoinColumn(name = "id_ubicacion"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_actividades_ubicaciones",
                    columnNames = {"id_actividad", "id_ubicacion"}
            )
    )
    private Set<Ubicacion> ubicaciones;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActividadReserva> reservas;

    @PrePersist
    @PreUpdate
    public void normalizarCampos() {
        if (this.tipo != null) this.tipo = this.tipo.toLowerCase();
    }

    public String getDuracionFormateada() {
        if (duracion == null) return "N/A";
        if (duracion >= 1440) {
            int dias = duracion / 1440;
            return dias + (dias == 1 ? " día" : " días");
        }
        if (duracion >= 60) {
            int horas = duracion / 60;
            int min = duracion % 60;
            return horas + " h" + (min > 0 ? " " + min + " min" : "");
        }
        return duracion + " min";
    }

}
