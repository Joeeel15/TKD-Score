package com.tkdscore.observer;

/**
 * Clase PantallaElectronica que actúa como un observador concreto (Concrete Observer).
 * Implementa la interfaz 'IObservador' para recibir notificaciones en tiempo real 
 * cada vez que se actualice el marcador del combate.
 */
public class PantallaElectronica implements IObservador {

    /**
     * Método de actualización que es invocado automáticamente por el 'Marcador' (Sujeto).
     * En esta implementación, simula la pantalla gigante que ven los competidores y 
     * el público asistente, mostrando en la consola un diseño representativo del puntaje.
     * * @param puntosAzul Puntos acumulados en tiempo real por el atleta de la esquina Azul.
     * @param puntosRojo Puntos acumulados en tiempo real por el atleta de la esquina Roja.
     */
    @Override
    public void actualizar(int puntosAzul, int puntosRojo) {
        // Imprime en la consola de depuración el estado actual del marcador de forma visual y estilizada
        System.out.println("[PantallaElectronica] AZUL " + puntosAzul + "  -  " + puntosRojo + " ROJO");
    }
}