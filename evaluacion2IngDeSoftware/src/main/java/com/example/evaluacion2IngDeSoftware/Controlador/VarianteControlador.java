package com.example.evaluacion2IngDeSoftware.Controlador;

import com.example.evaluacion2IngDeSoftware.Modelo.Variante;
import com.example.evaluacion2IngDeSoftware.Servicios.VarianteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variantes")
public class VarianteControlador {

    @Autowired
    private VarianteServicio servicio;

    @GetMapping
    public List<Variante> listar() { return servicio.listar(); }

    @GetMapping("/{id}")
    public Variante obtener(@PathVariable Long id) { return servicio.obtener(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Variante crear(@RequestBody Variante v) { return servicio.crear(v); }

    @PutMapping("/{id}")
    public Variante editar(@PathVariable Long id, @RequestBody Variante v) { return servicio.editar(id, v); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { servicio.eliminar(id); }
}