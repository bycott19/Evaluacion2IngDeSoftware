package com.example.evaluacion2IngDeSoftware.Servicios;

import com.example.evaluacion2IngDeSoftware.Modelo.Cotizacion;
import com.example.evaluacion2IngDeSoftware.Modelo.CotizacionItem;
import com.example.evaluacion2IngDeSoftware.Modelo.Mueble;
import com.example.evaluacion2IngDeSoftware.Modelo.Venta;
import com.example.evaluacion2IngDeSoftware.Repositorio.CotizacionRepositorio;
import com.example.evaluacion2IngDeSoftware.Repositorio.MuebleRepositorio;
import com.example.evaluacion2IngDeSoftware.Repositorio.VentaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VentaServicio {

    private final VentaRepositorio ventaRepo;
    private final CotizacionRepositorio cotRepo;
    private final MuebleRepositorio muebleRepo;

    public VentaServicio(VentaRepositorio ventaRepo,
                         CotizacionRepositorio cotRepo,
                         MuebleRepositorio muebleRepo) {
        this.ventaRepo = ventaRepo;
        this.cotRepo = cotRepo;
        this.muebleRepo = muebleRepo;
    }

    @Transactional
    public Venta confirmarVentaDesdeCotizacion(Long cotizacionId) {
        Cotizacion c = cotRepo.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("No existe la cotización " + cotizacionId));

        if (Boolean.FALSE.equals(c.getConfirmada())) {
            throw new RuntimeException("Primero confirma la cotización antes de generar la venta.");
        }

        // <-- VALIDACIÓN CLAVE: si ya hay venta para esta cotización, abortar con mensaje claro
        if (ventaRepo.existsByCotizacionId(cotizacionId)) {
            throw new RuntimeException("La cotización " + cotizacionId + " ya fue convertida en venta anteriormente.");
        }

        // Verificar stock suficiente por cada ítem
        for (CotizacionItem it : c.getItems()) {
            Mueble m = it.getMueble();
            int disponible = m.getStock();
            int requerido = it.getCantidad();
            if (requerido > disponible) {
                throw new RuntimeException("Stock insuficiente para el mueble '" + m.getNombre() +
                        "'. Disponible: " + disponible + ", requerido: " + requerido);
            }
        }

        // Descontar stock
        for (CotizacionItem it : c.getItems()) {
            Mueble m = it.getMueble();
            m.setStock(m.getStock() - it.getCantidad());
            muebleRepo.save(m);
        }

        // Crear venta
        Venta v = new Venta();
        v.setCotizacion(c);
        v.setFechaConfirmacion(LocalDateTime.now());
        v.setTotal(c.getTotal());
        return ventaRepo.save(v);
    }
}
