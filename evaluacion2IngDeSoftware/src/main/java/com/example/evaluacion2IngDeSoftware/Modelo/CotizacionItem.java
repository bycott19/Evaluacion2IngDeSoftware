package com.example.evaluacion2IngDeSoftware.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cotizacion_items")
public class CotizacionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cotizacion_id")
    private Cotizacion cotizacion;

    @ManyToOne
    @JoinColumn(name = "mueble_id")
    private Mueble mueble;

    @ManyToOne
    @JoinColumn(name = "variante_id")
    private Variante variante;

    private Integer cantidad;

    @Column(name = "precio_unitario_aplicado")
    private BigDecimal precioUnitarioAplicado;

    private BigDecimal subtotal;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cotizacion getCotizacion() { return cotizacion; }
    public void setCotizacion(Cotizacion cotizacion) { this.cotizacion = cotizacion; }

    public Mueble getMueble() { return mueble; }
    public void setMueble(Mueble mueble) { this.mueble = mueble; }

    public Variante getVariante() { return variante; }
    public void setVariante(Variante variante) { this.variante = variante; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitarioAplicado() { return precioUnitarioAplicado; }
    public void setPrecioUnitarioAplicado(BigDecimal precioUnitarioAplicado) { this.precioUnitarioAplicado = precioUnitarioAplicado; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
