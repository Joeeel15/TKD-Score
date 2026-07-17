package com.tkdscore.tecnica;

/**
 * Clase PatadaGiratoria que actúa como un Componente Concreto (Concrete Component) 
 * en el patrón de diseño Decorator.
 * Representa la acción de conectar una patada con rotación (giro) dirigida a la zona 
 * del protector de tronco (peto), otorgando una puntuación base regulada de 4 puntos.
 */
public class PatadaGiratoria implements ITecnica {

    /**
     * Calcula los puntos base otorgados por esta técnica en el reglamento oficial.
     * Una patada con giro al cuerpo equivale a una puntuación base de 4 puntos.
     * * @return Valor constante de 4 puntos.
     */
    @Override
    public int calcularPuntos() {
        return 4;
    }

    /**
     * Obtiene el nombre oficial de la técnica ejecutada.
     * * @return La cadena "Patada giratoria".
     */
    @Override
    public String getNombre() {
        return "Patada giratoria";
    }
}