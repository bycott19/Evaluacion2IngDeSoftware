package com.example.evaluacion2IngDeSoftware;

import com.example.evaluacion2IngDeSoftware.Modelo.EstadoMueble;
import com.example.evaluacion2IngDeSoftware.Modelo.Mueble;
import com.example.evaluacion2IngDeSoftware.Modelo.Tamano;
import com.example.evaluacion2IngDeSoftware.Modelo.TipoMueble;
import com.example.evaluacion2IngDeSoftware.Repositorio.MuebleRepositorio;
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
class CatalogoCrudTest {

    @Autowired
    MuebleRepositorio muebleRepositorio;

    @Test
    void crudMueble() {
        Mueble m = new Mueble();
        m.setNombre("Mesa Pino");
        m.setTipo(TipoMueble.MESA);
        m.setTamano(Tamano.MEDIANO);
        m.setMaterial("Pino");
        m.setEstado(EstadoMueble.ACTIVO);
        m.setStock(7);
        m.setPrecioBase(new BigDecimal("45000"));
        m = muebleRepositorio.save(m);

        Long id = m.getId();
        assertNotNull(id, "Debe generar id al crear");

        Mueble obtenido = muebleRepositorio.findById(id).orElseThrow();
        assertEquals("Mesa Pino", obtenido.getNombre());
        assertEquals(EstadoMueble.ACTIVO, obtenido.getEstado());

        obtenido.setPrecioBase(new BigDecimal("47000"));
        obtenido.setStock(9);
        muebleRepositorio.save(obtenido);

        Mueble actualizado = muebleRepositorio.findById(id).orElseThrow();
        assertEquals(0, actualizado.getPrecioBase().compareTo(new BigDecimal("47000")));
        assertEquals(9, actualizado.getStock());

        actualizado.setEstado(EstadoMueble.INACTIVO);
        muebleRepositorio.save(actualizado);
        Mueble inactivo = muebleRepositorio.findById(id).orElseThrow();
        assertEquals(EstadoMueble.INACTIVO, inactivo.getEstado(), "Debe quedar INACTIVO al borrar");

        inactivo.setEstado(EstadoMueble.ACTIVO);
        muebleRepositorio.save(inactivo);
        Mueble reactivado = muebleRepositorio.findById(id).orElseThrow();
        assertEquals(EstadoMueble.ACTIVO, reactivado.getEstado(), "Debe volver a ACTIVO al restaurar");
    }
}
