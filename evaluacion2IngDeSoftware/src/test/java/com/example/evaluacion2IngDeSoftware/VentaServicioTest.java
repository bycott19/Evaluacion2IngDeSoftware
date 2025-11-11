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
import java.text.Normalizer;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class VentaServicioTest {

    @Autowired CotizacionServicio cotSrv;
    @Autowired VentaServicio ventaSrv;
    @Autowired MuebleRepositorio muebleRepo;

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
        mueble = muebleRepo.save(mueble);
    }

    @Test
    void confirmarVenta_descuentaStock_ok() {
        Cotizacion c = cotSrv.crear();
        cotSrv.agregarItem(c.getId(), mueble.getId(), null, 2);
        c = cotSrv.confirmar(c.getId());

        Venta v = ventaSrv.confirmarVentaDesdeCotizacion(c.getId());

        assertNotNull(v.getId());
        assertBdEq("40000.00", v.getTotal()); // 20000 * 2

        Mueble actualizado = muebleRepo.findById(mueble.getId()).orElseThrow();
        assertEquals(3, actualizado.getStock()); // 5 - 2
    }

    @Test
    void confirmarVenta_sinConfirmarCotizacion_lanza() {
        Cotizacion c = cotSrv.crear();
        cotSrv.agregarItem(c.getId(), mueble.getId(), null, 1);

        // usar id "efectivamente final" para la lambda
        final Long cotId = c.getId();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ventaSrv.confirmarVentaDesdeCotizacion(cotId));

        // normalizamos para no depender de acentos/variantes del mensaje
        String norm = Normalizer.normalize(ex.getMessage(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();

        assertTrue(norm.contains("confirm"), "Se esperaba un mensaje pidiendo confirmar la cotizacion. Mensaje: " + ex.getMessage());
    }

    @Test
    void ventaConStockInsuficiente_lanza() {
        // stock 5, intentamos vender 10
        Cotizacion c = cotSrv.crear();
        cotSrv.agregarItem(c.getId(), mueble.getId(), null, 10);
        c = cotSrv.confirmar(c.getId());

        final Long cotId = c.getId();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ventaSrv.confirmarVentaDesdeCotizacion(cotId));

        String norm = Normalizer.normalize(ex.getMessage(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
        assertTrue(norm.contains("stock"), "Se esperaba error relacionado a stock insuficiente. Mensaje: " + ex.getMessage());
    }
}
