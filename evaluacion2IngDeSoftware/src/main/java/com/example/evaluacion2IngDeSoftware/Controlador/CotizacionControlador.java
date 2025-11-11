package com.example.evaluacion2IngDeSoftware.Controlador;

import com.example.evaluacion2IngDeSoftware.Modelo.Cotizacion;
import com.example.evaluacion2IngDeSoftware.Servicios.CotizacionServicio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionControlador {

    private final CotizacionServicio servicio;

    public CotizacionControlador(CotizacionServicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cotizacion crear() {
        return servicio.crear();
    }

    @GetMapping("/{id}")
    public Cotizacion obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    // Agregar item: recibe ids por query params para mantener simple
    @PostMapping("/{id}/items")
    public Cotizacion agregarItem(@PathVariable Long id,
                                  @RequestParam Long muebleId,
                                  @RequestParam(required = false) Long varianteId,
                                  @RequestParam Integer cantidad) {
        return servicio.agregarItem(id, muebleId, varianteId, cantidad);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public Cotizacion quitarItem(@PathVariable Long id, @PathVariable Long itemId) {
        return servicio.quitarItem(id, itemId);
    }

    @PostMapping("/{id}/recalcular")
    public Cotizacion recalcular(@PathVariable Long id) {
        return servicio.recalcular(id);
    }

    @PostMapping("/{id}/confirmar")
    public Cotizacion confirmar(@PathVariable Long id) {
        return servicio.confirmar(id);
    }
}
