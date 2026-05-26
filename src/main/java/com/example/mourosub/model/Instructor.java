package com.example.mourosub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "INSTRUCTORES")
@Data
@NoArgsConstructor
public class Instructor {

    @Id
    @Column(name = "dni_instructor", length = 20)
    private String dniInstructor;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido_1", length = 100)
    private String apellido1;

    @Column(name = "apellido_2", length = 100)
    private String apellido2;

    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "experiencia", columnDefinition = "TEXT")
    private String experiencia;

    // Campos adicionales para el frontend
    @Column(name = "titulo_padi", length = 200)
    private String tituloPadi;

    @Column(name = "especialidad", length = 200)
    private String especialidad;


    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstructorReserva> reservas;

    public String getNombreCompleto() {
        return nombre + (apellido1 != null ? " " + apellido1 : "") + (apellido2 != null ? " " + apellido2 : "");
    }
}
