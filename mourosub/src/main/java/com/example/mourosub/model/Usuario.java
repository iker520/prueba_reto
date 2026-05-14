package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "USUARIOS")
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    @Column(name = "dni_usuario", length = 20)
    private String dniUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido_1", length = 100)
    private String apellido1;

    @Column(name = "apellido_2", length = 100)
    private String apellido2;

    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "provincia", length = 100)
    private String provincia;

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    @Column(name = "pais", length = 100)
    private String pais;

    @Column(name = "tfno_sos", length = 20)
    private String tfnoSos;

    @Column(name = "notif_emails")
    private Boolean notifEmails = false;

    @Column(name = "notif_whatsapp")
    private Boolean notifWhatsapp = false;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "nivel_buceo", length = 100)
    private String nivelBuceo;

    @Column(name = "num_inmersiones")
    private Integer numInmersiones = 0;

    @Column(name = "fecha_ultima_inmersion")
    private LocalDate fechaUltimaInmersion;

    @Column(name = "seguro_accidentes")
    private Boolean seguroAccidentes = false;

    @Column(name = "compania_seguros", length = 150)
    private String companiaSeguros;

    @Column(name = "fecha_vto")
    private LocalDate fechaVto;

    // --- Campos para autenticación Spring Security ---
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol = "ROLE_USER";

    // --- Relaciones ---
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Certificacion> certificaciones;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsuarioReserva> reservas;
}
