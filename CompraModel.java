package edu.itson.cu6.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reglas de negocio e inventario de una sesión. No usa red ni cobra dinero. */
public final class CompraModel {
    private final Map<String, Producto> productos = new LinkedHashMap<>();
    private final Map<String, Tarjeta> tarjetas = new LinkedHashMap<>();
    private final Map<String, LineaCompra> carrito = new LinkedHashMap<>();
    private Ticket ultimoTicket;
    private boolean pagada;
    private int consecutivo = 1;

    public CompraModel() {
        productos.put("P01", new Producto("P01", "Cuaderno profesional", new BigDecimal("45.00"), 20));
        productos.put("P02", new Producto("P02", "Memoria USB 32 GB", new BigDecimal("159.90"), 10));
        productos.put("P03", new Producto("P03", "Mochila escolar", new BigDecimal("499.00"), 5));
        productos.put("P04", new Producto("P04", "Bolígrafo azul", new BigDecimal("12.50"), 40));
        productos.put("P05", new Producto("P05", "Termo de acero", new BigDecimal("229.00"), 8));
        tarjetas.put("4111111111111111", new Tarjeta("4111111111111111", "Banco del Valle", "Hermosillo"));
        tarjetas.put("5555555555554444", new Tarjeta("5555555555554444", "Banco del Pacífico", "Ciudad Obregón"));
        tarjetas.put("4000000000000002", new Tarjeta("4000000000000002", "Banco del Norte", "Navojoa"));
    }

    public List<Producto> listarProductos() { return List.copyOf(productos.values()); }
    public List<LineaCompra> getCarrito() { return List.copyOf(carrito.values()); }
    public BigDecimal getTotal() {
        return carrito.values().stream().map(LineaCompra::getSubtotal)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
    public Ticket getUltimoTicket() { return ultimoTicket; }
    public boolean isPagada() { return pagada; }

    public void agregarProducto(String id, int cantidad) {
        asegurarCompraAbierta();
        Producto producto = productos.get(id);
        if (producto == null) throw new IllegalArgumentException("Selecciona un producto del catálogo.");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        LineaCompra previa = carrito.get(id);
        long acumulada = (previa == null ? 0L : previa.getCantidad()) + cantidad;
        if (acumulada > producto.getStock()) {
            throw new IllegalArgumentException("Sólo hay " + producto.getStock() + " unidades de "
                    + producto.getNombre() + ". La cantidad incluye lo ya seleccionado.");
        }
        carrito.put(id, new LineaCompra(producto, (int) acumulada));
    }

    public void retirarProducto(String id) {
        asegurarCompraAbierta();
        if (carrito.remove(id) == null) throw new IllegalArgumentException("Selecciona un producto del carrito.");
    }

    public Tarjeta consultarTarjeta(String numero) {
        String limpio = normalizarNumero(numero);
        Tarjeta tarjeta = tarjetas.get(limpio);
        if (tarjeta == null) throw new IllegalArgumentException("Tarjeta no registrada. Usa una tarjeta ficticia de la lista de demostración.");
        return tarjeta;
    }

    public Ticket pagar(String numero) {
        asegurarCompraAbierta();
        if (carrito.isEmpty()) throw new IllegalStateException("Agrega al menos un producto antes de pagar.");
        Tarjeta tarjeta = consultarTarjeta(numero);
        // Todas las validaciones se completan antes de modificar el inventario.
        for (LineaCompra linea : carrito.values()) {
            if (linea.getCantidad() > linea.getProducto().getStock()) {
                throw new IllegalStateException("El inventario cambió. Revisa las cantidades seleccionadas.");
            }
        }
        Ticket ticket = new Ticket(String.format("CU6-%04d", consecutivo),
                LocalDateTime.now(ZoneId.of("America/Hermosillo")), tarjeta, getCarrito());
        for (LineaCompra linea : carrito.values()) {
            linea.getProducto().descontarStock(linea.getCantidad());
        }
        consecutivo++;
        ultimoTicket = ticket;
        pagada = true;
        return ticket;
    }

    public void reiniciarCompra() {
        carrito.clear();
        ultimoTicket = null;
        pagada = false;
    }

    private void asegurarCompraAbierta() {
        if (pagada) throw new IllegalStateException("Esta compra ya fue pagada. Inicia una nueva compra.");
    }

    private String normalizarNumero(String numero) {
        if (numero == null) throw new IllegalArgumentException("Escribe el número de tarjeta.");
        String limpio = numero.replace(" ", "").replace("-", "");
        if (!limpio.matches("[0-9]{16}")) {
            throw new IllegalArgumentException("La tarjeta debe contener 16 dígitos; se permiten espacios y guiones.");
        }
        int suma = 0;
        for (int i = limpio.length() - 1, posicion = 0; i >= 0; i--, posicion++) {
            int digito = limpio.charAt(i) - '0';
            if (posicion % 2 == 1) { digito *= 2; if (digito > 9) digito -= 9; }
            suma += digito;
        }
        if (suma % 10 != 0) throw new IllegalArgumentException("El número no pasa la validación local de Luhn.");
        return limpio;
    }
}
