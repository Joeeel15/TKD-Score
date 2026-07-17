package com.tkdscore.state;

/**
 * Clase EstadoRonda que representa un estado concreto (Concrete State) en el patrón State.
 * Define el comportamiento y las transiciones de un combate de Taekwondo cuando 
 * se está disputando un round activo en el tapiz (donde sí se permite registrar puntos).
 */
public class EstadoRonda extends EstadoCombate {

    /**
     * Controla la transición de salida de un round activo.
     * Aplica la regla del límite de 3 asaltos oficiales:
     * - Si la ronda actual es menor a 3, el combate pasa a un periodo de 'Descanso'.
     * - Si ya se disputó la ronda 3, el combate avanza al estado terminal 'Finalizado'.
     * * @param combate El contexto del combate actual que evalúa su número de round y cambia de estado.
     */
    @Override
    public void manejar(Combate combate) {
        // Evaluamos si aún quedan asaltos por disputar (asumiendo el formato estándar de 3 rounds)
        if (combate.getRondaActual() < 3) {
            // Transición hacia el intervalo de descanso antes del siguiente asalto
            combate.cambiarEstado(new EstadoDescanso());
        } else {
            // Si ya terminó el round 3, el combate concluye definitivamente
            combate.cambiarEstado(new EstadoFinalizado());
        }
    }

    /**
     * Retorna el nombre identificador de esta fase del combate.
     * @return La cadena "Ronda en curso".
     */
    @Override
    public String getNombre() {
        return "Ronda en curso";
    }
}