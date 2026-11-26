package persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class PersistenciaDatos {
    private static final Path RUTA = Path.of("records.txt");

    public int leerRecord(String nombreJugador) {
        HashMap<String, Integer> records = leerTodos();
        if (records.containsKey(nombreJugador)) {
            return records.get(nombreJugador);
        }
        return 0;
    }

    public void guardarRecord(String nombreJugador, int record) {
        HashMap<String, Integer> records = leerTodos();
        records.put(nombreJugador, record);

        String contenido = "";
        for (String nombre : records.keySet()) {
            contenido = contenido + nombre + ";" + records.get(nombre) + "\n";
        }

        try {
            Files.writeString(RUTA, contenido);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el récord: " + e.getMessage());
        }
    }

    private HashMap<String, Integer> leerTodos() {
        HashMap<String, Integer> records = new HashMap<String, Integer>();
        try {
            List<String> lineas = Files.readAllLines(RUTA);
            for (String linea : lineas) {
                String[] partes = linea.split(";");
                if (partes.length == 2) {
                    records.put(partes[0], Integer.parseInt(partes[1].trim()));
                }
            }
        } catch (IOException e) {
            return records;
        } catch (NumberFormatException e) {
            System.out.println("El archivo de récords tiene un dato inválido: " + e.getMessage());
        }
        return records;
    }
}
