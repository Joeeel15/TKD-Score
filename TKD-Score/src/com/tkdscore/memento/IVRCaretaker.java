package com.tkdscore.memento;

// Importamos la estructura de datos ArrayDeque y la interfaz Deque para simular una pila (Stack)
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Clase IVRCaretaker que actúa como el Guardián (Caretaker) en el patrón Memento.
 * Su función principal es almacenar ordenadamente los mementos (estados anteriores) 
 * generados por el marcador, permitiendo volver atrás (deshacer) en caso de una 
 * revisión de video instantánea (IVR) exitosa.
 */
public class IVRCaretaker {

    /**
     * Pila bidireccional (Deque) utilizada para almacenar el historial de estados del marcador.
     * Usamos Deque con ArrayDeque en lugar de la clase Stack porque es más moderna, 
     * eficiente y segura para operaciones de pila de un solo hilo.
     * Funciona bajo la lógica LIFO (Último en entrar, Primero en salir).
     */
    private Deque<MarcadorMemento> historial = new ArrayDeque<>();

    /**
     * Guarda un nuevo estado (Memento) en el tope del historial.
     * Se invoca automáticamente cada vez que se anota un punto o se realiza un cambio.
     * * @param m El memento que contiene la foto del estado del marcador antes de la nueva acción.
     */
    public void guardar(MarcadorMemento m) {
        // Inserta el memento al principio de la pila (en la cima)
        historial.push(m);
    }

    /**
     * Recupera y elimina el último estado (Memento) guardado en el historial.
     * Es la acción directa de "Deshacer" para la revisión de video (IVR).
     * * @return El último MarcadorMemento registrado, o null si el historial está vacío (no hay qué revertir).
     */
    public MarcadorMemento restaurar() {
        // Validamos si la pila está vacía antes de intentar sacar un elemento para evitar excepciones
        if (historial.isEmpty()) {
            return null; // Retorna null indicando que no hay acciones previas registradas
        }
        
        // Retira y devuelve el elemento que se encuentra en la cima de la pila (el más reciente)
        return historial.pop();
    }
}