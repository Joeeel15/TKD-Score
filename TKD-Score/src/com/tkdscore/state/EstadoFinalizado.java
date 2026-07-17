package com.tkdscore.state;

/**
 * Clase EstadoFinalizado que representa un estado concreto (Concrete State) en el patrón State.
 * Define el comportamiento de un combate de Taekwondo que ha llegado a su fin. Actúa 
 * como un estado terminal o de parada dentro de la máquina de estados.
 */
public class EstadoFinalizado extends EstadoCombate {

    /**
     * Maneja la lógica de transición cuando el combate ya ha concluido.
     * Al ser la fase final, no existen estados posteriores en el flujo regular de la pelea,
     * por lo que simplemente se emite un mensaje informativo por consola y se ignora la acción.
     * * @param combate El contexto del combate actual que se encuentra en su fase de cierre.
     */
    @Override
    public void manejar(Combate combate) {
        // Notifica por consola de depuración que el combate ya está resuelto y cerrado
        System.out.println("[EstadoFinalizado] El combate ya finalizo. No hay mas transiciones.");
    }

    /**
     * Retorna el nombre identificador de esta fase del combate.
     * @return La cadena "Finalizado".
     */
    @Override
    public String getNombre() {
        return "Finalizado";
    }
}