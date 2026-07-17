package com.tkdscore.state;

/**
 * Clase abstracta EstadoCombate que define la interfaz base para el patrón de diseño State.
 * Cada estado concreto del combate de Taekwondo (en combate, descanso o finalizado) 
 * heredará de esta clase y definirá su propio comportamiento para la transición de fases.
 */
public abstract class EstadoCombate {
    
    /**
     * Método abstracto que contiene la lógica de transición y control para este estado.
     * Es invocado por el contexto (la clase 'Combate') para decidir cuál es el siguiente 
     * paso en el flujo de la pelea.
     * * @param combate El contexto del combate actual que contiene el estado y la ronda activa.
     */
    public abstract void manejar(Combate combate);
    
    /**
     * Método abstracto para obtener el nombre descriptivo y estandarizado del estado.
     * Útil para mostrar la fase actual en la interfaz de usuario o persistirla en la BD.
     * * @return Cadena de texto con el nombre único del estado (ej. "Ronda en curso", "Descanso", "Finalizado").
     */
    public abstract String getNombre();
}