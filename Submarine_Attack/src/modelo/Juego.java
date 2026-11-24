package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Juego {
	private static final Random RANDOM = new Random();
	private Submarino submarino;
	private int puntos = 0;
	private static final int BUQUES_ACTIVOS_MAX = 3;
	private final double LIMITE_HORIZONTAL;
	
	private double velocidadBuques = 5;
	private double velocidadCaidaCargas = 5;

	private List<Buque> buquesActivos = new ArrayList<Buque>();
	private int contadorBuquesTotales = 0;

	private List<CargaProfundidad> cargas = new ArrayList<CargaProfundidad>();
	
	public Juego(double anchoCampo) {
		LIMITE_HORIZONTAL = anchoCampo;
		inicializarJuego();
	}
	
	private void inicializarJuego() {
		submarino = new Submarino(0, 500);
		agregarBuques();
	}
	
	private Buque crearBuque() {
		boolean apareceIzquierda = RANDOM.nextBoolean();
		Buque buque;
		int pasadas = RANDOM.nextInt(1,4);
		
		if (apareceIzquierda)
		{
			buque = new Buque(-LIMITE_HORIZONTAL, true, velocidadBuques, velocidadCaidaCargas, pasadas );
		}
		else
		{
			buque = new Buque(LIMITE_HORIZONTAL, false, velocidadBuques, velocidadCaidaCargas, pasadas );
		}
		return buque;
	}
	
	private void agregarBuques() {

		while(buquesActivos.size() < BUQUES_ACTIVOS_MAX && contadorBuquesTotales < 12)
		{
			Buque nuevoBuque = crearBuque();
			buquesActivos.add(nuevoBuque);
			contadorBuquesTotales++;
		}
			
	}
	
	private void actualizarBuques() {
		int cantBuquesActivos = buquesActivos.size();
		for (int i = cantBuquesActivos - 1; i >= 0; i--)
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
		
		agregarBuques();
	}
	
	
	public void lanzarCargaAleatoria() {
		if (buquesActivos.isEmpty())
		{
			return;
		}
		
		int indice = RANDOM.nextInt(buquesActivos.size());
		Buque buque = buquesActivos.get(indice);
		CargaProfundidad nuevaCarga = buque.lanzarCarga();
		cargas.add(nuevaCarga);

	}
	
	private void actualizarCargas() {
		int cantCargas = cargas.size();
		for (int i = cantCargas - 1; i >= 0; i--)
		{
			CargaProfundidad cargaActual = cargas.get(i);
			cargaActual.moverse();
			
			if (cargaActual.getExploto() )
			{
				double distancia = cargaActual.distanciaA(submarino);
				
				if (distancia < 10)
				{
					//
				}
				else if (distancia >= 10 && distancia <50)
				{
					submarino.recibirDanio(50);
				}
				else if (distancia >= 50 && distancia <= 100)
				{
					submarino.recibirDanio(30);
					puntos = puntos + 10;
				}
				else
				{
					puntos = puntos + 30;
				}
				
				cargas.remove(i);
				
			}
		}
	}
	
	public void actualizarJuego() {
	    actualizarBuques();
	    actualizarCargas();
	}

}


