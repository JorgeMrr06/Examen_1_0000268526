package edu.itson.cu6;

import edu.itson.cu6.controller.CompraController;
import edu.itson.cu6.model.*;
import edu.itson.cu6.view.CompraView;
import java.math.BigDecimal;
import java.util.List;

/** Pruebas autónomas de reglas de negocio y coordinación MVC; sin frameworks. */
public final class ModelTests {
    private static int checks;

    public static void main(String[] args) {
        CompraModel model = new CompraModel();
        check(model.listarProductos().size() == 5, "Catálogo: cinco productos con precios e inventario");
        check(model.getTotal().equals(new BigDecimal("0.00")), "Carrito inicial vacío y total exacto");
        rejects(() -> model.pagar("4111111111111111"), "No pagar carrito vacío");
        rejects(() -> model.agregarProducto(null, 1), "No agregar sin seleccionar producto");
        rejects(() -> model.agregarProducto("DESCONOCIDO", 1), "No aceptar producto inexistente");
        rejects(() -> model.agregarProducto("P01", 0), "No aceptar cantidad cero");
        rejects(() -> model.agregarProducto("P01", -1), "No aceptar cantidad negativa");
        rejects(() -> model.agregarProducto("P03", 6), "No exceder stock");
        model.agregarProducto("P01", 1); model.agregarProducto("P01", 1);
        check(model.getCarrito().size() == 1 && model.getCarrito().get(0).getCantidad() == 2, "Selección repetida acumula en una fila");
        rejects(() -> model.agregarProducto("P01", Integer.MAX_VALUE), "Acumulación no permite overflow ni exceder stock");
        check(model.getCarrito().get(0).getCantidad() == 2, "Error de stock conserva carrito previo");
        model.agregarProducto("P02", 1);
        check(model.getTotal().equals(new BigDecimal("249.90")), "Total decimal exacto: 2x45.00 +159.90 =249.90");
        Tarjeta card = model.consultarTarjeta("4111-1111 1111-1111");
        check(card.getBanco().equals("Banco del Valle") && card.getCiudad().equals("Hermosillo"), "Consulta recupera banco y ciudad; normaliza espacios/guiones");
        check(card.getNumeroEnmascarado().equals("•••• •••• •••• 1111"), "Número mostrado conserva sólo últimos cuatro dígitos");
        check(model.consultarTarjeta("5555555555554444").getCiudad().equals("Ciudad Obregón"), "Segunda tarjeta registrada");
        check(model.consultarTarjeta("4000000000000002").getCiudad().equals("Navojoa"), "Tercera tarjeta registrada");
        rejects(() -> model.consultarTarjeta(""), "Tarjeta vacía rechazada");
        rejects(() -> model.consultarTarjeta(null), "Tarjeta nula rechazada");
        rejects(() -> model.consultarTarjeta("411111111111111A"), "Caracteres no numéricos rechazados");
        rejects(() -> model.consultarTarjeta("4111111111111112"), "Control de Luhn detecta dígito incorrecto");
        rejects(() -> model.consultarTarjeta("6011111111111117"), "Tarjeta con Luhn válido no registrada rechazada");
        rejects(() -> model.pagar("4111111111111112"), "Pago no modifica compra con tarjeta inválida");
        check(!model.isPagada() && model.listarProductos().get(0).getStock() == 20 && model.getUltimoTicket() == null,
                "Pago fallido conserva stock y no genera ticket");
        Ticket ticket = model.pagar("4111111111111111");
        check(ticket.getFolio().equals("CU6-0001") && model.isPagada(), "Primer pago finaliza con folio único");
        check(ticket.getTotal().equals(new BigDecimal("249.90")) && ticket.getLineas().size() == 2,
                "Ticket conserva líneas, datos de tarjeta y total");
        check(ticket.getTarjeta() == card && ticket.getFecha() != null, "Ticket incluye tarjeta y fecha");
        check(model.listarProductos().get(0).getStock() == 18 && model.listarProductos().get(1).getStock() == 9,
                "Pago descuenta unidades una sola vez");
        rejects(() -> model.pagar("4111111111111111"), "Segundo pago bloqueado");
        rejects(() -> model.agregarProducto("P04", 1), "No agregar a compra finalizada");
        rejects(() -> model.retirarProducto("P01"), "No retirar de compra finalizada");
        check(model.listarProductos().get(0).getStock() == 18 && model.getUltimoTicket() == ticket,
                "Doble pago conserva inventario y ticket anterior");
        try { ticket.getLineas().clear(); throw new AssertionError("Ticket mutable"); }
        catch (UnsupportedOperationException expected) { check(true, "Lista de ticket inmutable"); }
        model.reiniciarCompra();
        check(!model.isPagada() && model.getCarrito().isEmpty() && model.getUltimoTicket() == null, "Nueva compra limpia la sesión de compra");
        check(ticket.getTotal().equals(new BigDecimal("249.90")) && ticket.getLineas().size() == 2, "Ticket previo permanece estable tras reiniciar");
        check(model.listarProductos().get(0).getStock() == 18, "Nueva compra conserva inventario restante");
        model.agregarProducto("P04", 2); model.retirarProducto("P04");
        check(model.getTotal().equals(new BigDecimal("0.00")), "Retirar selección recalcula total");
        rejects(() -> model.retirarProducto("P04"), "Retirar producto inexistente informa error");
        model.agregarProducto("P04", 1);
        check(model.pagar("5555555555554444").getFolio().equals("CU6-0002"), "Siguiente compra incrementa folio");

        FakeView view = new FakeView();
        CompraModel controllerModel = new CompraModel();
        CompraController controller = new CompraController(controllerModel, view);
        controller.iniciar();
        check(view.products == 5 && view.lines == 0 && !view.blocked, "Controlador inicia catálogo y carrito");
        controller.agregarProducto("P03", 10);
        check(view.error.contains("Sólo hay 5"), "Controlador traduce error de inventario a la vista");
        controller.agregarProducto("P01", 2); controller.agregarProducto("P02", 1);
        controller.consultarTarjeta("4111111111111111");
        check(view.card != null && view.card.getBanco().equals("Banco del Valle"), "Controlador presenta tarjeta consultada");
        controller.consultarTarjeta("incorrecta");
        check(view.card == null && view.error.contains("16 dígitos"), "Consulta errónea borra datos previos");
        controller.pagar("4111111111111111");
        check(view.ticket != null && view.blocked && view.total.equals(new BigDecimal("249.90")), "Controlador presenta ticket y bloquea compra");
        controller.pagar("4111111111111111");
        check(view.error.contains("ya fue pagada"), "Controlador informa pago duplicado");
        controller.nuevaCompra();
        check(view.ticket == null && view.card == null && !view.blocked && view.lines == 0, "Controlador restablece la vista para otra compra");
        System.out.println("RESULTADO: " + checks + " verificaciones correctas. Flujo básico y alternos aprobados.");
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
        checks++; System.out.println("OK " + checks + ": " + description);
    }
    private static void rejects(Runnable action, String description) {
        try { action.run(); } catch (IllegalArgumentException | IllegalStateException expected) { check(true, description); return; }
        throw new AssertionError(description);
    }
    private static final class FakeView implements CompraView {
        int products, lines; boolean blocked; String error = ""; Tarjeta card; Ticket ticket; BigDecimal total;
        public void mostrarProductos(List<Producto> p) { products = p.size(); }
        public void mostrarCarrito(List<LineaCompra> l, BigDecimal t) { lines = l.size(); total = t; }
        public void mostrarTarjeta(Tarjeta t) { card = t; }
        public void mostrarTicket(Ticket t) { ticket = t; }
        public void mostrarEstado(String message) { }
        public void mostrarError(String message) { error = message; }
        public void bloquearCompra(boolean value) { blocked = value; }
        public void limpiarPago() { card = null; ticket = null; }
    }
}
