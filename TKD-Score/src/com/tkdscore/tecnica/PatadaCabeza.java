package com.tkdscore.tecnica;

/**
 * Clase PatadaCabeza que actúa como un Componente Concreto (Concrete Component) 
 * en el patrón de diseño Decorator.
 * Representa la acción de conectar una patada directa en el protector de cabeza (casco),
 * la cual otorga una puntuación base fija en el reglamento de Taekwondo.
 */
public class PatadaCabeza implements ITecnica {

    /**
     * Calcula los puntos base otorgados por esta técnica.
     * Según el reglamento oficial, un impacto directo a la cabeza equivale a 3 puntos.
     * * @return Valor constante de 3 puntos.
     */
    @Override
    public int calcularPuntos() {
        return 3;
    }

    /**
     * Obtiene el nombre oficial de la técnica ejecutada.
     * * @return La cadena "Patada a la cabeza".
     */
    @Override
    public String getNombre() {
        return "Patada a la cabeza";
    }
}