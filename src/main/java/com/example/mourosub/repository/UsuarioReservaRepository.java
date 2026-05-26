package com.example.mourosub.repository;

import com.example.mourosub.model.UsuarioReserva;
import com.example.mourosub.model.UsuarioReservaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioReservaRepository extends JpaRepository<UsuarioReserva, UsuarioReservaId> {

    /** Todas las entradas de reserva de un usuario concreto */
    List<UsuarioReserva> findByDniUsuario(String dniUsuario);
}
