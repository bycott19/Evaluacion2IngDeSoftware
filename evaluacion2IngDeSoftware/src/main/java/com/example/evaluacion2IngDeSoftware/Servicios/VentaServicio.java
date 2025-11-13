package com.example.evaluacion2IngDeSoftware.Servicios;

import com.example.evaluacion2IngDeSoftware.Modelo.*;
import com.example.evaluacion2IngDeSoftware.Repositorio.CotizacionRepositorio;
import com.example.evaluacion2IngDeSoftware.Repositorio.MuebleRepositorio;
import com.example.evaluacion2IngDeSoftware.Repositorio.VentaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VentaServicio {

    @Autowired
    private VentaRepositorio ventaRepositorio;
    @Autowired
    private CotizacionRepositorio cotizacionRepositorio;
    @Autowired
    private MuebleRepositorio muebleRepositorio;

    @Transactional
    public Venta confirmarVentaDesdeCotizacion(Long cotizacionId) {
        Cotizacion c = cotizacionRepositorio.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("No existe la cotización " + cotizacionId));

        if (ventaRepositorio.existsByCotizacionId(cotizacionId)) {
            throw new RuntimeException("La cotización " + cotizacionId + " ya fue convertida en venta anteriormente.");
        }

        if (c.getItems().isEmpty()) {
            throw new RuntimeException("La cotización no tiene items");
        }

        for (CotizacionItem item : c.getItems()) {
            Mueble m = item.getMueble();

            if (m.getEstado() == EstadoMueble.INACTIVO) {
                throw new RuntimeException("Venta cancelada: El mueble '" + m.getNombre() + "' no está disponible.");
            }

            int disponible = m.getStock();
            int requerido = item.getCantidad();
            if (requerido > disponible) {
                throw new RuntimeException("Stock insuficiente para el mueble '" + m.getNombre() + "'. Disponible: " + disponible + ", requerido: " + requerido);
            }
        }
        c.setConfirmada(Boolean.TRUE);
        cotizacionRepositorio.save(c);

        for (CotizacionItem it : c.getItems()) {
            Mueble m = it.getMueble();
            m.setStock(m.getStock() - it.getCantidad());
            muebleRepositorio.save(m);
        }

        Venta v = new Venta();
        v.setCotizacion(c);
        v.setFechaConfirmacion(LocalDateTime.now());
        v.setTotal(c.getTotal());
        return ventaRepositorio.save(v);
    }
}