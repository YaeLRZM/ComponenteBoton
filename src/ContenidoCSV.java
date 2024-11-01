import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContenidoCSV {
    private List<String[]> filas;

    // Constructor
    public ContenidoCSV() {
        this.filas = new ArrayList<>();
    }

    // Método para cargar los datos desde un archivo CSV
    public void cargarDesdeArchivo(File archivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean esPrimeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (esPrimeraLinea) {
                    // Saltamos la primera línea (encabezados)
                    esPrimeraLinea = false;
                    continue;
                }
                String[] valores = linea.split(","); // Dividimos por comas
                filas.add(valores);
            }
        }
    }

    // Método para obtener las filas cargadas
    public List<String[]> obtenerFilas() {
        return this.filas;
    }
}

