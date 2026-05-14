package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "CERTIFICACIONES")
@Data
@NoArgsConstructor
public class Certificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_certificacion")
    private Long numCertificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dni_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "entidad_certificadora", length = 150)
    private String entidadCertificadora;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "revisada")
    private Boolean revisada = false;

    @Column(name = "validada")
    private Boolean validada = false;
}
