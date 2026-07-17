package com.tkdscore.observer;

/**
 * Interfaz IObservador que define el contrato para el patrón de diseño Observer.
 * Cualquier clase que actúe como observadora (como las pantallas o paneles del marcador) 
 * debe implementar esta interfaz para recibir notificaciones automáticas y actualizar 
 * su interfaz de usuario de forma asíncrona en tiempo real.
 */
public interface IObservador {
    
    /**
     * Método de actualización (callback) que es invocado por el objeto observado (Sujeto)
     * cada vez que ocurre un cambio en los puntajes del combate.
     * * @param puntosAzul La cantidad actualizada de puntos acumulados por el atleta azul.
     * @param puntosRojo La cantidad actualizada de puntos acumulados por el atleta rojo.
     */
    void actualizar(int puntosAzul, int puntosRojo);
}