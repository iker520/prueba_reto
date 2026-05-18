package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "RESERVAS")
@Data
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    /**
     * Estado de la reserva (String): PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
     */
    @Column(name = "estado", length = 50)
    private String estado = "PENDIENTE";

    @Column(name = "total")
    private Double total;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsuarioReserva> usuarios;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActividadReserva> actividades;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstructorReserva> instructores;
}
