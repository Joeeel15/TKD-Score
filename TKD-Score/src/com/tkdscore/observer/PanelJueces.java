package com.tkdscore.observer;

/**
 * Clase PanelJueces que actúa como un observador concreto (Concrete Observer).
 * Implementa la interfaz 'IObservador' para "escuchar" y reaccionar de manera automática 
 * cada vez que el marcador central cambie sus puntuaciones.
 */
public class PanelJueces implements IObservador {

    /**
     * Método de actualización que es invocado automáticamente por el 'Marcador' (Sujeto).
     * En esta implementación, simula el panel que visualizan los jueces del torneo, 
     * imprimiendo en la consola de depuración el estado exacto y confirmado del marcador.
     * * @param puntosAzul Puntos acumulados en tiempo real por el atleta de la esquina Azul.
     * @param puntosRojo Puntos acumulados en tiempo real por el atleta de la esquina Roja.
     */
    @Override
    public void actualizar(int puntosAzul, int puntosRojo) {
        // Imprime en la consola del sistema el marcador actualizado para propósitos de control y log
        System.out.println("[PanelJueces] Marcador confirmado -> Azul: " + puntosAzul + " | Rojo: " + puntosRojo);
    }
}