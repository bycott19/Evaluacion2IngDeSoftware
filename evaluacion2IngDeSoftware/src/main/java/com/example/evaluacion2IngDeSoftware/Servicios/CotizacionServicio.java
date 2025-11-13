package com.example.evaluacion2IngDeSoftware.Servicios;

import com.example.evaluacion2IngDeSoftware.Modelo.*;
import com.example.evaluacion2IngDeSoftware.Repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;

@Service
public class CotizacionServicio {

    @Autowired
    private CotizacionRepositorio cotizacionRepositorio;
    @Autowired
    private CotizacionItemRepositorio cotizacionItemRepositorio;
    @Autowired
    private MuebleRepositorio muebleRepositorio;
    @Autowired
    private VarianteRepositorio varianteRepositorio;

    @Transactional
    public Cotizacion crear() {
        Cotizacion c = new Cotizacion();
        c.setFechaCreacion(LocalDateTime.now());
        c.setConfirmada(Boolean.FALSE);
        c.setTotal(BigDecimal.ZERO);
        return cotizacionRepositorio.save(c);
    }

    @Transactional(readOnly = true)
    public Cotizacion obtener(Long id) {
        Cotizacion c = cotizacionRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Cotización no encontrada: " + id));
        c.getItems().size();
        return c;
    }

    @Transactional
    public Cotizacion agregarItem(Long cotizacionId, Long muebleId, Long varianteId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) throw new RuntimeException("Cantidad inválida");

        Cotizacion c = obtener(cotizacionId);
        if (Boolean.TRUE.equals(c.getConfirmada())) throw new RuntimeException("La cotización ya está confirmada y no se puede modificar.");

        Mueble m = muebleRepositorio.findById(muebleId)
                .orElseThrow(() -> new RuntimeException("Mueble no encontrado: " + muebleId));

        if (m.getEstado() == EstadoMueble.INACTIVO) {
            throw new RuntimeException("El mueble '" + m.getNombre() + "' no está disponible por el momento.");
        }

        Variante v = null;
        if (varianteId != null) {
            v = varianteRepositorio.findById(varianteId)
                    .orElseThrow(() -> new RuntimeException("Variante no encontrada: " + varianteId));
        }

        BigDecimal precioBase = m.getPrecioBase() == null ? BigDecimal.ZERO : m.getPrecioBase();
        BigDecimal inc = (v == null || v.getIncrementoPrecio() == null) ? BigDecimal.ZERO : v.getIncrementoPrecio();
        BigDecimal unit = precioBase.add(inc);
        BigDecimal sub = unit.multiply(BigDecimal.valueOf(cantidad));

        CotizacionItem it = new CotizacionItem();
        it.setCotizacion(c);
        it.setMueble(m);
        it.setVariante(v);
        it.setCantidad(cantidad);
        it.setPrecioUnitarioAplicado(unit);
        it.setSubtotal(sub);

        c.getItems().add(it);
        recalcularTotalInterno(c);
        return cotizacionRepositorio.save(c);
    }

    @Transactional
    public Cotizacion quitarItem(Long cotizacionId, Long itemId) {
        Cotizacion c = obtener(cotizacionId);
        if (Boolean.TRUE.equals(c.getConfirmada())) throw new RuntimeException("La cotización ya está confirmada y no se puede modificar.");

        Iterator<CotizacionItem> it = c.getItems().iterator();
        boolean removed = false;
        while (it.hasNext()) {
            CotizacionItem ci = it.next();
            if (ci.getId() != null && ci.getId().equals(itemId)) {
                it.remove();
                removed = true;
                break;
            }
        }

        if (!removed) {
            boolean found = c.getItems().removeIf(item -> item.getId() != null && item.getId().equals(itemId));
            if (!found) {
                throw new RuntimeException("Item no encontrado ("+itemId+") en la cotización");
            }
        }

        recalcularTotalInterno(c);
        return cotizacionRepositorio.save(c);
    }

    @Transactional
    public Cotizacion recalcular(Long cotizacionId) {
        Cotizacion c = obtener(cotizacionId);
        recalcularTotalInterno(c);
        return cotizacionRepositorio.save(c);
    }

    private void recalcularTotalInterno(Cotizacion c) {
        BigDecimal total = BigDecimal.ZERO;
        for (CotizacionItem it : c.getItems()) {
            BigDecimal sub = it.getSubtotal() == null ? BigDecimal.ZERO : it.getSubtotal();
            total = total.add(sub);
        }
        c.setTotal(total);
        if (c.getFechaCreacion() == null) c.setFechaCreacion(LocalDateTime.now());
        if (c.getConfirmada() == null) c.setConfirmada(Boolean.FALSE);
    }
}