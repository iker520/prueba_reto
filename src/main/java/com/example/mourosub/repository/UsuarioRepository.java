package com.example.mourosub.repository;

import com.example.mourosub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    /** Buceadores cuyo seguro está en estado PENDIENTE (para badge de campanita). */
    long countByEsBuceadorTrueAndEstadoSeguro(String estadoSeguro);

    /** Lista de buceadores con seguro pendiente. */
    List<Usuario> findByEsBuceadorTrueAndEstadoSeguro(String estadoSeguro);
}
