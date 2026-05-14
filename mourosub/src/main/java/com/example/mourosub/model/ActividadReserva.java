package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
}
