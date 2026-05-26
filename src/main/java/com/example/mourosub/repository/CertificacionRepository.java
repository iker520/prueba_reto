package com.example.mourosub.repository;

import com.example.mourosub.model.Certificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificacionRepository extends JpaRepository<Certificacion, Long> {

    /** Certs de un usuario concreto (todas: propias y Mouro). */
    List<Certificacion> findByUsuarioDniUsuario(String dniUsuario);

    /** Certs propias del usuario (aportadas en el registro). */
    List<Certificacion> findByUsuarioDniUsuarioAndExpedidaPorMourosubFalse(String dniUsuario);

    /** Certs expedidas por MouroSub a un usuario concreto. */
    List<Certificacion> findByUsuarioDniUsuarioAndExpedidaPorMourosubTrue(String dniUsuario);

    /** Todas las certs expedidas por MouroSub. */
    List<Certificacion> findByExpedidaPorMourosubTrue();

    /** Certs de usuario pendientes de revisión (revisada = false, no Mouro). */
    List<Certificacion> findByRevisadaFalseAndExpedidaPorMourosubFalse();

    /** Cuántas certs de usuario pendientes de revisión. */
    long countByRevisadaFalseAndExpedidaPorMourosubFalse();

    /** Certs ya revisadas y validadas del usuario. */
    List<Certificacion> findByValidadaTrue();
}
