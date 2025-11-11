package com.example.evaluacion2IngDeSoftware.Repositorio;

import com.example.evaluacion2IngDeSoftware.Modelo.Mueble;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuebleRepositorio extends JpaRepository<Mueble, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
