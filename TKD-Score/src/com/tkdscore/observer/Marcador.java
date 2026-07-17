package com.tkdscore.observer;

// Importamos la estructura ArrayList y la interfaz List para manejar dinámicamente los observadores
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Marcador que actúa como el "Sujeto" (Subject) u "Observable" en el patrón Observer.
 * Se encarga de encapsular los puntajes del combate en curso y de notificar de manera 
 * automática a todas las pantallas y paneles registrados (observadores) cada vez que 
 * ocurre un cambio.
 */
public class Marcador {

    /**
     * Lista interna que almacena a todos los observadores suscritos.
     * Cualquier vista, pantalla secundaria o panel que implemente 'IObservador' 
     * puede agregarse a esta lista para recibir actualizaciones.
     */
    private List<IObservador> observadores = new ArrayList<>();
    
    // Variables para el control del marcador del combate en tiempo real
    private int puntosAzul = 0; // Puntos actuales del atleta de la esquina Azul
    private int puntosRojo = 0; // Puntos actuales del atleta de la esquina Roja

    /**
     * Agrega (suscribe) un nuevo observador a la lista de notificaciones.
     * * @param o El observador que desea escuchar los cambios de puntaje.
     */
    public void agregarObservador(IObservador o) {
        observadores.add(o);
    }

    /**
     * Remueve (desasocia) un observador de la lista de notificaciones.
     * * @param o El observador que ya no desea seguir escuchando los cambios.
     */
    public void quitarObservador(IObservador o) {
        observadores.remove(o);
    }

    /**
     * Método principal para sumar puntos. Determina el atleta que anotó 
     * e incrementa su puntaje acumulado, desencadenando la alerta a los observadores.
     * * @param atleta El color de la esquina del atleta anotador ("AZUL" o "ROJO").
     * @param puntos Cantidad de puntos que se van a sumar al puntaje actual.
     */
    public void actualizarPuntaje(String atleta, int puntos) {
        // Compara el color omitiendo mayúsculas y minúsculas de forma segura
        if ("AZUL".equalsIgnoreCase(atleta)) {
            puntosAzul += puntos; // Suma los puntos al atleta Azul
        } else {
            puntosRojo += puntos; // Suma los puntos al atleta Rojo (cualquier otra entrada por defecto)
        }
        
        // Ejecuta la difusión automática del nuevo estado
        notificar();
    }

    /**
     * Recorre la lista de observadores suscritos y llama al método 'actualizar' 
     * de cada uno, enviando los puntajes más recientes.
     */
    private void notificar() {
        // Bucle "for-each" para recorrer la colección de observadores registrados
        for (IObservador o : observadores) {
            o.actualizar(puntosAzul, puntosRojo); // Actualiza cada pantalla o panel individualmente
        }
    }

    /**
     * Obtiene los puntos del atleta Azul.
     * @return Puntos actuales del atleta azul.
     */
    public int getPuntosAzul() {
        return puntosAzul;
    }

    /**
     * Obtiene los puntos del atleta Rojo.
     * @return Puntos actuales del atleta rojo.
     */
    public int getPuntosRojo() {
        return puntosRojo;
    }

    /**
     * Restaura los puntajes del marcador a valores específicos (usado en conjunto 
     * con el patrón Memento para realizar el retroceso de puntos en el IVR).
     * * @param azul Nuevo puntaje a definir para el atleta Azul.
     * @param rojo Nuevo puntaje a definir para el atleta Rojo.
     */
    public void restaurarEstado(int azul, int rojo) {
        this.puntosAzul = azul;
        this.puntosRojo = rojo;
        
        // Notifica a las pantallas visuales el cambio brusco para que se actualicen de inmediato
        notificar();
    }
}