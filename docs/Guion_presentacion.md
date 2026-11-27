# Submarine Attack — Guion de la presentación

Karo: diapositivas 1 a 8. Day: diapositivas 9 a 13. El mismo texto está en las notas del orador de cada diapositiva.

## 1. Portada — Karo

Hola, somos Carolina y Dayana y les vamos a presentar Submarine Attack, nuestro trabajo práctico de Paradigma Orientado a Objetos.

Nos dividimos así: yo presento el juego, el diseño general con el UML y las clases que coordinan todo: Juego, el controlador, la vista y la persistencia. Después Dayana explica las clases de los objetos del juego (el submarino, los barcos y las cargas), los supuestos que tomamos y hace la demo.

Como el profe nos pidió navegar el código, en cada diapositiva abajo dice en qué archivo y método está lo que contamos, para abrirlo en el momento.

## 2. El juego — Karo

El jugador mueve el submarino con las flechas, a izquierda y derecha y entre los 300 y los 800 metros de profundidad.

Los barcos vienen en series de 12, nunca hay más de 3 en pantalla, y cada uno aparece por un costado al azar. Tiran cargas que caen en línea recta y explotan a una profundidad al azar entre 300 y 700 metros.

Según a qué distancia explota la carga, ganamos puntos o perdemos salud: a más de 100 metros sumamos 30 puntos, entre 50 y 100 sumamos 10 y perdemos 30% de salud, entre 10 y 50 perdemos 50%, y a menos de 10, o si la tocamos, perdemos una vida.

Si sobrevivimos a la serie pasamos de nivel, que es un 20% más rápido. Al superar los 3 niveles ganamos. Cada nivel suma 200 puntos y cada 500 puntos hay una vida extra.

Todos estos números están como constantes al principio de la clase Juego, así el código se lee como el enunciado.

## 3. Separamos el juego en capas — Karo

Organizamos el código en paquetes según la responsabilidad, siguiendo modelo, vista y controlador.

El modelo tiene las reglas del juego y no sabe nada de la interfaz. Gracias a esa separación podemos cambiar las reglas sin tocar la interfaz, y cambiar o rediseñar la interfaz sin tocar las reglas.

La vista solo muestra los datos. El controlador es el que recibe los eventos, le pide cambios al modelo y actualiza la vista.

Y aparte separamos la persistencia: una clase que solo se ocupa de leer y guardar el récord en un archivo.

## 4. Diagrama de clases — Karo

Este es el diagrama de clases del modelo, el controlador y la persistencia.

Arriba está el ControladorJuego, que conoce al Juego y a PersistenciaDatos.

Juego es la clase central: tiene un Submarino, de 0 a 3 buques activos y una lista de cargas, y un estado, que es un enum.

Submarino, Buque y CargaProfundidad heredan de la clase abstracta ObjetoJuego, que tiene la posición y el cálculo de distancia. Buque y CargaProfundidad además implementan la interfaz EntidadMovil, porque se mueven solos en cada paso.

Las relaciones con flecha llena son asociaciones, la flecha con triángulo es herencia y la punteada con triángulo es implementar una interfaz.

## 5. Juego: las reglas en cada paso — Karo

Juego es la clase que aplica las reglas. Cada 16 milisegundos el controlador llama a actualizarJuego, que hace estos cinco pasos en orden: si hay lugar agrega un buque, cada cierta cantidad de pasos un buque al azar tira una carga, mueve los buques, mueve las cargas y resuelve las explosiones, y si terminó la serie sube de nivel o, si era el nivel 3, ganamos.

Además el juego funciona como una máquina de estados. El estado es un enum con cinco valores: INICIO, JUGANDO, PAUSADO, GAME_OVER y VICTORIA. El método cambiarEstado solo permite los cambios válidos: de inicio a jugando, de jugando a pausado y vuelta con la tecla P, y de jugando a game over o victoria. actualizarJuego solo avanza si el estado es JUGANDO, así en pausa todo queda quieto.

Usamos un enum porque limita los valores a una lista fija: no se puede asignar un estado que no existe.

## 6. ControladorJuego: recibe eventos y coordina — Karo

El controlador implementa ActionListener y KeyListener: se registra como oyente del temporizador, de los botones y del teclado. Esas son sus entradas, a la izquierda.

El Timer de Swing dispara un evento cada 16 milisegundos, unas 60 veces por segundo. En cada uno, el método paso le pide al juego que avance y actualiza la pantalla. Los botones llevan de una pantalla a otra, y el teclado mueve el submarino o pausa con la P.

A la derecha están sus salidas. Además de avanzar el juego y actualizar el HUD, el controlador detecta dos eventos: guarda la salud, las vidas y el nivel de antes del paso y los compara con los de después. Si bajó la salud, actualiza la barra de vida y activa el efecto de fading; así el indicador de vida se refresca por evento, como pide el enunciado. Y si cambió el nivel, guarda el récord automáticamente.

## 7. La vista: pantallas y dibujo — Karo

La vista la armamos con el diseñador de formularios de IntelliJ. VentanaPrincipal tiene un CardLayout, que apila las pantallas como cartas y muestra una a la vez: inicio de sesión, menú, juego, instrucciones y fin del juego. El menú tiene una imagen de fondo que dibuja PanelMenu.

En la pantalla de juego, arriba está el HUD con las vidas, la barra de salud, el puntaje y el nivel, y al centro está PanelJuego.

PanelJuego redefine paintComponent: pinta el cielo y recorre las listas del juego dibujando cada barco, carga y el submarino con drawImage. Convierte los metros del modelo en píxeles con una regla de tres. En cada paso el controlador llama a repaint y se dibuja todo de nuevo en las posiciones nuevas, como los cuadros de un dibujo animado.

Elegimos dibujar en vez de una grilla de labels porque las posiciones del modelo son continuas, en metros, y así el movimiento queda fluido y las distancias exactas.

El fading usa otro Timer, de 50 milisegundos, que va cambiando la transparencia del submarino.

## 8. Persistencia: el récord de cada jugador — Karo

Al empezar, el juego pide el nombre del jugador. El récord de cada jugador se guarda en el archivo records.txt, una línea por jugador con el formato nombre punto y coma récord.

Para leerlo, PersistenciaDatos lee todas las líneas, separa cada una en nombre y récord con split, y las guarda en un HashMap donde la clave es el nombre y el valor el récord. Usamos un HashMap porque necesitamos buscar el récord directamente por el nombre y no nos importa el orden.

El guardado es automático: cuando el controlador detecta que cambió el nivel, y también al terminar la partida. Solo escribe si el puntaje superó el récord.

Los errores se manejan con try/catch: si el archivo no existe, la primera vez, el récord vale 0; si hay un dato inválido, se informa y el juego sigue.

Con esto termino mi parte, le paso a Day.

## 9. ObjetoJuego y EntidadMovil — Day

Submarino, buques y cargas comparten que tienen una posición y, además, necesitan calcular distancias. Por eso hicimos la clase ObjetoJuego: tiene la posición en x y la profundidad, y el método distanciaA, que calcula la distancia entre dos objetos con la fórmula de distancia entre dos puntos. Así lo escribimos una sola vez en vez de tres.

Es abstracta porque en el juego no existe un objeto genérico: siempre es un submarino, un barco o una carga. Los setters de posición son protected, para que solo las subclases puedan mover un objeto.

EntidadMovil es una interfaz con un solo método, moverse. La implementan Buque y CargaProfundidad, y cada una lo redefine a su manera: el barco avanza en horizontal y la carga cae en vertical. Eso es polimorfismo: el mismo mensaje produce comportamientos distintos según el objeto.

## 10. Submarino — Day

El submarino lo mueve el jugador con las flechas, a través de los métodos subir, bajar, izquierda y derecha.

Para que no salga del rango de 300 a 800 metros, subir y bajar pasan por acotarProfundidad, que no deja que la profundidad salga de esos límites. Y Juego controla que no salga por los costados.

También maneja su salud: recibirDanio le resta el porcentaje y nunca la deja bajar de 0, estaDestruido avisa cuando llegó a 0, y restaurarSalud la vuelve al 100% cuando perdemos una vida.

Todo eso es encapsulamiento: los atributos son privados y solo cambian a través de métodos que respetan las reglas.

## 11. Buque y CargaProfundidad — Day

El buque navega siempre a profundidad cero y aparece por la izquierda o la derecha, al azar. Cada uno da una cantidad aleatoria de pasadas, entre 1 y 3: cuando sale de la pantalla, completarPasada le resta una y lo da vuelta, y cuando no le quedan más, se retira.

Para disparar, el método lanzarCarga crea una carga en la posición del barco. Es el patrón Creador: el barco crea la carga porque tiene los datos para hacerlo, su posición y la velocidad de caída.

La carga, al crearse, elige al azar a qué profundidad va a explotar, entre 300 y 700 metros. Cae en línea recta y explota al llegar a esa profundidad, o antes si toca al submarino. Cuando explota se muestra la imagen de explosión un momento.

## 12. Supuestos y decisiones — Day

Donde el enunciado no especificaba algo, tomamos decisiones.

Las pasadas: el enunciado dice que cada barco da una cantidad aleatoria de pasadas, pero no dice cuántas como máximo, así que elegimos entre 1 y 3.

El lanzamiento: el enunciado permite liberar las cargas con un temporizador o según la posición del barco. Elegimos el temporizador: cada cierta cantidad de pasos, un barco elegido al azar tira una carga.

El ritmo: aparece un barco cada 2 segundos, siempre que haya menos de 3 en pantalla, y se lanza una carga cada 80 pasos en el nivel 1, un 20% más seguido en cada nivel.

Las velocidades iniciales las elegimos probando el juego, según lo que nos resultó más cómodo. El aumento del 20% por nivel sí lo fija el enunciado.

Y cuando perdemos una vida, la salud vuelve al 100%.

## 13. Demo — Day

Ahora vamos a ver el juego funcionando.

Primero ingresamos un nombre, que es con el que se guarda el récord. Llegamos al menú, que tiene la imagen de fondo, y podemos ver las instrucciones.

Al jugar, arriba vemos las vidas, la barra de salud, el puntaje y el nivel. Cuando una carga explota cerca, la barra baja y el submarino hace el efecto de fading. Si una carga nos toca, perdemos una vida.

Con la P pausamos y con la P seguimos.

Al terminar, la pantalla final muestra Ganaste en dorado o Game Over en rojo, con el puntaje y el récord del jugador, que queda guardado en records.txt.
