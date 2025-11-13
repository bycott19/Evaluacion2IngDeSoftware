package com.example.evaluacion2IngDeSoftware.Servicios;

import com.example.evaluacion2IngDeSoftware.Modelo.Variante;
import com.example.evaluacion2IngDeSoftware.Repositorio.VarianteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VarianteServicio {

    @Autowired
    private VarianteRepositorio varianteRepositorio;

    public List<Variante> listar() {
        return varianteRepositorio.findAll();
    }

    public Variante obtener(Long id) {
        return varianteRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Variante no encontrada: " + id));
    }

    @Transactional
    public Variante crear(Variante v) {
        if (v.getNombre() == null || v.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la variante no puede estar vacío.");
        }

        if (varianteRepositorio.existsByNombreIgnoreCase(v.getNombre().trim())) {
            throw new RuntimeException("Ya existe una variante con el nombre: '" + v.getNombre() + "'");
        }

        if (v.getIncrementoPrecio() == null) {
            v.setIncrementoPrecio(BigDecimal.ZERO);
        }
        return varianteRepositorio.save(v);
    }

    @Transactional
    public Variante editar(Long id, Variante data) {
        Variante db = obtener(id);
        db.setNombre(data.getNombre());

        db.setIncrementoPrecio(data.getIncrementoPrecio() == null ? BigDecimal.ZERO : data.getIncrementoPrecio());

        return varianteRepositorio.save(db);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!varianteRepositorio.existsById(id)) throw new RuntimeException("Variante no encontrada: " + id);
        varianteRepositorio.deleteById(id);
    }
}