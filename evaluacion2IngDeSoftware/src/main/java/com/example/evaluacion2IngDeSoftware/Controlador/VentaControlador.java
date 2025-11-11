package com.example.evaluacion2IngDeSoftware.Controlador;

import com.example.evaluacion2IngDeSoftware.Modelo.Venta;
import com.example.evaluacion2IngDeSoftware.Repositorio.VentaRepositorio;
import com.example.evaluacion2IngDeSoftware.Servicios.VentaServicio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaControlador {

    private final VentaServicio servicio;
    private final VentaRepositorio repo;

    public VentaControlador(VentaServicio servicio, VentaRepositorio repo) {
        this.servicio = servicio;
        this.repo = repo;
    }

    @PostMapping("/confirmar")
    @ResponseStatus(HttpStatus.CREATED)
    public Venta confirmar(@RequestParam Long cotizacionId) {
        return servicio.confirmarVentaDesdeCotizacion(cotizacionId);
    }

    @GetMapping
    public List<Venta> listar() {
        return repo.findAll();
    }
}

