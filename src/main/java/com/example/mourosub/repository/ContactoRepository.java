package com.example.mourosub.repository;

import com.example.mourosub.model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {

    List<Contacto> findByEstadoOrderByFechaEnvioDesc(String estado);

    List<Contacto> findAllByOrderByFechaEnvioDesc();

    long countByEstado(String estado);
}
