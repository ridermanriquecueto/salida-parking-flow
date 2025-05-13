package com.example.estacionamiento_api.repository;

import com.example.estacionamiento_api.model.Estacionamiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EstacionamientoRepository extends JpaRepository<Estacionamiento, Long> {
    Page<Estacionamiento> findByHoraEntradaBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    List<Estacionamiento> findAllByHoraEntradaBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    List<Estacionamiento> findByHoraSalidaIsNull();

    // JPA proporciona automáticamente el método findAll(), pero puedes declararlo explícitamente si lo prefieres para mayor claridad.
    // List<Estacionamiento> findAll();
    Page<Estacionamiento> findByHoraEntradaBetweenAndTurno(LocalDateTime start, LocalDateTime end, String turno, Pageable pageable);
    List<Estacionamiento> findAllByHoraEntradaBetweenAndTurno(LocalDateTime start, LocalDateTime end, String turno, Pageable pageable);

    List<Estacionamiento> findByTurno(String turno);
}