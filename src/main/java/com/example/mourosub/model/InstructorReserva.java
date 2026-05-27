package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "INSTRUCTORES_RESERVAS")
@IdClass(InstructorReservaId.class)
@Data
@NoArgsConstructor
public class InstructorReserva {

    @Id
    @Column(name = "id_reserva")
    private Long idReserva;

    @Id
    @Column(name = "id_actividad")
    private Long idActividad;

    @Id
    @Column(name = "dni_instructor", length = 20)
    private String dniInstructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "id_reserva", referencedColumnName = "id_reserva", insertable = false, updatable = false),
        @JoinColumn(name = "id_actividad", referencedColumnName = "id_actividad", insertable = false, updatable = false)
    })
    private ActividadReserva actividadReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_instructor", insertable = false, updatable = false)
    private Instructor instructor;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
}
