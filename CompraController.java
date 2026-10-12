package edu.itson.cu6.controller;

import edu.itson.cu6.model.CompraModel;
import edu.itson.cu6.model.Tarjeta;
import edu.itson.cu6.model.Ticket;
import edu.itson.cu6.view.CompraView;

/** Coordina las acciones del cliente y presenta resultados o errores. */
public final class CompraController {
    private final CompraModel model;
    private final CompraView view;

    public CompraController(CompraModel model, CompraView view) {
        this.model = model;
        this.view = view;
    }

    public void iniciar() {
        view.mostrarProductos(model.listarProductos());
        actualizarCarrito();
        view.bloquearCompra(false);
        view.mostrarEstado("Selecciona un producto y su cantidad para comenzar.");
    }

    public void agregarProducto(String id, int cantidad) {
        try {
            model.agregarProducto(id, cantidad);
            actualizarCarrito();
            view.mostrarEstado("Producto agregado. Revisa tu selección en la lista de la derecha.");
        } catch (IllegalArgumentException | IllegalStateException error) {
            view.mostrarError(error.getMessage());
        }
    }

    public void retirarProducto(String id) {
        try {
            model.retirarProducto(id);
            actualizarCarrito();
            view.mostrarEstado("Producto retirado del carrito.");
        } catch (IllegalArgumentException | IllegalStateException error) {
            view.mostrarError(error.getMessage());
        }
    }

    public void consultarTarjeta(String numero) {
        try {
            Tarjeta tarjeta = model.consultarTarjeta(numero);
            view.mostrarTarjeta(tarjeta);
            view.mostrarEstado("Tarjeta ficticia encontrada. Puedes confirmar el pago simulado.");
        } catch (IllegalArgumentException error) {
            view.mostrarTarjeta(null);
            view.mostrarError(error.getMessage());
        }
    }

    public void pagar(String numero) {
        try {
            Ticket ticket = model.pagar(numero);
            view.mostrarTarjeta(ticket.getTarjeta());
            view.mostrarTicket(ticket);
            view.mostrarProductos(model.listarProductos());
            view.bloquearCompra(true);
            view.mostrarEstado("Compra completada. El ticket aparece debajo de los datos de tarjeta.");
        } catch (IllegalArgumentException | IllegalStateException error) {
            view.mostrarError(error.getMessage());
        }
    }

    public void nuevaCompra() {
        model.reiniciarCompra();
        view.limpiarPago();
        iniciar();
    }

    private void actualizarCarrito() {
        view.mostrarCarrito(model.getCarrito(), model.getTotal());
    }
}
