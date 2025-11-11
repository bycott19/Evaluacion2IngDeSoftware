package com.example.evaluacion2IngDeSoftware.Controlador;

import com.example.evaluacion2IngDeSoftware.Modelo.Mueble;
import com.example.evaluacion2IngDeSoftware.Servicios.MuebleServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/muebles")
@CrossOrigin
public class MuebleControlador {

    private final MuebleServicio muebleSrv;

    public MuebleControlador(MuebleServicio muebleSrv) {
        this.muebleSrv = muebleSrv;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Mueble> crear(@RequestBody Mueble mueble) {
        Mueble creado = muebleSrv.crear(mueble);
        return ResponseEntity.created(URI.create("/api/muebles/" + creado.getId())).body(creado);
    }

    // READ ALL
    @GetMapping
    public List<Mueble> listar() {
        return muebleSrv.listar();
    }

    // READ ONE
    @GetMapping("/{id}")
    public Mueble obtener(@PathVariable Long id) {
        return muebleSrv.buscarPorId(id);
    }

    // UPDATE (PUT completo)
    @PutMapping("/{id}")
    public Mueble actualizar(@PathVariable Long id, @RequestBody Mueble datos) {
        return muebleSrv.actualizar(id, datos);
    }

    // SOFT DELETE: DESACTIVAR
    @PatchMapping("/{id}/desactivar")
    public Mueble desactivar(@PathVariable Long id) {
        return muebleSrv.desactivar(id);
    }

    // REACTIVAR
    @PatchMapping("/{id}/activar")
    public Mueble activar(@PathVariable Long id) {
        return muebleSrv.activar(id);
    }
}
