package com.example.evaluacion2IngDeSoftware.Servicios;

import com.example.evaluacion2IngDeSoftware.Modelo.*;
import com.example.evaluacion2IngDeSoftware.Repositorio.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;

@Service
public class CotizacionServicio {

    private final CotizacionRepositorio cotRepo;
    private final CotizacionItemRepositorio itemRepo;
    private final MuebleRepositorio muebleRepo;
    private final VarianteRepositorio varianteRepo;

    public CotizacionServicio(CotizacionRepositorio cotRepo,
                              CotizacionItemRepositorio itemRepo,
                              MuebleRepositorio muebleRepo,
                              VarianteRepositorio varianteRepo) {
        this.cotRepo = cotRepo;
        this.itemRepo = itemRepo;
        this.muebleRepo = muebleRepo;
        this.varianteRepo = varianteRepo;
    }

    @Transactional
    public Cotizacion crear() {
        Cotizacion c = new Cotizacion();
        c.setFechaCreacion(LocalDateTime.now());
        c.setConfirmada(Boolean.FALSE);
        c.setTotal(BigDecimal.ZERO);
        return cotRepo.save(c);
    }

    public Cotizacion obtener(Long id) {
        return cotRepo.findById(id).orElseThrow(() -> new RuntimeException("Cotización no encontrada: " + id));
    }

    @Transactional
    public Cotizacion agregarItem(Long cotizacionId, Long muebleId, Long varianteId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) throw new RuntimeException("Cantidad inválida");

        Cotizacion c = obtener(cotizacionId);
        if (Boolean.TRUE.equals(c.getConfirmada())) throw new RuntimeException("La cotización ya está confirmada");

        Mueble m = muebleRepo.findById(muebleId)
                .orElseThrow(() -> new RuntimeException("Mueble no encontrado: " + muebleId));

        Variante v = null;
        if (varianteId != null) {
            v = varianteRepo.findById(varianteId)
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
        return cotRepo.save(c);
    }

    @Transactional
    public Cotizacion quitarItem(Long cotizacionId, Long itemId) {
        Cotizacion c = obtener(cotizacionId);
        if (Boolean.TRUE.equals(c.getConfirmada())) throw new RuntimeException("La cotización ya está confirmada");

        Iterator<CotizacionItem> it = c.getItems().iterator();
        boolean removed = false;
        while (it.hasNext()) {
            CotizacionItem ci = it.next();
            if (ci.getId() != null && ci.getId().equals(itemId)) {
                it.remove();
                itemRepo.deleteById(itemId);
                removed = true;
                break;
            }
        }
        if (!removed) throw new RuntimeException("Item no encontrado en la cotización");
        recalcularTotalInterno(c);
        return cotRepo.save(c);
    }

    @Transactional
    public Cotizacion recalcular(Long cotizacionId) {
        Cotizacion c = obtener(cotizacionId);
        recalcularTotalInterno(c);
        return cotRepo.save(c);
    }

    @Transactional
    public Cotizacion confirmar(Long cotizacionId) {
        Cotizacion c = obtener(cotizacionId);
        if (c.getItems().isEmpty()) throw new RuntimeException("La cotización no tiene items");
        c.setConfirmada(Boolean.TRUE);
        return cotRepo.save(c);
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
