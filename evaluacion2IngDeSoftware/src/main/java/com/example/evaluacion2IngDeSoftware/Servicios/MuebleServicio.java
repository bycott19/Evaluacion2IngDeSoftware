package com.example.evaluacion2IngDeSoftware.Servicios;

import com.example.evaluacion2IngDeSoftware.Modelo.EstadoMueble;
import com.example.evaluacion2IngDeSoftware.Modelo.Mueble;
import com.example.evaluacion2IngDeSoftware.Repositorio.MuebleRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MuebleServicio {

    private final MuebleRepositorio muebleRepo;

    public MuebleServicio(MuebleRepositorio muebleRepo) {
        this.muebleRepo = muebleRepo;
    }

    // Crear
    public Mueble crear(Mueble m) {
        // por claridad, si no viene estado lo dejamos ACTIVO
        if (m.getEstado() == null) {
            m.setEstado(EstadoMueble.ACTIVO);
        }
        // evitar que el cliente “imponga” IDs
        m.setId(null);
        return muebleRepo.save(m);
    }

    // Leer todos
    public List<Mueble> listar() {
        return muebleRepo.findAll();
    }

    // Leer uno
    public Mueble buscarPorId(Long id) {
        return muebleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Mueble id " + id + " no existe"));
    }

    // Actualizar (PUT completo)
    public Mueble actualizar(Long id, Mueble datos) {
        Mueble existente = buscarPorId(id);

        // Actualizamos campos básicos; si prefieres “parcial”, avísame y lo paso a PATCH-like
        existente.setNombre(datos.getNombre());
        existente.setTipo(datos.getTipo());
        existente.setTamano(datos.getTamano());
        existente.setPrecioBase(datos.getPrecioBase());
        existente.setStock(datos.getStock());
        existente.setMaterial(datos.getMaterial());
        // el estado normalmente se maneja con activar/desactivar, pero si lo envían lo respetamos
        if (datos.getEstado() != null) {
            existente.setEstado(datos.getEstado());
        }

        return muebleRepo.save(existente);
    }

    // Desactivar (soft delete)
    public Mueble desactivar(Long id) {
        Mueble m = buscarPorId(id);
        if (m.getEstado() != EstadoMueble.INACTIVO) {
            m.setEstado(EstadoMueble.INACTIVO);
            m = muebleRepo.save(m);
        }
        return m;
    }

    // Activar
    public Mueble activar(Long id) {
        Mueble m = buscarPorId(id);
        if (m.getEstado() != EstadoMueble.ACTIVO) {
            m.setEstado(EstadoMueble.ACTIVO);
            m = muebleRepo.save(m);
        }
        return m;
    }
}
