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
    MuebleRepositorio muebleRepo;

    @Test
    void crudBasicoMueble_borradoLogico() {
        // CREATE
        Mueble m = new Mueble();
        m.setNombre("Mesa Pino");
        m.setTipo(TipoMueble.MESA);
        m.setTamano(Tamano.MEDIANO);
        m.setMaterial("Pino");
        m.setEstado(EstadoMueble.ACTIVO);
        m.setStock(7);
        m.setPrecioBase(new BigDecimal("45000"));
        m = muebleRepo.save(m);

        Long id = m.getId();
        assertNotNull(id, "Debe generar id al crear");

        // READ
        Mueble obtenido = muebleRepo.findById(id).orElseThrow();
        assertEquals("Mesa Pino", obtenido.getNombre());
        assertEquals(EstadoMueble.ACTIVO, obtenido.getEstado());

        // UPDATE (precio y stock)
        obtenido.setPrecioBase(new BigDecimal("47000"));
        obtenido.setStock(9);
        muebleRepo.save(obtenido);

        Mueble actualizado = muebleRepo.findById(id).orElseThrow();
        assertEquals(0, actualizado.getPrecioBase().compareTo(new BigDecimal("47000")));
        assertEquals(9, actualizado.getStock());

        // "DELETE" LÓGICO: desactivar
        actualizado.setEstado(EstadoMueble.INACTIVO);
        muebleRepo.save(actualizado);
        Mueble inactivo = muebleRepo.findById(id).orElseThrow();
        assertEquals(EstadoMueble.INACTIVO, inactivo.getEstado(), "Debe quedar INACTIVO al 'borrar'");

        // "UNDELETE": activar nuevamente
        inactivo.setEstado(EstadoMueble.ACTIVO);
        muebleRepo.save(inactivo);
        Mueble reactivado = muebleRepo.findById(id).orElseThrow();
        assertEquals(EstadoMueble.ACTIVO, reactivado.getEstado(), "Debe volver a ACTIVO al 'restaurar'");
    }
}
