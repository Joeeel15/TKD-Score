package com.tkdscore.tecnica;

/**
 * Clase Punetazo que actúa como un Componente Concreto (Concrete Component) 
 * en el patrón de diseño Decorator.
 * Representa la acción de conectar un golpe de puño directo y contundente en el 
 * protector de tronco (peto), sumando una puntuación base de 1 punto.
 */
public class Punetazo implements ITecnica {

    /**
     * Calcula los puntos base otorgados por esta técnica.
     * Según el reglamento oficial, un puñetazo válido y con la fuerza requerida equivale a 1 punto.
     * * @return Valor constante de 1 punto.
     */
    @Override
    public int calcularPuntos() {
        return 1;
    }

    /**
     * Obtiene el nombre oficial de la técnica ejecutada.
     * * @return La cadena "Punetazo".
     */
    @Override
    public String getNombre() {
        return "Punetazo";
    }
}