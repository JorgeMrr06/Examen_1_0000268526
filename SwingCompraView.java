package edu.itson.cu6.view;

import edu.itson.cu6.controller.CompraController;
import edu.itson.cu6.model.LineaCompra;
import edu.itson.cu6.model.Producto;
import edu.itson.cu6.model.Tarjeta;
import edu.itson.cu6.model.Ticket;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/** Vista Swing: catálogo izquierda, carrito derecha y tarjeta/ticket abajo. */
public final class SwingCompraView extends JPanel implements CompraView {
    private static final Color NAVY = new Color(22, 39, 59);
    private static final Color TEAL = new Color(0, 110, 113);
    private static final Color BACKGROUND = new Color(241, 245, 249);
    private static final Color MUTED = new Color(82, 101, 118);
    private final DefaultTableModel modeloProductos = tablaModelo("Producto", "Costo (MXN)", "Disponible");
    private final DefaultTableModel modeloCarrito = tablaModelo("Producto", "Cant.", "Unitario", "Subtotal");
    private final JTable tablaProductos = crearTabla(modeloProductos, "catalogo");
    private final JTable tablaCarrito = crearTabla(modeloCarrito, "carrito");
    private final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JPasswordField numeroTarjeta = new JPasswordField(21);
    private final JButton agregar = crearBoton("Agregar a la selección", "agregar", true);
    private final JButton retirar = crearBoton("Retirar selección", "retirar", false);
    private final JButton consultar = crearBoton("Consultar tarjeta", "consultar", false);
    private final JButton pagar = crearBoton("Pagar y generar ticket", "pagar", true);
    private final JButton nuevaCompra = crearBoton("Nueva compra", "nueva", false);
    private final JLabel total = new JLabel("Total: $0.00 MXN", SwingConstants.RIGHT);
    private final JLabel banco = etiquetaDato("Banco emisor: —");
    private final JLabel ciudad = etiquetaDato("Ciudad: —");
    private final JLabel numeroMostrado = etiquetaDato("Tarjeta: —");
    private final JTextArea ticket = new JTextArea();
    private final JLabel estado = new JLabel(" ");
    private List<Producto> productos = new ArrayList<>();
    private List<LineaCompra> lineas = new ArrayList<>();
    private CompraController controlador;

    public SwingCompraView() {
        setName("compraView");
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(1160, 1000));
        add(crearCabecera(), BorderLayout.NORTH);
        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 1; gc.weighty = 0.45;
        gc.fill = GridBagConstraints.BOTH; gc.insets = new Insets(0, 0, 12, 0);
        contenido.add(crearSeleccion(), gc);
        gc.gridy = 1; gc.weighty = 0.55; gc.insets = new Insets(0, 0, 0, 0);
        contenido.add(crearZonaPago(), gc);
        add(contenido, BorderLayout.CENTER);
        estado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        estado.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 4));
        add(estado, BorderLayout.SOUTH);
        conectarAcciones();
    }

    public void setControlador(CompraController controlador) { this.controlador = controlador; }

    public static void configurarApariencia() {
        UIManager.put("Table.font", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        UIManager.put("Label.font", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        UIManager.put("Button.font", new Font(Font.SANS_SERIF, Font.BOLD, 14));
        UIManager.put("TextField.font", new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        UIManager.put("PasswordField.font", new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        UIManager.put("Spinner.font", new Font(Font.SANS_SERIF, Font.PLAIN, 16));
    }

    private JPanel crearCabecera() {
        JPanel panel = new JPanel(new BorderLayout(20, 4));
        panel.setOpaque(false);
        JPanel titulos = new JPanel(new GridLayout(3, 1, 0, 4));
        titulos.setOpaque(false);
        JLabel titulo = new JLabel("Comprar producto");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 29));
        titulo.setForeground(NAVY);
        JLabel descripcion = new JLabel("CU6 · Selecciona productos, consulta tu tarjeta y genera el ticket.");
        descripcion.setForeground(MUTED);
        JLabel autor = new JLabel("Luis Antonio Cota Rea · ITSON 0000268526");
        autor.setForeground(MUTED);
        autor.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        titulos.add(titulo); titulos.add(descripcion); titulos.add(autor);
        panel.add(titulos, BorderLayout.CENTER);
        JLabel demo = new JLabel("  DEMOSTRACIÓN LOCAL  ");
        demo.setOpaque(true); demo.setBackground(new Color(217, 238, 238)); demo.setForeground(TEAL);
        demo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        demo.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        JPanel derecha = new JPanel(new BorderLayout()); derecha.setOpaque(false);
        derecha.add(demo, BorderLayout.NORTH); derecha.add(nuevaCompra, BorderLayout.SOUTH);
        panel.add(derecha, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearSeleccion() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0)); panel.setOpaque(false);
        JPanel catalogo = tarjetaPanel("01  Catálogo de productos");
        JScrollPane lista = new JScrollPane(tablaProductos); lista.getViewport().setBackground(Color.WHITE);
        lista.setColumnHeaderView(tablaProductos.getTableHeader());
        lista.setPreferredSize(new Dimension(510, 210));
        catalogo.add(lista, BorderLayout.CENTER);
        JPanel controles = new JPanel(new BorderLayout(12, 0)); controles.setOpaque(false);
        JPanel entrada = new JPanel(new BorderLayout(8, 0)); entrada.setOpaque(false);
        entrada.add(new JLabel("Cantidad"), BorderLayout.WEST);
        cantidad.setName("cantidad"); cantidad.setPreferredSize(new Dimension(76, 36));
        ((JSpinner.NumberEditor) cantidad.getEditor()).getTextField().setEditable(true);
        entrada.add(cantidad, BorderLayout.CENTER);
        controles.add(entrada, BorderLayout.WEST); controles.add(agregar, BorderLayout.CENTER);
        catalogo.add(controles, BorderLayout.SOUTH);
        JPanel seleccionados = tarjetaPanel("02  Productos seleccionados");
        JScrollPane compra = new JScrollPane(tablaCarrito); compra.getViewport().setBackground(Color.WHITE);
        compra.setColumnHeaderView(tablaCarrito.getTableHeader());
        compra.setPreferredSize(new Dimension(510, 210));
        seleccionados.add(compra, BorderLayout.CENTER);
        JPanel resumen = new JPanel(new BorderLayout(12, 0)); resumen.setOpaque(false);
        total.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18)); total.setForeground(TEAL);
        resumen.add(retirar, BorderLayout.WEST); resumen.add(total, BorderLayout.CENTER);
        seleccionados.add(resumen, BorderLayout.SOUTH);
        panel.add(catalogo); panel.add(seleccionados);
        return panel;
    }

    private JPanel crearZonaPago() {
        JPanel panel = tarjetaPanel("03  Datos de tarjeta y ticket");
        JPanel parteSuperior = new JPanel(new BorderLayout(0, 8)); parteSuperior.setOpaque(false);
        JPanel entrada = new JPanel(new BorderLayout(12, 0)); entrada.setOpaque(false);
        JPanel campo = new JPanel(new BorderLayout(8, 0)); campo.setOpaque(false);
        campo.add(new JLabel("Número de tarjeta"), BorderLayout.WEST);
        numeroTarjeta.setName("numeroTarjeta"); numeroTarjeta.setMargin(new Insets(6, 8, 6, 8));
        campo.add(numeroTarjeta, BorderLayout.CENTER);
        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0)); botones.setOpaque(false);
        botones.add(consultar); botones.add(pagar);
        entrada.add(campo, BorderLayout.CENTER); entrada.add(botones, BorderLayout.EAST);
        parteSuperior.add(entrada, BorderLayout.NORTH);
        JPanel datos = new JPanel(new BorderLayout(0, 8)); datos.setOpaque(false);
        JLabel ayuda = new JLabel("Prueba: 4111 1111 1111 1111 · Datos ficticios · Sin cobros reales");
        ayuda.setForeground(MUTED); ayuda.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        datos.add(ayuda, BorderLayout.NORTH);
        JPanel detalle = new JPanel(new GridLayout(1, 3, 10, 0));
        detalle.setBackground(new Color(237, 247, 247));
        detalle.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        detalle.add(banco); detalle.add(ciudad); detalle.add(numeroMostrado);
        datos.add(detalle, BorderLayout.SOUTH);
        parteSuperior.add(datos, BorderLayout.CENTER);
        panel.add(parteSuperior, BorderLayout.NORTH);
        ticket.setName("ticket"); ticket.setEditable(false); ticket.setLineWrap(false);
        ticket.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        ticket.setForeground(NAVY); ticket.setBackground(new Color(250, 252, 253));
        ticket.setMargin(new Insets(12, 14, 12, 14));
        ticket.setText("TICKET DE COMPRA\n\nAquí aparecerá el ticket, debajo de los datos de tarjeta,\ncuando selecciones «Pagar y generar ticket».");
        JScrollPane comprobante = new JScrollPane(ticket);
        comprobante.setPreferredSize(new Dimension(900, 270));
        comprobante.setBorder(BorderFactory.createTitledBorder("Ticket · Se genera al confirmar el pago"));
        panel.add(comprobante, BorderLayout.CENTER);
        return panel;
    }

    private void conectarAcciones() {
        agregar.addActionListener(event -> {
            try {
                cantidad.commitEdit();
                int fila = tablaProductos.getSelectedRow();
                controlador.agregarProducto(fila < 0 ? null : productos.get(fila).getId(), (Integer) cantidad.getValue());
            } catch (ParseException error) {
                mostrarError("La cantidad debe ser un número entero entre 1 y 999.");
            }
        });
        retirar.addActionListener(event -> {
            int fila = tablaCarrito.getSelectedRow();
            controlador.retirarProducto(fila < 0 ? null : lineas.get(fila).getProducto().getId());
        });
        consultar.addActionListener(event -> controlador.consultarTarjeta(new String(numeroTarjeta.getPassword())));
        pagar.addActionListener(event -> controlador.pagar(new String(numeroTarjeta.getPassword())));
        nuevaCompra.addActionListener(event -> controlador.nuevaCompra());
        numeroTarjeta.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { mostrarTarjeta(null); }
            public void removeUpdate(DocumentEvent event) { mostrarTarjeta(null); }
            public void changedUpdate(DocumentEvent event) { mostrarTarjeta(null); }
        });
    }

    @Override public void mostrarProductos(List<Producto> productos) {
        int seleccion = tablaProductos.getSelectedRow();
        this.productos = List.copyOf(productos);
        modeloProductos.setRowCount(0);
        for (Producto producto : productos) modeloProductos.addRow(new Object[]{producto.getNombre(), dinero(producto.getCosto()), producto.getStock()});
        if (seleccion >= 0 && seleccion < tablaProductos.getRowCount()) tablaProductos.setRowSelectionInterval(seleccion, seleccion);
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(260);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(100);
    }

    @Override public void mostrarCarrito(List<LineaCompra> lineas, BigDecimal totalCompra) {
        this.lineas = List.copyOf(lineas);
        modeloCarrito.setRowCount(0);
        for (LineaCompra linea : lineas) modeloCarrito.addRow(new Object[]{linea.getProducto().getNombre(), linea.getCantidad(), dinero(linea.getCostoUnitario()), dinero(linea.getSubtotal())});
        total.setText("Total: " + dinero(totalCompra) + " MXN");
        tablaCarrito.getColumnModel().getColumn(0).setPreferredWidth(210);
        tablaCarrito.getColumnModel().getColumn(1).setPreferredWidth(44);
    }

    @Override public void mostrarTarjeta(Tarjeta tarjeta) {
        banco.setText("Banco emisor: " + (tarjeta == null ? "—" : tarjeta.getBanco()));
        ciudad.setText("Ciudad: " + (tarjeta == null ? "—" : tarjeta.getCiudad()));
        numeroMostrado.setText("Tarjeta: " + (tarjeta == null ? "—" : tarjeta.getNumeroEnmascarado()));
    }

    @Override public void mostrarTicket(Ticket comprobante) {
        Tarjeta tarjeta = comprobante.getTarjeta();
        StringBuilder texto = new StringBuilder("TICKET DE COMPRA  |  SIMULACIÓN LOCAL\n");
        texto.append("Folio: ").append(comprobante.getFolio()).append("   Fecha: ")
                .append(comprobante.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append(" (Hermosillo)\n");
        texto.append("Banco emisor: ").append(tarjeta.getBanco()).append("   Ciudad: ").append(tarjeta.getCiudad()).append('\n');
        texto.append("Tarjeta: ").append(tarjeta.getNumeroEnmascarado()).append('\n');
        texto.append("---------------------------------------------------------------\n");
        texto.append(String.format("%-30s %5s %11s %11s%n", "Producto", "Cant.", "Unitario", "Subtotal"));
        for (LineaCompra linea : comprobante.getLineas()) {
            texto.append(String.format("%-30s %5d %11s %11s%n", linea.getProducto().getNombre(), linea.getCantidad(), dinero(linea.getCostoUnitario()), dinero(linea.getSubtotal())));
        }
        texto.append("---------------------------------------------------------------\n");
        texto.append("TOTAL: ").append(dinero(comprobante.getTotal())).append(" MXN\n");
        texto.append("Pago simulado completado. Gracias por tu compra.");
        ticket.setText(texto.toString()); ticket.setCaretPosition(0);
    }

    @Override public void mostrarEstado(String mensaje) { estado.setForeground(TEAL); estado.setText(mensaje); }
    @Override public void mostrarError(String mensaje) { estado.setForeground(new Color(169, 49, 39)); estado.setText("Error: " + mensaje); }
    @Override public void bloquearCompra(boolean bloqueada) {
        agregar.setEnabled(!bloqueada); retirar.setEnabled(!bloqueada); pagar.setEnabled(!bloqueada);
        consultar.setEnabled(!bloqueada); cantidad.setEnabled(!bloqueada); numeroTarjeta.setEnabled(!bloqueada);
        tablaProductos.setEnabled(!bloqueada); tablaCarrito.setEnabled(!bloqueada);
    }
    @Override public void limpiarPago() {
        numeroTarjeta.setText(""); mostrarTarjeta(null);
        ticket.setText("TICKET DE COMPRA\n\nAquí aparecerá el ticket, debajo de los datos de tarjeta,\ncuando selecciones «Pagar y generar ticket».");
        cantidad.setValue(1);
    }

    private static String dinero(BigDecimal importe) { return "$" + importe.toPlainString(); }
    private static DefaultTableModel tablaModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) { @Override public boolean isCellEditable(int fila, int columna) { return false; } };
    }
    private static JTable crearTabla(DefaultTableModel modelo, String nombre) {
        JTable tabla = new JTable(modelo); tabla.setName(nombre); tabla.setRowHeight(34);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); tabla.setFillsViewportHeight(true);
        tabla.setGridColor(new Color(224, 230, 236)); tabla.setSelectionBackground(new Color(213, 237, 238));
        tabla.setSelectionForeground(NAVY); tabla.setForeground(NAVY);
        tabla.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tabla.getTableHeader().setPreferredSize(new Dimension(100, 34));
        return tabla;
    }
    private static JButton crearBoton(String texto, String nombre, boolean principal) {
        JButton boton = new JButton(texto); boton.setName(nombre);
        boton.setFocusPainted(false); boton.setMargin(new Insets(9, 13, 9, 13));
        boton.setBackground(principal ? TEAL : new Color(232, 239, 245));
        boton.setForeground(principal ? Color.WHITE : NAVY);
        return boton;
    }
    private static JLabel etiquetaDato(String texto) {
        JLabel etiqueta = new JLabel(texto); etiqueta.setForeground(NAVY);
        etiqueta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12)); return etiqueta;
    }
    private static JPanel tarjetaPanel(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(0, 12)); panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(219, 228, 236)),
                        titulo, 0, 0, new Font(Font.SANS_SERIF, Font.BOLD, 17), NAVY),
                BorderFactory.createEmptyBorder(12, 14, 14, 14)));
        return panel;
    }
}
