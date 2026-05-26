package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "cantidad")
    private Integer cantidad = 1;

    /** Copia del campo esBuceador del usuario en el momento de hacer la reserva */
    @Column(name = "es_buceador")
    private Boolean esBuceador = false;
}
