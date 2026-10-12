package edu.itson.cu6.model;

import java.math.BigDecimal;

/** Producto del catálogo local. El inventario sólo lo modifica CompraModel. */
public final class Producto {
    private final String id;
    private final String nombre;
    private final BigDecimal costo;
    private int stock;

    public Producto(String id, String nombre, BigDecimal costo, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.costo = costo;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getCosto() { return costo; }
    public int getStock() { return stock; }

    void descontarStock(int cantidad) {
        if (cantidad <= 0 || cantidad > stock) {
            throw new IllegalArgumentException("La cantidad excede el inventario disponible.");
        }
        stock -= cantidad;
    }
}
