package com.example.mourosub.repository;

import com.example.mourosub.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    @Query("SELECT DISTINCT u FROM Actividad a JOIN a.ubicaciones u WHERE a.tipo = :tipo AND a.activa = true")
    List<Ubicacion> findUbicacionesByActividadTipo(@Param("tipo") String tipo);

    java.util.Optional<Ubicacion> findByNombre(String nombre);
}
