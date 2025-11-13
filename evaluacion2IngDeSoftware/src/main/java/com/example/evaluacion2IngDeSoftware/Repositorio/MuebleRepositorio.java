package com.example.evaluacion2IngDeSoftware.Repositorio;

import com.example.evaluacion2IngDeSoftware.Modelo.EstadoMueble;
import com.example.evaluacion2IngDeSoftware.Modelo.Mueble;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface MuebleRepositorio extends JpaRepository<Mueble, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    List<Mueble> findByEstado(EstadoMueble estado);
}