package com.example.evaluacion2IngDeSoftware.Repositorio;

import com.example.evaluacion2IngDeSoftware.Modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VentaRepositorio extends JpaRepository<Venta, Long> {
    boolean existsByCotizacionId(Long cotizacionId);
    Optional<Venta> findByCotizacionId(Long cotizacionId);
}
