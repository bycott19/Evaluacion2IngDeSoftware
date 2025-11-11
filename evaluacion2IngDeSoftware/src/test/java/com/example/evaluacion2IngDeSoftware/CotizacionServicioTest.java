package com.example.evaluacion2IngDeSoftware;

import com.example.evaluacion2IngDeSoftware.Modelo.*;
import com.example.evaluacion2IngDeSoftware.Repositorio.MuebleRepositorio;
import com.example.evaluacion2IngDeSoftware.Repositorio.VarianteRepositorio;
import com.example.evaluacion2IngDeSoftware.Servicios.CotizacionServicio;
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
class CotizacionServicioTest {

    @Autowired CotizacionServicio cotSrv;
    @Autowired MuebleRepositorio muebleRepo;
    @Autowired VarianteRepositorio varianteRepo;

    private Mueble mueble;
    private Variante variante;

    private static void assertBdEq(String expected, BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, actual.compareTo(new BigDecimal(expected)));
    }

    @BeforeEach
    void setUp() {
        mueble = new Mueble();
        mueble.setNombre("Silla Test");
        mueble.setTipo(TipoMueble.SILLA);
        mueble.setPrecioBase(new BigDecimal("10000"));
        mueble.setStock(10);
        mueble.setEstado(EstadoMueble.ACTIVO);
        mueble.setTamano(Tamano.MEDIANO);
        mueble.setMaterial("Pino");
        mueble = muebleRepo.save(mueble);

        variante = new Variante();
        variante.setNombre("PREMIUM");
        variante.setIncrementoPrecio(new BigDecimal("1500"));
        variante = varianteRepo.save(variante);
    }

    @Test
    void crearCotizacion_ok() {
        Cotizacion c = cotSrv.crear();
        assertNotNull(c.getId());
        assertFalse(Boolean.TRUE.equals(c.getConfirmada()));
        assertNotNull(c.getTotal());
    }

    @Test
    void agregarItem_y_recalcular_total_conVariante_ok() {
        Cotizacion c = cotSrv.crear();
        c = cotSrv.agregarItem(c.getId(), mueble.getId(), variante.getId(), 2);
        // (10000 + 1500) * 2 = 23000
        assertBdEq("23000.00", c.getTotal());
    }

    @Test
    void agregarItem_y_recalcular_total_sinVariante_ok() {
        Cotizacion c = cotSrv.crear();
        c = cotSrv.agregarItem(c.getId(), mueble.getId(), null, 3);
        // 10000 * 3 = 30000
        assertBdEq("30000.00", c.getTotal());
    }

    @Test
    void agregarItem_cantidadInvalida_lanza() {
        Cotizacion c = cotSrv.crear();
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cotSrv.agregarItem(c.getId(), mueble.getId(), null, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("cantidad"));
    }

    @Test
    void confirmar_sin_items_lanza() {
        Cotizacion c = cotSrv.crear();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> cotSrv.confirmar(c.getId()));
        assertTrue(ex.getMessage().toLowerCase().contains("no tiene items"));
    }

    @Test
    void confirmar_con_items_ok() {
        Cotizacion c = cotSrv.crear();
        cotSrv.agregarItem(c.getId(), mueble.getId(), null, 1);
        c = cotSrv.confirmar(c.getId());
        assertTrue(Boolean.TRUE.equals(c.getConfirmada()));
    }
}
