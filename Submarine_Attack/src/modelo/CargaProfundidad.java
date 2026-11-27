package modelo;

import java.util.Random;

public class CargaProfundidad extends ObjetoJuego implements EntidadMovil {
    //cada carga tiene un detonador que provoca su explosión a una profundidad
	//aleatoria entre 300 y 700 metros
	public static final int PROFUNDIDAD_EXPLOSION_MIN = 300;
    public static final int PROFUNDIDAD_EXPLOSION_MAX = 700;

    //Crea un objeto que permite generar números aleatorios.
    private static final Random RANDOM = new Random();

    //una vez creada la carga, su velocidad no puede modificarse
    //mediante una nueva asignación.
    private final double velocidadCaida;
    private final int profundidadExplosion;
    private boolean exploto = false;
    private int pasosDesdeExplosion = 0;

    public CargaProfundidad(double x, double y, double velocidadCaida) {
        //Como CargaProfundidad hereda de ObjetoJuego, utilizamos
     	//super para inicializar la parte heredada del objeto.
    		super(x, y);
        this.velocidadCaida = velocidadCaida;
        this.profundidadExplosion = PROFUNDIDAD_EXPLOSION_MIN +
                RANDOM.nextInt(
                		//nextInt(n) no incluye el valor n.
                		PROFUNDIDAD_EXPLOSION_MAX - PROFUNDIDAD_EXPLOSION_MIN + 1);
    }

    @Override
    public void moverse() {
        if (exploto) {
            pasosDesdeExplosion++;
            return;
        }
        double nuevaY = getPosicionY() + velocidadCaida;
        if (nuevaY >= profundidadExplosion) {
            nuevaY = profundidadExplosion;
            exploto = true;
        }
        setPosicionY(nuevaY);
    }

    public void explotar() {
        exploto = true;
    }

    public boolean exploto() {
        return exploto;
    }

    public int getPasosDesdeExplosion() {
        return pasosDesdeExplosion;
    }
}

