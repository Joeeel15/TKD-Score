package com.tkdscore.observer;

/**
 * Clase SistemaSonido que actúa como un observador concreto (Concrete Observer).
 * Implementa la interfaz 'IObservador' para reaccionar emitiendo una señal auditiva
 * cada vez que se registre una actualización de puntos en el marcador.
 */
public class SistemaSonido implements IObservador {

    /**
     * Método de actualización que es invocado automáticamente por el 'Marcador' (Sujeto).
     * En esta implementación, simula el altavoz de la arena deportiva, emitiendo un pitido 
     * característico en la consola de depuración cada vez que el puntaje cambia.
     * * @param puntosAzul Puntos acumulados en tiempo real por el atleta de la esquina Azul.
     * @param puntosRojo Puntos acumulados en tiempo real por el atleta de la esquina Roja.
     */
    @Override
    public void actualizar(int puntosAzul, int puntosRojo) {
        // Simula la alerta sonora física (pitido de confirmación) mediante texto en la consola
        System.out.println("[SistemaSonido] *beep* Nuevo punto registrado");
    }
}