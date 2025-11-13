package com.example.evaluacion2IngDeSoftware.Controlador;

import com.example.evaluacion2IngDeSoftware.Modelo.Mueble;
import com.example.evaluacion2IngDeSoftware.Servicios.MuebleServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/muebles")
@CrossOrigin
public class MuebleControlador {
    @Autowired
    private MuebleServicio muebleServicio;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mueble crear(@RequestBody Mueble mueble) {
        Mueble creado = muebleServicio.crear(mueble);
        return creado;
    }

    @GetMapping
    public List<Mueble> listar() {
        return muebleServicio.listar();
    }

    @GetMapping("/{id}")
    public Mueble obtener(@PathVariable Long id) {
        return muebleServicio.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Mueble actualizar(@PathVariable Long id, @RequestBody Mueble datos) {
        return muebleServicio.actualizar(id, datos);
    }

    @PatchMapping("/{id}/desactivar")
    public Mueble desactivar(@PathVariable Long id) {
        return muebleServicio.desactivar(id);
    }

    @PatchMapping("/{id}/activar")
    public Mueble activar(@PathVariable Long id) {
        return muebleServicio.activar(id);
    }
}