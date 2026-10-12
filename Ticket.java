package edu.itson.cu6.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Resultado inmutable de una compra completada dentro del simulador. */
public final class Ticket {
    private final String folio;
    private final LocalDateTime fecha;
    private final Tarjeta tarjeta;
    private final List<LineaCompra> lineas;
    private final BigDecimal total;

    public Ticket(String folio, LocalDateTime fecha, Tarjeta tarjeta, List<LineaCompra> lineas) {
        this.folio = folio;
        this.fecha = fecha;
        this.tarjeta = tarjeta;
        this.lineas = List.copyOf(lineas);
        this.total = lineas.stream().map(LineaCompra::getSubtotal)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public String getFolio() { return folio; }
    public LocalDateTime getFecha() { return fecha; }
    public Tarjeta getTarjeta() { return tarjeta; }
    public List<LineaCompra> getLineas() { return lineas; }
    public BigDecimal getTotal() { return total; }
}
