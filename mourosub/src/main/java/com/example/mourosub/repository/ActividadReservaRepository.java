package com.example.mourosub.repository;

import com.example.mourosub.model.ActividadReserva;
import com.example.mourosub.model.ActividadReservaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadReservaRepository extends JpaRepository<ActividadReserva, ActividadReservaId> {

    List<ActividadReserva> findByIdReserva(Long idReserva);
}
