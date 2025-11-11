package com.example.evaluacion2IngDeSoftware.Modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "variantes")
public class Variante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Column(name = "incremento_precio")
    private BigDecimal incrementoPrecio;

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getIncrementoPrecio() { return incrementoPrecio; }
    public void setIncrementoPrecio(BigDecimal incrementoPrecio) { this.incrementoPrecio = incrementoPrecio; }
}


