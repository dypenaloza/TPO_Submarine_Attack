package main;

import controlador.ControladorJuego;
import javax.swing.JFrame;
import vista.VentanaPrincipal;

public class Main {
    public static void main(String[] args) {
        VentanaPrincipal vista = new VentanaPrincipal();
        new ControladorJuego(vista);

        JFrame ventana = new JFrame("Submarine Attack");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setContentPane(vista.getPanelPrincipal());
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
