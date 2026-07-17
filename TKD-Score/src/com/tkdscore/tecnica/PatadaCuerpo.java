package com.tkdscore.tecnica;

/**
 * Clase PatadaCuerpo que actúa como un Componente Concreto (Concrete Component) 
 * en el patrón de diseño Decorator.
 * Representa la acción de conectar una patada directa y regular en la zona del 
 * protector de tronco (peto), otorgando una puntuación base de 2 puntos.
 */
public class PatadaCuerpo implements ITecnica {

    /**
     * Calcula los puntos base otorgados por esta técnica.
     * Según el reglamento oficial de la WT, un impacto directo al cuerpo equivale a 2 puntos.
     * * @return Valor constante de 2 puntos.
     */
    @Override
    public int calcularPuntos() {
        return 2;
    }

    /**
     * Obtiene el nombre oficial de la técnica ejecutada.
     * * @return La cadena "Patada al cuerpo".
     */
    @Override
    public String getNombre() {
        return "Patada al cuerpo";
    }
}