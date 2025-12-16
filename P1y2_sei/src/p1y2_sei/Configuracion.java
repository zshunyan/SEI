/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p1y2_sei;

	import java.io.*;
	
	/**
	 * Clase Configuracion: es llamado por clase Main. Principalmente se encarga la lectura del fichero config.txt
	 * 		Configura los atributos según el fichero config.txt
	 * Autor: Shunya Zhan
	 * Version: 1.0
	 */
	
	public class Configuracion {
	    private String configFilePath;
	    private boolean codifica = true;
	    private boolean traza = true;
	    private String ficheroEntrada = "entrada.txt";
	    private String ficheroSalida = "salida.txt";
	    private String ficheroClave = null;
	    
		/**
		* Constructor parametrizado que recibe configFilePath
		* @param configFilePath
		*/
	    public Configuracion(String configFilePath) {
	        this.configFilePath = configFilePath;
	    }
		/**
		 * Lee y procesa un archivo de configuración línea por línea.
		 * Llama al procesarLinea para procesar cada línea leída
		 * Si hay error al leer el archivo, se muestra mensaje de error.
		 */
            public void procesarConfiguracion() {
                try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        procesarLinea(line);
                    }
                } catch (IOException e) {
                    System.err.println("Error leyendo el archivo de configuración: " + e.getMessage());
                }
            }
		/**
		 * Procesa una línea del archivo de configuracion
		 * Si la línea empieza con '#', se trata de un comentario y, si la traza activado se imprime
		 * Si la línea comienza con '&', se interpreta como un comando y se ejecuta la acción correspondiente.
		 * Si la línea comienza con '@', se interpreta como una bandera y se ajusta la configuración correspondiente.
		 * 
		 * @param line
		 */
	    private void procesarLinea(String line) {
	        if (line.startsWith("#")) {
	            if (traza) System.out.println("Comentario: " + line);
	        } else if (line.startsWith("&")) {
	            String[] parts = line.split(" ");
	            switch (parts[1]) {
	                case "clave":
	                    ficheroClave = parts[2];
	                    break;
	                case "ficheroentrada":
	                    ficheroEntrada = parts[2];
	                    break;
	                case "ficherosalida":
	                    ficheroSalida = parts[2];
	                    break;
	                case "chase":
	                    ejecutarChase();
	                    break;
	                default:
	                    System.err.println("Comando desconocido: " + parts[1]);
	            }
	        } else if (line.startsWith("@")) {
	            String[] parts = line.split(" ");
	            switch (parts[1]) {
	                case "codifica":
	                    codifica = parts[2].equalsIgnoreCase("ON");
	                    if (codifica) {
	                        System.out.println("Modo codificación: ON");
	                    } else {
	                        System.out.println("Modo codificación: OFF");
	                    }
	                    break;
	                case "traza":
	                    traza = parts[2].equalsIgnoreCase("ON");
	                    if (traza) {
	                        System.out.println("Modo traza: ON");
	                    } else {
	                        System.out.println("Modo traza: OFF");
	                    }
	                    break;
	                default:
	                    System.err.println("Bandera desconocida: " + parts[1]);
	            }
	        }
	    }
		
		/**
		 * Crea una instancia de la clase Procesar y ejecuta su método
		 */
	    private void ejecutarChase() {
			Procesar procesar=new Procesar(this);
			procesar.ejecutar();
		}
	
	    /**
	     * Devuelve el estado del modo de codificación.
	     * @return {@code true} si el modo de codificación está activado, {@code false} en caso contrario.
	     */
	    public boolean isCodifica() {
	        return codifica;
	    }

	    /**
	     * Devuelve el estado del modo de traza.
	     *
	     * @return {@code true} si el modo de traza está activado, {@code false} en caso contrario.
	     */
	    public boolean isTraza() {
	        return traza;
	    }

	    /**
	     * Devuelve la ruta del fichero de entrada.
	     *
	     * @return la ruta del fichero de entrada.
	     */
	    public String getFicheroEntrada() {
	        return ficheroEntrada;
	    }

	    /**
	     * Devuelve la ruta del fichero de salida.
	     *
	     * @return la ruta del fichero de salida.
	     */
	    public String getFicheroSalida() {
	        return ficheroSalida;
	    }

	    /**
	     * Devuelve la ruta del fichero clave.
	     *
	     * @return la ruta del fichero clave.
	     */
	    public String getFicheroClave() {
	        return ficheroClave;
	    }

	}
	

