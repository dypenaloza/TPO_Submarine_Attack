package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import modelo.Buque;
import modelo.CargaProfundidad;
import modelo.Juego;
import modelo.Submarino;

public class PanelJuego extends JPanel implements ActionListener {
    private static final int ALTO_CIELO = 80;
    private static final double PROFUNDIDAD_VISIBLE = 850;
    private static final Color COLOR_CIELO = new Color(135, 200, 235);
    private static final Color COLOR_AGUA = new Color(10, 40, 90);
    private static final int MILISEGUNDOS_FADING = 50;
    private static final int PASOS_FADING = 20;

    private Image imagenSubmarino = new ImageIcon(getClass().getResource("/imagenes/submarino.png")).getImage();
    private Image imagenBarco = new ImageIcon(getClass().getResource("/imagenes/barco.png")).getImage();
    private Image imagenCarga = new ImageIcon(getClass().getResource("/imagenes/carga.png")).getImage();
    private Image imagenExplosion = new ImageIcon(getClass().getResource("/imagenes/explosion.png")).getImage();

    private Juego juego;
    private Timer timerFading;
    private int pasosFading = 0;
    private float transparencia = 1;

    public PanelJuego() {
        setPreferredSize(new Dimension(800, 500));
        setBackground(COLOR_AGUA);
        setFocusable(true);
        timerFading = new Timer(MILISEGUNDOS_FADING, this);
    }

    public void iniciarFading() {
        pasosFading = PASOS_FADING;
        timerFading.restart();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pasosFading--;
        transparencia = 0.1f + Math.abs(pasosFading - PASOS_FADING / 2) * 0.09f;
        if (pasosFading == 0) {
            timerFading.stop();
            transparencia = 1;
        }
        repaint();
    }

    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    private int aPixelX(double x) {
        return (int) ((x + Juego.LIMITE_HORIZONTAL) / (2 * Juego.LIMITE_HORIZONTAL) * getWidth());
    }

    private int aPixelY(double profundidad) {
        return ALTO_CIELO + (int) (profundidad / PROFUNDIDAD_VISIBLE * (getHeight() - ALTO_CIELO));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(COLOR_CIELO);
        g.fillRect(0, 0, getWidth(), ALTO_CIELO);

        if (juego == null) {
            return;
        }

        for (Buque buque : juego.getBuquesActivos()) {
            int x = aPixelX(buque.getPosicionX());
            int y = aPixelY(buque.getPosicionY());
            if (buque.isDerecha()) {
                g.drawImage(imagenBarco, x - 30, y - 22, 60, 27, null);
            } else {
                g.drawImage(imagenBarco, x + 30, y - 22, -60, 27, null);
            }
        }

        for (CargaProfundidad carga : juego.getCargas()) {
            int x = aPixelX(carga.getPosicionX());
            int y = aPixelY(carga.getPosicionY());
            if (carga.exploto()) {
                int radio = (int) (50 / PROFUNDIDAD_VISIBLE * (getHeight() - ALTO_CIELO));
                g.drawImage(imagenExplosion, x - radio, y - radio, radio * 2, radio * 2, null);
            } else {
                g.drawImage(imagenCarga, x - 5, y - 7, 10, 14, null);
            }
        }

        Submarino submarino = juego.getSubmarino();
        int x = aPixelX(submarino.getPosicionX());
        int y = aPixelY(submarino.getPosicionY());
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparencia));
        g2.drawImage(imagenSubmarino, x - 18, y - 7, 36, 14, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }
}
