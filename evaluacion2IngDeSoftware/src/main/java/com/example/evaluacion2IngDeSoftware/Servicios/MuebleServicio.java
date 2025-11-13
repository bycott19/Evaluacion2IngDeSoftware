package com.example.evaluacion2IngDeSoftware.Servicios;

import com.example.evaluacion2IngDeSoftware.Modelo.EstadoMueble;
import com.example.evaluacion2IngDeSoftware.Modelo.Mueble;
import com.example.evaluacion2IngDeSoftware.Repositorio.MuebleRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MuebleServicio {

    @Autowired
    private MuebleRepositorio muebleRepositorio;

    @Transactional
    public Mueble crear(Mueble m) {
        if (m.getEstado() == null) {
            m.setEstado(EstadoMueble.ACTIVO);
        }
        m.setId(null);
        return muebleRepositorio.save(m);
    }

    @Transactional(readOnly = true)
    public List<Mueble> listar() {
        return muebleRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Mueble> listarActivos() {
        return muebleRepositorio.findByEstado(EstadoMueble.ACTIVO);
    }

    @Transactional(readOnly = true)
    public Mueble buscarPorId(Long id) {
        Mueble m = muebleRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("El Mueble con id " + id + " no existe"));

        m.getVariantes().size();

        return m;
    }

    @Transactional
    public Mueble actualizar(Long id, Mueble datos) {
        Mueble existente = buscarPorId(id);

        existente.setNombre(datos.getNombre());
        existente.setTipo(datos.getTipo());
        existente.setTamano(datos.getTamano());
        existente.setPrecioBase(datos.getPrecioBase());
        existente.setStock(datos.getStock());
        existente.setMaterial(datos.getMaterial());

        if (datos.getEstado() != null) {
            existente.setEstado(datos.getEstado());
        }

        return muebleRepositorio.save(existente);
    }

    @Transactional
    public Mueble desactivar(Long id) {
        Mueble m = buscarPorId(id);
        if (m.getEstado() != EstadoMueble.INACTIVO) {
            m.setEstado(EstadoMueble.INACTIVO);
            m = muebleRepositorio.save(m);
        }
        return m;
    }

    @Transactional
    public Mueble activar(Long id) {
        Mueble m = buscarPorId(id);
        if (m.getEstado() != EstadoMueble.ACTIVO) {
            m.setEstado(EstadoMueble.ACTIVO);
            m = muebleRepositorio.save(m);
        }
        return m;
    }
}