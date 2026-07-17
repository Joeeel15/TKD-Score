package com.tkdscore.tecnica;

/**
 * Clase TecnicaFactory que implementa el patrón de diseño Factory (Fábrica Simple).
 * Se encarga de centralizar la lógica de instanciación de los diversos objetos que 
 * implementan la interfaz 'ITecnica'. Esto desacopla a los clientes (como la interfaz gráfica) 
 * de tener que conocer las clases concretas de las técnicas.
 */
public class TecnicaFactory {

    /**
     * Método fábrica estático que crea y retorna una instancia concreta de una técnica 
     * basándose en una cadena de texto (String) identificadora.
     * * @param tipo Nombre de la técnica a instanciar (no es sensible a mayúsculas/minúsculas).
     * @return Una instancia que implementa la interfaz ITecnica.
     * @throws IllegalArgumentException si el tipo de técnica proporcionado no coincide con ninguna opción válida.
     */
    public static ITecnica crearTecnica(String tipo) {
        // Evaluamos el tipo de técnica convirtiéndolo a mayúsculas para evitar errores por diferencias de escritura
        switch (tipo.toUpperCase()) {
            case "PUNETAZO":
                // Retorna una nueva instancia de la técnica básica de puño (1 punto)
                return new Punetazo();
                
            case "PATADA_CUERPO":
                // Retorna una nueva instancia de la patada regular al peto (2 puntos)
                return new PatadaCuerpo();
                
            case "PATADA_CABEZA":
                // Retorna una nueva instancia de la patada directa al casco (3 puntos)
                return new PatadaCabeza();
                
            case "PATADA_GIRATORIA":
                // Retorna una nueva instancia de la patada con rotación al peto (4 puntos)
                return new PatadaGiratoria();
                
            default:
                // Si la cadena de texto no coincide con ninguna técnica soportada, lanzamos una excepción clara
                throw new IllegalArgumentException("Tipo de tecnica no reconocido: " + tipo);
        }
    }
}