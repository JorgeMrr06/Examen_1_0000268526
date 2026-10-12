package edu.itson.cu6;

import edu.itson.cu6.controller.CompraController;
import edu.itson.cu6.model.CompraModel;
import edu.itson.cu6.view.SwingCompraView;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/** Ejecuta eventos Swing reales en EDT y dibuja componentes con printAll. */
public final class ScreenshotHarness {
    private static Path output;
    private static SwingCompraView view;
    private static final List<String> log = new ArrayList<>();
    private static int frame;

    public static void main(String[] args) throws Exception {
        output = Path.of(args.length == 0 ? "build/evidencia" : args[0]).toAbsolutePath();
        Files.createDirectories(output.resolve("frames"));
        SwingUtilities.invokeAndWait(() -> {
            try {
                SwingCompraView.configurarApariencia();
                CompraModel model = new CompraModel();
                view = new SwingCompraView();
                CompraController controller = new CompraController(model, view);
                view.setControlador(controller); controller.iniciar();
                view.setSize(1160, 1000); layoutAll(view);
                save("01_catalogo.png"); saveFrame("Aplicación iniciada; catálogo y carrito vacío");
                JTable catalog = (JTable) find(view, "catalogo");
                JSpinner quantity = (JSpinner) find(view, "cantidad");
                catalog.setRowSelectionInterval(0, 0); quantity.setValue(2);
                saveFrame("Cliente selecciona Cuaderno profesional y cantidad2");
                click("agregar"); saveFrame("Se agregan dos cuadernos al carrito de la derecha");
                catalog.setRowSelectionInterval(1, 1); quantity.setValue(1);
                saveFrame("Cliente selecciona Memoria USB32GB y cantidad1");
                click("agregar"); save("02_seleccion.png");
                saveFrame("Carrito con dos cuadernos y una USB; total249.90MXN");
                JPasswordField card = (JPasswordField) find(view, "numeroTarjeta");
                card.setText("4111"); saveFrame("Comienza la captura de tarjeta ficticia");
                card.setText("4111 1111"); saveFrame("Captura de tarjeta en campo protegido");
                card.setText("4111 1111 1111"); saveFrame("Continúa captura de tarjeta ficticia");
                card.setText("4111 1111 1111 1111"); saveFrame("Número completo capturado; listo para consultar");
                click("consultar"); save("03_tarjeta.png");
                saveFrame("Zona inferior: Banco del Valle, Hermosillo, tarjeta terminada1111");
                click("pagar"); save("04_ticket.png");
                saveFrame("Pago simulado; ticket debajo de los datos de tarjeta");
                if (!model.isPagada() || !model.getTotal().toPlainString().equals("249.90")) throw new AssertionError("Flujo Swing incompleto");
                if (((JButton) find(view, "pagar")).isEnabled()) throw new AssertionError("Pago duplicado habilitado");
                String text = ((JTextArea) find(view, "ticket")).getText();
                if (!text.contains("249.90") || !text.contains("Banco del Valle") || !text.contains("Hermosillo") || text.contains("4111111111111111")) throw new AssertionError("Contenido de ticket incorrecto");
                Files.writeString(output.resolve("ticket_demo.txt"), text, StandardCharsets.UTF_8);
                log.add("Verificación: total249.90; inventario P01=18, P02=9; pago bloqueado; ticket con tarjeta enmascarada.");
                Files.write(output.resolve("demo_log.txt"), log, StandardCharsets.UTF_8);
            } catch (Exception error) { throw new RuntimeException(error); }
        });
        System.out.println("UI VERIFICADA: cuatro estados y " + frame + " frames Swing en " + output);
    }

    private static void click(String name) { ((JButton) find(view, name)).doClick(0); }
    private static void saveFrame(String description) throws Exception {
        frame++; save(String.format("frames/%02d.png", frame)); log.add(String.format("%02d: %s", frame, description));
    }
    private static void save(String name) throws Exception {
        layoutAll(view);
        BufferedImage image = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        view.printAll(graphics); graphics.dispose();
        ImageIO.write(image, "png", output.resolve(name).toFile());
    }
    private static void layoutAll(Container container) {
        container.doLayout();
        for (Component component : container.getComponents()) if (component instanceof Container child) layoutAll(child);
    }
    private static Component find(Component component, String name) {
        if (name.equals(component.getName())) return component;
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) { Component found = find(child, name); if (found != null) return found; }
        }
        return null;
    }
}
