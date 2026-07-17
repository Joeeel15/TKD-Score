package com.tkdscore.state;

/**
 * Clase EstadoDescanso que representa un estado concreto (Concrete State) en el patrón State.
 * Define las reglas de negocio y transiciones que ocurren cuando el combate se encuentra 
 * en el intervalo de descanso entre rounds.
 */
public class EstadoDescanso extends EstadoCombate {

    /**
     * Define la transición de estado desde el periodo de Descanso.
     * Al finalizar el descanso, el flujo natural del combate es incrementar la ronda 
     * en curso e iniciar la siguiente ronda de pelea activa (EstadoRonda).
     * * @param combate El contexto del combate actual cuya ronda y estado serán modificados.
     */
    @Override
    public void manejar(Combate combate) {
        // 1. Incrementa el contador del round actual (ej. de Ronda 1 a Ronda 2)
        combate.incrementarRonda();
        
        // 2. Realiza la transición de regreso al estado de ronda activa para poder volver a marcar puntos
        combate.cambiarEstado(new EstadoRonda());
    }

    /**
     * Retorna el nombre identificador de esta fase del combate.
     * @return La cadena "Descanso".
     */
    @Override
    public String getNombre() {
        return "Descanso";
    }
}