package com.example.evaluacion2IngDeSoftware;

import com.example.evaluacion2IngDeSoftware.Modelo.*;
import com.example.evaluacion2IngDeSoftware.Repositorio.MuebleRepositorio;
import com.example.evaluacion2IngDeSoftware.Servicios.CotizacionServicio;
import com.example.evaluacion2IngDeSoftware.Servicios.VentaServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class VentaServicioTest {

    @Autowired CotizacionServicio cotizacionServicio;
    @Autowired VentaServicio ventaServicio;
    @Autowired MuebleRepositorio muebleRepositorio;

    private Mueble mueble;

    private static void assertBdEq(String expected, BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, actual.compareTo(new BigDecimal(expected)));
    }

    @BeforeEach
    void setUp() {
        mueble = new Mueble();
        mueble.setNombre("Mesa Test");
        mueble.setTipo(TipoMueble.MESA);
        mueble.setPrecioBase(new BigDecimal("20000"));
        mueble.setStock(5);
        mueble.setEstado(EstadoMueble.ACTIVO);
        mueble.setTamano(Tamano.GRANDE);
        mueble.setMaterial("Roble");
        mueble = muebleRepositorio.save(mueble);
    }

    @Test
    void testVentaOkDescuentaStock() {
        Cotizacion c = cotizacionServicio.crear();
        cotizacionServicio.agregarItem(c.getId(), mueble.getId(), null, 2);
        Venta v = ventaServicio.confirmarVentaDesdeCotizacion(c.getId());
        assertNotNull(v.getId());
        assertBdEq("40000.00", v.getTotal());

        Mueble actualizado = muebleRepositorio.findById(mueble.getId()).orElseThrow();
        assertEquals(3, actualizado.getStock());

        Cotizacion cActualizada = cotizacionServicio.obtener(c.getId());
        assertTrue(cActualizada.getConfirmada());
    }

    @Test
    void testErrorVentaVacia() {
        Cotizacion c = cotizacionServicio.crear();
        final Long cotId = c.getId();
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ventaServicio.confirmarVentaDesdeCotizacion(cotId));

        assertTrue(ex.getMessage().toLowerCase().contains("no tiene items"));
    }


    @Test
    void testErrorStockInsuficiente() {
        Cotizacion c = cotizacionServicio.crear();
        cotizacionServicio.agregarItem(c.getId(), mueble.getId(), null, 10);

        final Long cotId = c.getId();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ventaServicio.confirmarVentaDesdeCotizacion(cotId));

        String norm = ex.getMessage().toLowerCase();
        assertTrue(norm.contains("stock"), "Se esperaba error relacionado a stock insuficiente. Mensaje: " + ex.getMessage());
    }
}