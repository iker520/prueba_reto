package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ACTIVIDADES_UBICACIONES")
@IdClass(ActividadUbicacionId.class)
@Data
@NoArgsConstructor
public class ActividadUbicacion {

    @Id
    @Column(name = "id_actividad")
    private Long idActividad;

    @Id
    @Column(name = "id_ubicacion")
    private Long idUbicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad", insertable = false, updatable = false)
    private Actividad actividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion", insertable = false, updatable = false)
    private Ubicacion ubicacion;
}
