package modelo;

public class Buque extends ObjetoJuego implements EntidadMovil {
    //los buques transitan siempre a profundidad cero.
	public static final int PROFUNDIDAD = 0;

    private boolean derecha;
    private double velocidad;
    private double velocidadCaidaCargas;
    private int pasadasRestantes;

    public Buque(double x, boolean derecha, double velocidad,
    		     double velocidadCaidaCargas, int pasadas) {
        super(x, PROFUNDIDAD);
        this.derecha = derecha;
        this.velocidad = velocidad;
        this.velocidadCaidaCargas = velocidadCaidaCargas;
        pasadasRestantes = pasadas;
    }

    @Override
    public void moverse() {
        if (derecha) {
            setPosicionX(getPosicionX() + velocidad);
        } else {
            setPosicionX(getPosicionX() - velocidad);
        }
    }

    public CargaProfundidad lanzarCarga() {
        return new CargaProfundidad(getPosicionX(), getPosicionY(), velocidadCaidaCargas);
    }

    public boolean salioDelCampo(double limiteHorizontal) {
        return getPosicionX() < -limiteHorizontal || getPosicionX() > limiteHorizontal;
    }

    public boolean isDerecha() {
        return derecha;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public double getVelocidadCaidaCargas() {
        return velocidadCaidaCargas;
    }
    
    public int getPasadasRestantes() {
    	return pasadasRestantes;
    }
    
    public void completarPasada() {
    	if (pasadasRestantes > 0) {
    		pasadasRestantes--;
    	}
    	if (pasadasRestantes > 0) {
    		derecha = !derecha;
    	}	
    }
}

