package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    /**
     * true = emitida por MouroSub al alumno.
     * false = aportada por el usuario al registrarse.
     */
    @Column(name = "expedida_por_mourosub")
    private Boolean expedidaPorMourosub = false;

    /**
     * Ruta local (o URL FTP en el futuro) del documento PDF/imagen adjunto.
     */
    @Column(name = "documento_url", length = 500)
    private String documentoUrl;

    /**
     * Hash SHA-256 del fichero para verificar integridad (útil cuando se migre a FTP).
     */
    @Column(name = "documento_hash", length = 100)
    private String documentoHash;

    /**
     * Notas del administrador al revisar/validar la certificación.
     */
    @Column(name = "notas_admin", length = 1000)
    private String notasAdmin;

    /**
     * Fecha en que se registró o expidió la certificación en el sistema.
     */
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
