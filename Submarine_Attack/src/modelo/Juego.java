package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Juego {
	public static final double LIMITE_HORIZONTAL = 750;

	private static final Random RANDOM = new Random();
	private static final int BUQUES_ACTIVOS_MAX = 3;
	private static final int BUQUES_POR_SERIE = 12;
	private static final int VIDAS_INICIALES = 3;
	private static final int NIVELES_TOTALES = 3;
	private static final int PUNTOS_POR_NIVEL = 200;
	private static final int PUNTOS_PARA_VIDA_EXTRA = 500;
	private static final double AUMENTO_POR_NIVEL = 1.2;
	private static final int PASOS_ENTRE_BUQUES = 120;
	private static final double DISTANCIA_CHOQUE = 30;
	private static final int PASOS_EXPLOSION_VISIBLE = 20;

	private Submarino submarino;
	private int puntos = 0;
	private int vidas = VIDAS_INICIALES;
	private int nivel = 1;
	private int puntosProximaVida = PUNTOS_PARA_VIDA_EXTRA;
	private int pasos = 0;
	private EstadoJuego estado;

	private double velocidadBuques = 2.3;
	private double velocidadCaidaCargas = 1.5;
	private int pasosEntreDisparos = 80;

	private List<Buque> buquesActivos = new ArrayList<Buque>();
	private int contadorBuquesTotales = 0;

	private List<CargaProfundidad> cargas = new ArrayList<CargaProfundidad>();

	public enum EstadoJuego {
	    INICIO,
	    JUGANDO,
	    PAUSADO,
	    GAME_OVER,
	    VICTORIA
	}
	
	public Juego() {
		inicializarJuego();
	}

	private void inicializarJuego() {
		submarino = new Submarino(0, 500);
		estado = EstadoJuego.INICIO;
		agregarBuque();
	}

	public void actualizarJuego() {
		if (estado != EstadoJuego.JUGANDO) {
			return;
		}
		pasos++;

		if (pasos % PASOS_ENTRE_BUQUES == 0) {
			agregarBuque();
		}
		if (pasos % pasosEntreDisparos == 0) {
			lanzarCargaAleatoria();
		}

		actualizarBuques();
		actualizarCargas();

		if (terminado()) {
		    return;
		}

		if (serieTerminada()) {
		    subirDeNivel();
		}
	}

	private Buque crearBuque() {
		boolean apareceIzquierda = RANDOM.nextBoolean();
		Buque buque;
		int pasadas = RANDOM.nextInt(1, 4);

		if (apareceIzquierda)
		{
			buque = new Buque(-LIMITE_HORIZONTAL, true, velocidadBuques, velocidadCaidaCargas, pasadas);
		}
		else
		{
			buque = new Buque(LIMITE_HORIZONTAL, false, velocidadBuques, velocidadCaidaCargas, pasadas);
		}
		return buque;
	}

	private void agregarBuque() {
		if (buquesActivos.size() < BUQUES_ACTIVOS_MAX && contadorBuquesTotales < BUQUES_POR_SERIE)
		{
			buquesActivos.add(crearBuque());
			contadorBuquesTotales++;
		}
	}

	private void actualizarBuques() {
		for (int i = buquesActivos.size() - 1; i >= 0; i--)
		{
			Buque buqueActual = buquesActivos.get(i);
			buqueActual.moverse();

			if (buqueActual.salioDelCampo(LIMITE_HORIZONTAL))
			{
				buqueActual.completarPasada();
			}

			if (buqueActual.getPasadasRestantes() == 0)
			{
				buquesActivos.remove(i);
			}
		}
	}

	private void lanzarCargaAleatoria() {
		if (buquesActivos.isEmpty())
		{
			return;
		}

		int indice = RANDOM.nextInt(buquesActivos.size());
		Buque buque = buquesActivos.get(indice);
		cargas.add(buque.lanzarCarga());
	}

	private void actualizarCargas() {
		for (int i = cargas.size() - 1; i >= 0; i--)
		{
			CargaProfundidad cargaActual = cargas.get(i);

			if (cargaActual.exploto())
			{
				cargaActual.moverse();
				if (cargaActual.getPasosDesdeExplosion() > PASOS_EXPLOSION_VISIBLE)
				{
					cargas.remove(i);
				}
			}
			else
			{
				cargaActual.moverse();
				
				double distancia = cargaActual.distanciaA(submarino);

				//Si toca al submarino
				if (!cargaActual.exploto() && distancia < DISTANCIA_CHOQUE)
				{
					cargaActual.explotar();
				}
				
				if (cargaActual.exploto())
				{
					aplicarExplosion(distancia);
				
				
					if (terminado() )
					{
						return;
					}
				}
			}
		}
	}

	private void aplicarExplosion(double distancia) {
		if (distancia < 10)
		{
			perderVida();
		}
		else if (distancia < 50)
		{
			daniarSubmarino(50);
		}
		else if (distancia <= 100)
		{
			daniarSubmarino(30);
			sumarPuntos(10);
		}
		else
		{
			sumarPuntos(30);
		}
	}

	private void daniarSubmarino(int porcentaje) {
		submarino.recibirDanio(porcentaje);
		if (submarino.estaDestruido())
		{
			perderVida();
		}
	}

	private void perderVida() {
		if (vidas > 0)
		{
			vidas--;
		}

		if (vidas > 0)
		{
			submarino.restaurarSalud();
		}
		
		if (vidas == 0)
		{
			cambiarEstado(EstadoJuego.GAME_OVER);
		}
		
	}

	private void sumarPuntos(int cantidad) {
		puntos = puntos + cantidad;
		if (puntos >= puntosProximaVida)
		{
			vidas++;
			puntosProximaVida = puntosProximaVida + PUNTOS_PARA_VIDA_EXTRA;
		}
	}

	private boolean serieTerminada() {
		return contadorBuquesTotales == BUQUES_POR_SERIE && buquesActivos.isEmpty() && cargas.isEmpty();
	}

	private void subirDeNivel() {
		sumarPuntos(PUNTOS_POR_NIVEL);
		if (nivel == NIVELES_TOTALES)
		{
			cambiarEstado(EstadoJuego.VICTORIA);
			return;
		}
		nivel++;
		velocidadBuques = velocidadBuques * AUMENTO_POR_NIVEL;
		velocidadCaidaCargas = velocidadCaidaCargas * AUMENTO_POR_NIVEL;
		pasosEntreDisparos = (int) (pasosEntreDisparos / AUMENTO_POR_NIVEL);
		contadorBuquesTotales = 0;
	}
	
	public void cambiarEstado(EstadoJuego nuevoEstado) {

	    if (nuevoEstado == null) {
	        return;
	    }

	    //INICIO => solamente podemos comenzar a jugar.
	    if (estado == EstadoJuego.INICIO) {

	        if (nuevoEstado == EstadoJuego.JUGANDO) {
	            estado = nuevoEstado;
	        }

	    }

	    // Mientras jugamos podemos pausar, perder o ganar.
	    else if (estado == EstadoJuego.JUGANDO) {

	        if (nuevoEstado == EstadoJuego.PAUSADO) {
	            estado = nuevoEstado;
	        }

	        else if (nuevoEstado == EstadoJuego.GAME_OVER
	                && vidas == 0) {

	            estado = nuevoEstado;
	        }

	        else if (nuevoEstado == EstadoJuego.VICTORIA
	                && nivel == NIVELES_TOTALES
	                && serieTerminada()) {

	            estado = nuevoEstado;
	        }

	    }

	    //PAUSADO => solamente podemos reanudar.
	    else if (estado == EstadoJuego.PAUSADO) {

	        if (nuevoEstado == EstadoJuego.JUGANDO) {
	            estado = nuevoEstado;
	        }

	    }

	}
	

	public boolean terminado() {

	    return estado == EstadoJuego.GAME_OVER
	            || estado == EstadoJuego.VICTORIA;
	}

	public boolean gano() {

	    return estado == EstadoJuego.VICTORIA;
	}

	public void subirSubmarino() {
		submarino.subir();
	}

	public void bajarSubmarino() {
		submarino.bajar();
	}

	public void moverSubmarinoIzquierda() {
		if (submarino.getPosicionX() - Submarino.VELOCIDAD >= -LIMITE_HORIZONTAL)
		{
			submarino.izquierda();
		}
	}

	public void moverSubmarinoDerecha() {
		if (submarino.getPosicionX() + Submarino.VELOCIDAD <= LIMITE_HORIZONTAL)
		{
			submarino.derecha();
		}
	}

	public Submarino getSubmarino() {
		return submarino;
	}

	public List<Buque> getBuquesActivos() {
		return buquesActivos;
	}

	public List<CargaProfundidad> getCargas() {
		return cargas;
	}

	public int getPuntos() {
		return puntos;
	}

	public int getVidas() {
		return vidas;
	}

	public int getNivel() {
		return nivel;
	}
	
	public EstadoJuego getEstado() {
	    return estado;
	}
}
