package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "USUARIOS_RESERVAS")
@IdClass(UsuarioReservaId.class)
@Data
@NoArgsConstructor
public class UsuarioReserva {

    @Id
    @Column(name = "id_reserva")
    private Long idReserva;

    @Id
    @Column(name = "dni_usuario", length = 20)
    private String dniUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", insertable = false, updatable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_usuario", insertable = false, updatable = false)
    private Usuario usuario;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "cantidad")
    private Integer cantidad = 1;
}
