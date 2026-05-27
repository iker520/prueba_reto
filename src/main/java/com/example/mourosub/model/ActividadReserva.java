package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ACTIVIDADES_RESERVAS")
@IdClass(ActividadReservaId.class)
@Data
@NoArgsConstructor
public class ActividadReserva {

    @Id
    @Column(name = "id_reserva")
    private Long idReserva;

    @Id
    @Column(name = "id_actividad")
    private Long idActividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", insertable = false, updatable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad", insertable = false, updatable = false)
    private Actividad actividad;

    /** Precio en el momento de la reserva */
    @Column(name = "precio")
    private Double precio;

    @OneToMany(mappedBy = "actividadReserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<ActividadReservaUbicacion> programaciones = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "actividadReserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<InstructorReserva> instructores = new java.util.ArrayList<>();
}
