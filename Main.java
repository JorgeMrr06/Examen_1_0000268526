package edu.itson.cu6;

import edu.itson.cu6.controller.CompraController;
import edu.itson.cu6.model.CompraModel;
import edu.itson.cu6.view.SwingCompraView;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Punto de entrada: construye y conecta Modelo, Vista y Controlador. */
public final class Main {
    private Main() { }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingCompraView.configurarApariencia();
            CompraModel model = new CompraModel();
            SwingCompraView view = new SwingCompraView();
            CompraController controller = new CompraController(model, view);
            view.setControlador(controller);
            controller.iniciar();
            JFrame ventana = new JFrame("CU6 · Comprar producto · Luis Antonio Cota Rea");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(view); ventana.pack();
            Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            ventana.setMinimumSize(new Dimension(Math.min(1020, pantalla.width - 40), Math.min(820, pantalla.height - 40)));
            ventana.setSize(Math.min(ventana.getWidth(), pantalla.width - 40), Math.min(ventana.getHeight(), pantalla.height - 40));
            ventana.setLocationRelativeTo(null); ventana.setVisible(true);
        });
    }
}
