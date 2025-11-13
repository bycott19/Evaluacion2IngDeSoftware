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

    @Autowired CotizacionServicio cotizacionServicio;
    @Autowired MuebleRepositorio muebleRepositorio;
    @Autowired VarianteRepositorio varianteRepositorio;

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
        mueble = muebleRepositorio.save(mueble);

        variante = new Variante();
        variante.setNombre("PREMIUM");
        variante.setIncrementoPrecio(new BigDecimal("1500"));
        variante = varianteRepositorio.save(variante);
    }

    @Test
    void crearCotizacionTest() {
        Cotizacion c = cotizacionServicio.crear();
        assertNotNull(c.getId());
        assertFalse(Boolean.TRUE.equals(c.getConfirmada()));
        assertNotNull(c.getTotal());
    }

    @Test
    void TestAgregarItemConVariante() {
        Cotizacion c = cotizacionServicio.crear();
        c = cotizacionServicio.agregarItem(c.getId(), mueble.getId(), variante.getId(), 2);
        assertBdEq("23000.00", c.getTotal());
    }

    @Test
    void testAgregarItemSinVariante() {
        Cotizacion c = cotizacionServicio.crear();
        c = cotizacionServicio.agregarItem(c.getId(), mueble.getId(), null, 3);
        assertBdEq("30000.00", c.getTotal());
    }

    @Test
    void testErrorCantidadCero() {
        Cotizacion c = cotizacionServicio.crear();
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cotizacionServicio.agregarItem(c.getId(), mueble.getId(), null, 0));
        assertTrue(ex.getMessage().toLowerCase().contains("cantidad"));
    }
}