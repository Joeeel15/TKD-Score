package com.tkdscore.memento;

/**
 * Clase MarcadorMemento que actúa como el Memento en el patrón de diseño homónimo.
 * Representa una "captura de pantalla" o "fotografía" inmutable del estado del marcador 
 * antes de que se realice un nuevo cambio de puntaje.
 */
public class MarcadorMemento {

    // Atributos marcados como 'final' para garantizar la inmutabilidad del estado guardado
    private final int puntosAzul;      // Almacena la puntuación que tenía el atleta AZUL
    private final int puntosRojo;      // Almacena la puntuación que tenía el atleta ROJO
    private final Integer idPunto;     // ID del registro del punto asociado en la Base de Datos (permite anulación)
    private final int idCombate;       // ID del combate en el que se generó este estado histórico

    /**
     * Constructor de la clase MarcadorMemento.
     * Captura y encapsula los datos exactos del estado del marcador.
     * * @param puntosAzul Puntaje acumulado del competidor azul antes de la acción actual.
     * @param puntosRojo Puntaje acumulado del competidor rojo antes de la acción actual.
     * @param idPunto    Identificador único de la base de datos para la acción realizada.
     * @param idCombate  Identificador del combate en curso.
     */
    public MarcadorMemento(int puntosAzul, int puntosRojo, Integer idPunto, int idCombate) {
        this.puntosAzul = puntosAzul;
        this.puntosRojo = puntosRojo;
        this.idPunto = idPunto;
        this.idCombate = idCombate;
    }

    /**
     * Obtiene los puntos del atleta Azul almacenados en este memento.
     * @return Puntos del competidor azul.
     */
    public int getPuntosAzul() {
        return puntosAzul;
    }

    /**
     * Obtiene los puntos del atleta Rojo almacenados en este memento.
     * @return Puntos del competidor rojo.
     */
    public int getPuntosRojo() {
        return puntosRojo;
    }

    /**
     * Obtiene el identificador del punto asociado a este estado en la base de datos.
     * Útil para buscar dicho registro en la tabla "punto" y cambiar su estado o anularlo.
     * @return ID del punto (puede ser null si el punto no se persistió).
     */
    public Integer getIdPunto() {
        return idPunto;
    }

    /**
     * Obtiene el identificador único del combate asociado a este estado histórico.
     * @return ID del combate.
     */
    public int getIdCombate() {
        return idCombate;
    }
}