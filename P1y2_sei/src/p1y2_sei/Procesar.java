/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p1y2_sei;

import com.sun.org.apache.bcel.internal.classfile.Code;
import java.io.*;
import java.util.*;
import java.util.Random;

/**
 * Clase Procesar: llamado por clase Configuracion . En la cual se encarga de
 * obtener clave, construir mapa y matriz de clave, escritura y lectura de
 * fichero Y el método fundamental de cifrar y descifrar con los métodos
 * necesario para conseguir su funcionamiento 
 * Autor: Shunya Zhan 
 * Version: 1.0
 */
public class Procesar {

    private Configuracion configuracion;
    private char[][] matriz;
    private Map<Character, String> coordenadas = new HashMap<>();
    private List<String> lineasEntrada = new ArrayList<>();
    private static final String CLAVE_POR_DEFECTO = "0123456789 ABCDEFGHIJKLMN&OPQRSTUVWXYZ.$# 5";
    private int tamanoBloque;

    /**
     * Constructor parametrizado, en el cual recibe configuracion, y se carga la
     * clave con getter
     *
     * @param configuracion
     */
    public Procesar(Configuracion configuracion) {
        this.configuracion = configuracion;
        cargarClave(configuracion.getFicheroClave());
    }

    /**
     * Ejecuta el proceso de lectura, codificación/descodificación y escritura
     * de archivos Lee el fichero de entrada especificado en la configuracion Si
     * el modo de codificacion está activado, cifra el contenido; de lo
     * contrario, lo descifra Escribe el resultado en el fichero de
     * entradarestaurada
     */
    public void ejecutar() {
        if (configuracion.isCodifica()) {
            leerFicheroEntrada(configuracion.getFicheroEntrada());
            cifrar();
            escribirFicheroSalida(configuracion.getFicheroSalida());

        } else {
            leerFicheroEntrada(configuracion.getFicheroEntrada());
            descifrar();
            escribirFicheroSalida(configuracion.getFicheroSalida()); 
        }
    }

    /**
     * Recibe el fichero de clave, si es null, se carga la clave por defecto. Y
     * si no, lee la clave desde el archivo especificado Si se lee una linea del
     * archivo, construye la matriz y el mapa con la clave leída Si no se puede
     * leer elarchivo o la linea es null, carga la clave por defecto
     *
     * @param claveFilePath
     */
    private void cargarClave(String claveFilePath) {
        if (claveFilePath == null) {
            cargarClavePorDefecto();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(claveFilePath))) {
            String line = br.readLine();
            if (line != null) {
                System.out.println("Leyendo clave: " + line);
                if (validarClave(line)) {
                    construirMatrizYMapa(line);
                } else {
                    System.out.println("La clave tiene formato incorrecto, utilizamos la clave por defecto.");
                    cargarClavePorDefecto();
                }
            } else {
                System.out.println("La clave es vacía, utilizamos la clave por defecto.");
                cargarClavePorDefecto();
            }
        } catch (IOException e) {
            System.err.println("Error leyendo el fichero de clave: " + e.getMessage());
            cargarClavePorDefecto();
        }
    }

    /**
     * Valida el formato de la clave. La clave debe tener tres partes separadas
     * por espacios: La primera parte debe ser una cadena de 10 dígitos (0-9).
     * La segunda parte debe ser una cadena de 30 caracteres del alfabeto. La
     * tercera parte debe ser un número entero entre 3 y 8 (inclusive).
     *
     * @param clave
     * @return true si la clave es válida, false en caso contrario.
     */
    private boolean validarClave(String clave) {
        String[] parts = clave.split(" ");
        if (parts.length != 3) {
            if (configuracion.isTraza()) {
                System.err.println("Formato de clave incorrecto: se esperaban 3 partes (columnas, contenido, tamaño del bloque)");
            }
            return false;
        }
        String columnas = parts[0];
        String contenido = parts[1];
        try {
            tamanoBloque = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            if (configuracion.isTraza()) {
                System.err.println("Formato de clave incorrecto: el tamaño del bloque debe ser un número entero");
            }
            return false;
        }
        if (!columnas.matches("\\d{10}")) {
            if (configuracion.isTraza()) {
                System.err.println("Formato de clave incorrecto: las columnas deben ser una cadena de 10 dígitos (0-9)");
            }
            return false;
        }
        if (contenido.length() != 30) {
            if (configuracion.isTraza()) {
                System.err.println("Formato de clave incorrecto: el contenido de la clave debe tener 30 caracteres");
            }
            return false;
        }
        if (tamanoBloque < 3 || tamanoBloque > 8) {
            if (configuracion.isTraza()) {
                System.err.println("Formato de clave incorrecto: el tamaño del bloque debe ser un número entero entre 3 y 8");
            }
            return false;
        }
        return true;
    }

    /**
     * Construye la mapa con la clave por defecto
     */
    private void cargarClavePorDefecto() {
        construirMatrizYMapa(CLAVE_POR_DEFECTO);
    }

    /**
     * Construye una matriz y un mapa de coordenadas a partir de una clave
     * proporcionada Divide la clave en partes usando el espacio como
     * delimitador Verifica si la clave tenga al menos tres partes: columnas,
     * contenido y tamaño del bloque Convierte el tamañp del bloque a un entero
     * y verifica si el contenido tenda 30 caracteres Construye una matriz de
     * 3*10 y un mapa de coordenadas a partir del contenido de la clave Si la
     * traza activada, imprime la clave cargada y la matriz construida
     *
     * @param clave
     */
    private void construirMatrizYMapa(String clave) {
        String[] parts = clave.split(" ");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Formato de clave incorrecto: se esperaban 3 partes (columnas, contenido, tamaño del bloque)");
        }

        String columnas = parts[0];
        String contenido = parts[1];
        try {
            tamanoBloque = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de clave incorrecto: el tamaño del bloque debe ser un número entero");
        }

        if (contenido.length() != 30) {
            throw new IllegalArgumentException("Formato de clave incorrecto: el contenido de la clave debe tener 30 caracteres");
        }

        matriz = new char[3][10];
        int index = 0;
        for (int k = 0; k < columnas.length(); k++) {
            char columna = columnas.charAt(k);
            int colIndex = Character.getNumericValue(columna);
            for (int i = 0; i < 3; i++) {
                matriz[i][colIndex] = contenido.charAt(index++);
                coordenadas.put(matriz[i][colIndex], (i + 1) + "" + colIndex);
            }
        }

        if (configuracion.isTraza()) {
            System.out.println("Clave cargada:");
            System.out.println("Columnas: " + columnas);
            System.out.println("Contenido: " + contenido);
            System.out.println("Tamaño bloque: " + tamanoBloque);
            System.out.println("  0 1 2 3 4 5 6 7 8 9");
            System.out.println("---------------------");
            for (int i = 0; i < 3; i++) {
                System.out.print((i + 1) + "|");
                for (int j = 0; j < 10; j++) {
                    System.out.print(matriz[i][j] + " ");
                }
                System.out.println();
            }
        }
    }

    /**
     * Lee el fichero de entrada, muestra las lineas leidas, y lo añade al
     * lineaEntrada Muestra mensaje de error si ha fallado la lectura del
     * fichero de entrada
     *
     * @param entradaFilePath
     */
    private void leerFicheroEntrada(String entradaFilePath) {
        lineasEntrada.clear(); // Limpiar la lista antes de leer nuevas líneas
        try (BufferedReader br = new BufferedReader(new FileReader(entradaFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Leyendo línea de entrada: " + line); // Mensaje de depuración
                lineasEntrada.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo el fichero de entrada: " + e.getMessage());
        }
    }

    /**
     * Escribe las líneas procesadas en un archivo de salida Si hay error,
     * captura la excepcion e imprime mensaje de error
     *
     * @param salidaFilePath
     */
    private void escribirFicheroSalida(String salidaFilePath) {
        if (lineasEntrada == null || lineasEntrada.isEmpty()) {
            System.err.println("No hay líneas para escribir en el fichero de salida.");
            return;
        }
        File file = new File(salidaFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("No se pudo crear el fichero de salida: " + e.getMessage());
                return;
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(salidaFilePath))) {
            for (int i = 0; i < lineasEntrada.size(); i++) {
                String linea = lineasEntrada.get(i);
                bw.write(linea);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error escribiendo el fichero de salida: " + e.getMessage());
        }
    }

    /**
     * Cifra las líneas de texto de la lista 'lineaEntrada' utilizando un
     * algoritmo de cifraddo basado en cooedenadas El proceso de cifrado incluye
     * los siguientes pasos: Sustituir la letra N por & Reemplaza los espacios
     * en blando con el signo '.' Elimina los caracteres no incluidos en el
     * alfabeto A-Z y los signos & y.. Obtener las coordenadas de cada letra y
     * guardarla Dividir el texto en bloques según atributo 'tamanoBloque'
     * Genera número aleatorios y modifica las coordenadas Combina los dígitos
     * de filas y columnas para formar las coordenadas cifradas Comvertir las
     * coordenadas cifradas entexto utiilizando la matriz de clave El resultado
     * cifrado se almace en la lista 'lineaEntrada' Si la configuracion de
     * trazabilidad está habilitada, se imprimen mensajes de depuración en la
     * consola Controla si se ocurre un error al convertir las coordenads a
     * números
     */
    private void cifrar() {
        List<String> resultado = new ArrayList<>();
        Random random = new Random();

        for (int l = 0; l < lineasEntrada.size(); l++) {
            String linea = lineasEntrada.get(l);
            StringBuilder coordenadasTexto = new StringBuilder();

            // 1. Sustituir la letra Ñ por &
            linea = linea.replace('Ñ', '&');

            // 2. Incluir el espacio en blanco (representado con el signo '.')
            linea = linea.replace(' ', '.');

            // 3. Eliminar los signos no incluidos en el alfabeto
            linea = linea.replaceAll("[^A-Z.&]", "");

            // 4. Obtener las coordenadas de cada letra y guardarlas
            for (int i = 0; i < linea.length(); i++) {
                char c = linea.charAt(i);
                if (coordenadas.containsKey(c)) {
                    String coord = coordenadas.get(c);
                    if (configuracion.isTraza()) {
                        System.out.println("Coordenadas de " + c + ": " + coord);
                    } // Imprimir coordenadas

                    coordenadasTexto.append(coord);
                }
            }

            // 5. Dividir el texto en bloques según el atributo 'tamanoBloque'
            List<String> bloques = dividirEnBloques(coordenadasTexto.toString(), tamanoBloque);

            if (configuracion.isTraza()) {
                System.out.println("Bloques dividido: " + bloques);
            }

            List<Long> filas = new ArrayList<>();
            List<Long> columnas = new ArrayList<>();

            for (int j = 0; j < bloques.size(); j++) {
                String bloque = bloques.get(j);
                String bloqueStr = String.valueOf(bloque);
                StringBuilder filasStr = new StringBuilder();
                StringBuilder columnasStr = new StringBuilder();

                for (int i = 0; i < bloqueStr.length(); i++) {
                    if (i % 2 == 0) {
                        filasStr.append(bloqueStr.charAt(i));
                    } else {
                        columnasStr.append(bloqueStr.charAt(i));
                    }
                }
                // Añadir un número aleatorio de 1 a 3 a la izquierda de cada número en filas
                int randomNum = random.nextInt(3) + 1;

                filasStr.insert(0, randomNum);
                // Convertir columnasStr a número entero y multiplicar por 9
                long columnasNum = Long.parseLong(columnasStr.toString());
                columnasNum *= 9;

                filas.add(Long.parseLong(filasStr.toString()));
                columnas.add(columnasNum);
            }

            if (configuracion.isTraza()) {
                System.out.println("Filas: " + filas);
                System.out.println("Columnas: " + columnas);
            }

            List<Long> coordenadaModificada = new ArrayList<>();
            for (int i = 0; i < filas.size(); i++) {
                String filasStr = String.valueOf(filas.get(i));
                String columnasStr = String.valueOf(columnas.get(i));
                StringBuilder coordenada = new StringBuilder();

                // Asegurarse de que ambas cadenas tengan la misma longitud
                int maxLength = Math.max(filasStr.length(), columnasStr.length());
                while (filasStr.length() < maxLength) {
                    filasStr = "0" + filasStr;
                }
                while (columnasStr.length() < maxLength) {
                    columnasStr = "0" + columnasStr;
                }

                // Combinar los dígitos de filas y columnas
                for (int j = 0; j < maxLength; j++) {
                    coordenada.append(filasStr.charAt(j));
                    coordenada.append(columnasStr.charAt(j));
                }

                coordenadaModificada.add(Long.parseLong(coordenada.toString()));
            }

            if (configuracion.isTraza()) {
                System.out.println("Coordenada Modificada: " + coordenadaModificada);
            }

            StringBuilder cifrado = new StringBuilder();

            for (int k = 0; k < coordenadaModificada.size(); k++) {
                Long coord = coordenadaModificada.get(k);
                String coordStr = String.valueOf(coord);

                // Dividir en pares de dígitos
                for (int i = 0; i < coordStr.length(); i += 2) {
                    int fila = Character.getNumericValue(coordStr.charAt(i)) - 1; // Ajustar índice de fila
                    int columna = Character.getNumericValue(coordStr.charAt(i + 1));

                    // Verificar que las coordenadas estén dentro de los límites de la matriz
                    if (fila >= 0 && fila < 3 && columna >= 0 && columna < 10) {
                        cifrado.append(matriz[fila][columna]);
                    } else {
                        System.err.println("Coordenadas fuera de rango: (" + fila + ", " + columna + ")");
                    }
                }
            }

            resultado.add(cifrado.toString());
            System.out.println("Texto cifrado: " + cifrado.toString()); // Mostrar el cifrado

            System.out.println("///////////////////////////////////////////////////////////////////////////////////");
        }

        System.out.println("Mostrar el resultado: ");
        lineasEntrada = resultado;
        for (String linea : lineasEntrada) {
            System.out.println(linea);
        }
        System.out.println("------------------------------------------------------------------------------------");

        if (configuracion.isTraza()) {
            System.out.println("Proceso de cifrado completado.");
            System.out.println("------------------------------------------------------------------------------------");

        }
    }

    /**
     * Divide una cadena de texto en bloque de tamaño especificado El método
     * toma una cadena de coordenadas y la divide en bloques de longitud
     * 'tamanoBloque' Si el último bloque no alcanza la longitud requerida, se
     * completa con la coordenada del signo '$'
     *
     * @param coordenadasTexto
     * @param tamanoBloque
     * @return
     */
    private List<String> dividirEnBloques(String coordenadasTexto, int tamanoBloque) {
        List<String> bloques = new ArrayList<>();
        int longitudBloque = tamanoBloque * 2; // Cada bloque tiene tamanoBloque pares de números

        String coordenadaDeSigno = coordenadas.get('$');
        for (int i = 0; i < coordenadasTexto.length(); i += longitudBloque) {
            String bloque = coordenadasTexto.substring(i, Math.min(i + longitudBloque, coordenadasTexto.length()));
            // Completar el último bloque con signo '$' si es necesario
            while (bloque.length() < longitudBloque) {
                bloque += coordenadaDeSigno;
            }
            bloques.add(bloque);
        }
        return bloques;
    }

    /**
     * Descifra las lineas de texto d ela lista 'lineasEntrada' utilizando un
     * algoritmo de descifrado basado en coordenadas El proceso de descifrado
     * incluye los siguientes pasos: Convertir cada caracter en sus coordenadas
     * coorespondientes en la matriz Separar las coordenadas en filas y columnas
     * Dividir las filas y columnas en bloques según el atributo 'tamanoBloque'
     * Procesar las filas y columnas para restaurar las coordenadas originales
     * Combinar las filas y columnas para formar las coordenadas descifradas
     * Convertir las coordenadas descifradas en texto utilizando la matriz de
     * clave El resultado descifrado se almacena en la lista 'lineaEntrada' Si
     * la configuracion de trazabilidad está habilitada, se imprimen mensajes de
     * depuracion en la consola
     */
    private void descifrar() {
        List<String> resultado = new ArrayList<>();
        for (int l = 0; l < lineasEntrada.size(); l++) {
            String linea = lineasEntrada.get(l);
            if (configuracion.isTraza()) {
                System.out.println("Descifrando línea: " + linea);
            }
            StringBuilder coordenadasTexto = new StringBuilder();

            // 1. Convertir cada carácter en sus coordenadas correspondientes
            for (int k = 0; k < linea.length(); k++) {
                char c = linea.charAt(k);
                boolean found = false;
                for (int i = 0; i < matriz.length; i++) {
                    for (int j = 0; j < matriz[i].length; j++) {
                        if (matriz[i][j] == c) {
                            coordenadasTexto.append(i + 1).append(j);
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }

            if (configuracion.isTraza()) {
                System.out.println("Coordenadas generadas: " + coordenadasTexto.toString());
            }
            StringBuilder filas = new StringBuilder();
            StringBuilder columnas = new StringBuilder();
            for (int i = 0; i < coordenadasTexto.length(); i++) {
                if (i % 2 == 0) {
                    filas.append(coordenadasTexto.charAt(i));
                } else {
                    columnas.append(coordenadasTexto.charAt(i));
                }
            }

            List<String> bloquesFilas = dividirEnBloquesD(filas.toString(), tamanoBloque + 1);
            List<String> bloquesColumnas = dividirEnBloquesD(columnas.toString(), tamanoBloque + 1);

            if (configuracion.isTraza()) {
                System.out.println("Filas en bloques: " + bloquesFilas);

                System.out.println("Columnas en bloques: " + bloquesColumnas);
            }
            List<String> filasProcesadas = procesarFilas(bloquesFilas);
            List<Long> columnasProcesadas = procesarColumnas(bloquesColumnas);

            if (configuracion.isTraza()) {
                System.out.println("Filas procesadas: " + filasProcesadas);

                System.out.println("Columnas procesadas: " + columnasProcesadas);
            }
            StringBuilder descifrado = combinarFilasColumnas(filasProcesadas, columnasProcesadas, tamanoBloque);
            if (configuracion.isTraza()) {
                System.out.println("Coordenada Restaurada: " + descifrado.toString());
            }

            StringBuilder textoDescifrado = obtenerTextoDescifrado(descifrado.toString(), matriz);
            System.out.println("Texto descifrado: " + textoDescifrado.toString());

            resultado.add(textoDescifrado.toString());
            System.out.println("///////////////////////////////////////////////////////////////////////////////////");

        }
        System.out.println("Mostrar el resultado: ");
        lineasEntrada = resultado;
        for (String linea : lineasEntrada) {
            System.out.println(linea);
        }
        System.out.println("------------------------------------------------------------------------------------");

        if (configuracion.isTraza()) {
            System.out.println("Proceso de descifrado completado.");
            System.out.println("------------------------------------------------------------------------------------");

        }

    }

    /**
     * Convierte una cadena de texto descifrada en el texto orifinal utilizando
     * una matriz de clave El proceso incluye los siguientes pasos: Eliminar los
     * espacios en blanco de la cadena descifrada Convertir cada par de dígitos
     * en coordenadas de la matriz para obtener el carácter correspondiente
     * Ignorar el signo de relleno ('$') durante la conversion Revertir los
     * cambios de cifrado, como reemplazar '&' por 'Ñ' y '.' por espacio
     *
     * @param descifradoTexto
     * @param matriz
     * @return
     */
    private static StringBuilder obtenerTextoDescifrado(String descifradoTexto, char[][] matriz) {
        StringBuilder textoDescifrado = new StringBuilder();

        // Eliminar espacios en blanco
        descifradoTexto = descifradoTexto.replace(" ", "");

        for (int i = 0; i < descifradoTexto.length(); i += 2) {
            int fila = Character.getNumericValue(descifradoTexto.charAt(i)) - 1; // Ajustar índice de fila
            int columna = Character.getNumericValue(descifradoTexto.charAt(i + 1));

            // Verificar que las coordenadas estén dentro de los límites de la matriz
            if (fila >= 0 && fila < 3 && columna >= 0 && columna < 10) {
                char caracter = matriz[fila][columna];
                if (caracter != '$') { // Ignorar el signo de relleno
                    textoDescifrado.append(caracter);
                }
            } else {
                System.err.println("Coordenadas fuera de rango: (" + fila + ", " + columna + ")");
            }
        }

        // Revertir los cambios de cifrado
        String textoFinal = textoDescifrado.toString()
                .replace('&', 'Ñ')
                .replace('.', ' ');

        return new StringBuilder(textoFinal);
    }

    /**
     * Combina las filas y columnas procesadas en una cadena de texto descifrada
     * Rellenar las filas y columnas con ceros a la izquierda para alcanzar el
     * tamaño de bloque Combinar los digitos de las filas y columnas en pares
     * para formar la caddena descifrada
     *
     * @param filas
     * @param columnas
     * @param tamanoBloque
     * @return
     */
    private static StringBuilder combinarFilasColumnas(List<String> filas, List<Long> columnas, int tamanoBloque) {
        StringBuilder descifrado = new StringBuilder();

        for (int i = 0; i < filas.size(); i++) {
            String fila = filas.get(i);
            String columna = String.valueOf(columnas.get(i));

            // Rellenar con ceros a la izquierda para alcanzar el tamaño de bloque
            while (fila.length() < tamanoBloque) {
                fila = "0" + fila;
            }
            while (columna.length() < tamanoBloque) {
                columna = "0" + columna;
            }

            // Combinar los dígitos de filas y columnas
            for (int j = 0; j < tamanoBloque; j++) {
                descifrado.append(fila.charAt(j));
                descifrado.append(columna.charAt(j));
            }
        }

        return descifrado;
    }

    /**
     * Procesa una lista de bloques de filas eliminando el primer digito de cada
     * bloque El metodo toma una lista de bloques de filas y elimina el primer
     * digito de cada bloque, devolviendo una nueva lista con la filas
     * procesadas
     *
     * @param bloquesFilas
     * @return
     */
    private static List<String> procesarFilas(List<String> bloquesFilas) {
        List<String> filasProcesadas = new ArrayList<>();
        for (int i = 0; i < bloquesFilas.size(); i++) {
            String bloque = bloquesFilas.get(i);
            // Eliminar el primer dígito de cada bloque
            filasProcesadas.add(bloque.substring(1));
        }

        return filasProcesadas;
    }

    /**
     * Procesa una lista de bloques de columnas dividiendo cada numero por 9 El
     * metodo toma una lista de bloques de columnass, convierte cada bloque en
     * un numero largo, lo divide por 9 y devuelve una nueva lista con los
     * resultados procesados
     *
     * @param bloquesColumnas
     * @return
     */
    private static List<Long> procesarColumnas(List<String> bloquesColumnas) {
        List<Long> columnasProcesadas = new ArrayList<>();
        for (int i = 0; i < bloquesColumnas.size(); i++) {
            String bloque = bloquesColumnas.get(i);
            // Dividir cada número por 9
            long numero = Long.parseLong(bloque);
            columnasProcesadas.add(numero / 9);
        }

        return columnasProcesadas;
    }

    /**
     * Divide una cadena de texto en bloques de tamaño especificado El método
     * toma unacadena de texto y la divide en bloques de longitud 'tamanoBloque'
     * Si el último bloque no alcanza la longitud requerida, se incluye tal cual
     *
     * @param texto
     * @param tamanoBloque
     * @return
     */
    private static List<String> dividirEnBloquesD(String texto, int tamanoBloque) {
        List<String> bloques = new ArrayList<>();
        for (int i = 0; i < texto.length(); i += tamanoBloque) {
            bloques.add(texto.substring(i, Math.min(i + tamanoBloque, texto.length())));
        }
        return bloques;
    }

}
