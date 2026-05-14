package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "UBICACIONES")
@Data
@NoArgsConstructor
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Long idUbicacion;

    @Column(name = "nombre", length = 200)
    private String nombre;

    @Column(name = "tipo_fondo", length = 100)
    private String tipoFondo;

    @Column(name = "profundidad_max")
    private Integer profundidadMax;

    @Column(name = "nivel_buceo", length = 100)
    private String nivelBuceo;

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActividadUbicacion> actividades;
}
