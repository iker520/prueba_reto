package com.example.mourosub.repository;

import com.example.mourosub.model.ActividadReservaUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadReservaUbicacionRepository extends JpaRepository<ActividadReservaUbicacion, Long> {
    List<ActividadReservaUbicacion> findByIdReservaAndIdActividad(Long idReserva, Long idActividad);
    List<ActividadReservaUbicacion> findByIdReserva(Long idReserva);
}
