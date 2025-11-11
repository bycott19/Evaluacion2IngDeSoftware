package com.example.evaluacion2IngDeSoftware.Servicios;

import com.example.evaluacion2IngDeSoftware.Modelo.Variante;
import com.example.evaluacion2IngDeSoftware.Repositorio.VarianteRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VarianteServicio {

    private final VarianteRepositorio repo;

    public VarianteServicio(VarianteRepositorio repo) {
        this.repo = repo;
    }

    public List<Variante> listar() {
        return repo.findAll();
    }

    public Variante obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Variante no encontrada: " + id));
    }

    @Transactional
    public Variante crear(Variante v) {
        normalizar(v);
        return repo.save(v);
    }

    @Transactional
    public Variante editar(Long id, Variante data) {
        Variante db = obtener(id);
        db.setNombre(data.getNombre());
        db.setIncrementoPrecio(safe(data.getIncrementoPrecio()));
        return repo.save(db);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repo.existsById(id)) throw new RuntimeException("Variante no encontrada: " + id);
        repo.deleteById(id);
    }

    private void normalizar(Variante v) {
        if (v.getIncrementoPrecio() == null) v.setIncrementoPrecio(BigDecimal.ZERO);
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
