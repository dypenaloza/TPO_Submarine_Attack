package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;
import modelo.Juego;
import modelo.Juego.EstadoJuego;
import persistencia.PersistenciaDatos;
import vista.VentanaPrincipal;

public class ControladorJuego implements ActionListener, KeyListener {
    private static final int MILISEGUNDOS_POR_PASO = 16;

    private VentanaPrincipal vista;
    private Timer timer;
    private Juego juego;
    private PersistenciaDatos persistenciaDatos;
    private String nombreJugador;
    private int record;

    public ControladorJuego(VentanaPrincipal vista) {
        this.vista = vista;
        this.timer = new Timer(MILISEGUNDOS_POR_PASO, this);
        this.persistenciaDatos = new PersistenciaDatos();

        vista.getIngresarButton().addActionListener(this);
        vista.getNombreField().addActionListener(this);
        vista.getJugarButton().addActionListener(this);
        vista.getJugardeNuevoButton().addActionListener(this);
        vista.getInstruccionesButton().addActionListener(this);
        vista.getVolverInstrucciones().addActionListener(this);
        vista.getSalirButton().addActionListener(this);
        vista.getSalirGameOverButton().addActionListener(this);

        vista.getPanelJuego().addKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == timer) {
            paso();
        } else if (origen == vista.getIngresarButton() || origen == vista.getNombreField()) {
            iniciarSesion();
        } else if (origen == vista.getJugarButton() || origen == vista.getJugardeNuevoButton()) {
            iniciarPartida();
        } else if (origen == vista.getInstruccionesButton()) {
            vista.mostrar(VentanaPrincipal.INSTRUCCIONES);
        } else if (origen == vista.getVolverInstrucciones()) {
            vista.mostrar(VentanaPrincipal.MENU);
        } else if (origen == vista.getSalirButton() || origen == vista.getSalirGameOverButton()) {
            System.exit(0);
        }
    }

    private void iniciarSesion() {
        nombreJugador = vista.getNombreJugador();
        if (nombreJugador.isEmpty()) {
            return;
        }
        record = persistenciaDatos.leerRecord(nombreJugador);
        vista.mostrar(VentanaPrincipal.MENU);
    }

    private void iniciarPartida() {
        juego = new Juego();
        juego.cambiarEstado(EstadoJuego.JUGANDO);
        vista.getPanelJuego().setJuego(juego);
        vista.actualizarSalud(juego.getSubmarino().getSalud());
        actualizarVista();
        vista.mostrar(VentanaPrincipal.JUEGO);
        timer.start();
    }

    private void paso() {
        int nivelAnterior = juego.getNivel();
        int saludAnterior = juego.getSubmarino().getSalud();
        int vidasAnteriores = juego.getVidas();
        juego.actualizarJuego();
        actualizarVista();

        if (juego.getSubmarino().getSalud() < saludAnterior || juego.getVidas() < vidasAnteriores) {
            vista.actualizarSalud(juego.getSubmarino().getSalud());
            vista.getPanelJuego().iniciarFading();
        }

        if (juego.getNivel() != nivelAnterior) {
            guardarRecord();
        }
        if (juego.terminado()) {
            timer.stop();
            guardarRecord();
            vista.mostrarFinDelJuego(juego.gano(), juego.getPuntos(), nombreJugador, record);
        }
    }

    private void guardarRecord() {
        if (juego.getPuntos() > record) {
            record = juego.getPuntos();
            persistenciaDatos.guardarRecord(nombreJugador, record);
        }
    }

    private void actualizarVista() {
        vista.actualizarHud(juego.getVidas(), juego.getPuntos(), juego.getNivel());
        vista.getPanelJuego().repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (juego == null || juego.terminado()) {
            return;
        } 
        
        if (juego == null || juego.terminado()) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {

            if (juego.getEstado() == Juego.EstadoJuego.JUGANDO) {

                juego.cambiarEstado(Juego.EstadoJuego.PAUSADO);

            } else if (juego.getEstado() == Juego.EstadoJuego.PAUSADO) {

                juego.cambiarEstado(Juego.EstadoJuego.JUGANDO);
            }

            return;
        }

        // Si no estamos jugando, no permitir movimientos
        if (juego.getEstado() != Juego.EstadoJuego.JUGANDO) {
            return;
        }
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                juego.subirSubmarino();
                break;
            case KeyEvent.VK_DOWN:
                juego.bajarSubmarino();
                break;
            case KeyEvent.VK_LEFT:
                juego.moverSubmarinoIzquierda();
                break;
            case KeyEvent.VK_RIGHT:
                juego.moverSubmarinoDerecha();
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
