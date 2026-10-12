package edu.itson.cu6.view;

import edu.itson.cu6.model.LineaCompra;
import edu.itson.cu6.model.Producto;
import edu.itson.cu6.model.Tarjeta;
import edu.itson.cu6.model.Ticket;
import java.math.BigDecimal;
import java.util.List;

/** Contrato de presentación usado por el controlador sin depender de Swing. */
public interface CompraView {
    void mostrarProductos(List<Producto> productos);
    void mostrarCarrito(List<LineaCompra> lineas, BigDecimal total);
    void mostrarTarjeta(Tarjeta tarjeta);
    void mostrarTicket(Ticket ticket);
    void mostrarEstado(String mensaje);
    void mostrarError(String mensaje);
    void bloquearCompra(boolean bloqueada);
    void limpiarPago();
}
