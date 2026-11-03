package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Juego {
	private static final Random RANDOM = new Random();
	private Submarino submarino;
	private static final int BUQUES_ACTIVOS_MAX = 3;
	private final double ANCHO_CAMPO;
	
	private double velocidadBuques = 5;
	private double velocidadCaidaCargas = 5;

	private List<Buque> buquesActivos = new ArrayList<Buque>();
	private int contadorBuquesTotales = 0;

	private List<CargaProfundidad> cargas = new ArrayList<CargaProfundidad>();
	
	public Juego(double anchoCampo) {
		ANCHO_CAMPO = anchoCampo;
		inicializarJuego();
	}
	
	private void inicializarJuego() {
		Buque B1 = crearBuque();
		Buque B2 = crearBuque();
		Buque B3 = crearBuque();
		
		contadorBuquesTotales += 3;
		
		buquesActivos.add(B1);
		buquesActivos.add(B2);
		buquesActivos.add(B3);
	}
	
	private Buque crearBuque() {
		boolean apareceIzquierda = RANDOM.nextBoolean();
		Buque buque;
		
		if (apareceIzquierda)
		{
			buque = new Buque(-ANCHO_CAMPO, true, velocidadBuques, velocidadCaidaCargas );
		}
		else
		{
			buque = new Buque(ANCHO_CAMPO, false, velocidadBuques, velocidadCaidaCargas );
		}
		return buque;
	}
	
	private void actualizarBuques() {
		
	}
	
	private void agregarBuques() {
			
		}
}


