package edu.itson.cu6.model;

import java.math.BigDecimal;

/** Cantidad seleccionada y precio unitario congelado para la compra. */
public final class LineaCompra {
    private final Producto producto;
    private final int cantidad;
    private final BigDecimal costoUnitario;

    public LineaCompra(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.costoUnitario = producto.getCosto();
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public BigDecimal getSubtotal() {
        return costoUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
