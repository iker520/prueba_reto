package com.example.mourosub.repository;

import com.example.mourosub.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    List<Actividad> findByActivaTrue();
    List<Actividad> findByTipoAndActivaTrue(String tipo);
    List<Actividad> findByDestacadaTrueAndActivaTrue();
    List<Actividad> findByNombreContainingIgnoreCase(String nombre);
    List<Actividad> findByNombreContainingIgnoreCaseAndActivaTrue(String nombre);
    List<Actividad> findByTipoAndSubtipoAndActivaTrue(String tipo, String subtipo);
    List<Actividad> findByTipoAndUbicaciones_NombreAndActivaTrue(String tipo, String ubicacionNombre);

}
