package com.example.mourosub.repository;

import com.example.mourosub.model.Certificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificacionRepository extends JpaRepository<Certificacion, Long> {
    List<Certificacion> findByUsuarioDniUsuario(String dniUsuario);
    List<Certificacion> findByValidadaTrue();
}
