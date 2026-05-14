package com.example.mourosub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioReservaId implements Serializable {
    private Long idReserva;
    private String dniUsuario;
}
