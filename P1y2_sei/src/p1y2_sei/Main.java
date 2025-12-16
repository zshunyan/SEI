package p1y2_sei;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

	/**
	 * Clase Main: principalmente llama a otras clases, y comprueba los argumentos necesarios para ejecutar
	 * Autor: Shunya Zhan
	 * Version: 1.0
	 */

public class Main {
    public static void main(String[] args) {
    	System.out.println("Mi primer programa");
    	System.out.println("14/10/2024 22:33");
        if (args.length == 0) {
            System.out.println("Este programa no tiene argumento");
            return;
        }
        switch (args[0]) {
            case "-h":
                mostrarFicheroAyuda("ayuda.txt");
                break;
            case "-f":
                if(args.length<2){
                    System.out.println("Uso: java -jar P1y2_sei.jar -f <config-file>");
                    
                }   
                String configFilePath = args[1];
                procesarFicheroConfiguracion(configFilePath);
                break;
            default:
                System.out.println("Argumento inválido");
                mostrarSintaxis();
                break;
        }
        
       
    }

    /**
     * Muestra el contenido del fichero de ayuda
     *  Verifica si el fichero de ayuda existe. Si no existe, muestra un mensaje indicando que es necesario tener el fichero ayuda.txt
     *  Si el fichero existe, lee y muestra su contenido. Si hay error, muestra mensaje de error.
     * @param filePath 
     */
    private static void mostrarFicheroAyuda(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.out.println("El fichero ayuda.txt no existe. Es necesario tener el fichero ayuda.txt para poder ejecutar la opción -h.");
            return;
        }

        try {
            String contenido = new String(Files.readAllBytes(path));
            System.out.println(contenido);
        } catch (IOException e) {
            System.out.println("Error al leer el fichero de ayuda: " + e.getMessage());
        }
    }

    /**
     * Procesa el fichero de configuracion.
     *  Crea instancia Configuracion utilizando la configFilePath proporcionada.
     *  Luego llama el método procesarConfiguracion para procesar el fichero.
     *  Crea instancia de Procesar y utiliza su método ejecutar.
     * @param configFilePath 
     */
    private static void procesarFicheroConfiguracion(String configFilePath) {
        Configuracion configuracion = new Configuracion(configFilePath);
        configuracion.procesarConfiguracion();

        Procesar procesar = new Procesar(configuracion);
        procesar.ejecutar();
    }

    /**
     * Muestra la sintaxist correcta del programa
     * Imprime en la consola la sintaxis correcta del programa y una descripcion de los argumentos que puede recibir
     */
   private static void mostrarSintaxis() {
        System.out.println("P1y2_si2024 [-f fichero] | [-h]");
        System.out.println("- El argumento asociado a -f será un fichero de texto con la configuración bajo la");
        System.out.println("  cual ha de funcionar el programa. En este caso, este fichero de configuración");
        System.out.println("  seguirá la especificación de configuración que se indica en el sub-apartado de");
        System.out.println("  fichero de configuración, que se enuncia más adelante en este documento.");
        System.out.println("- El argumento -h indica ayuda y hará que el programa informe al usuario de cuáles");
        System.out.println("  son sus posibilidades respecto al contenido y formato del fichero de");
        System.out.println("  configuración. Una vez mostrada esa información el programa termina.");
        System.out.println("- En el caso de que NO venga ninguno de los dos argumentos el programa informará");
        System.out.println("  de cuál es su sintaxis.");
    }
        
}
