package com.example.evaluacion2IngDeSoftware.Modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "muebles")
public class Mueble {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Enumerated(EnumType.STRING)
    private TipoMueble tipo;

    @Column(name = "precio_base")
    private BigDecimal precioBase;

    private Integer stock;
    @Enumerated(EnumType.STRING)
    private EstadoMueble estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tamano")
    private Tamano tamano;

    private String material;

    @ManyToMany
    @JoinTable(name = "mueble_variantes",
            joinColumns = @JoinColumn(name = "mueble_id"),
            inverseJoinColumns = @JoinColumn(name = "variante_id"))
    private Set<Variante> variantes = new LinkedHashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoMueble getTipo() { return tipo; }
    public void setTipo(TipoMueble tipo) { this.tipo = tipo; }
    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public EstadoMueble getEstado() { return estado; }
    public void setEstado(EstadoMueble estado) { this.estado = estado; }
    public Tamano getTamano() { return tamano; }
    public void setTamano(Tamano tamano) { this.tamano = tamano; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public Set<Variante> getVariantes() { return variantes; }
    public void setVariantes(Set<Variante> variantes) { this.variantes = variantes; }
}
