package com.tkdscore.state;

/**
 * Clase Combate que actúa como el "Contexto" (Context) en el patrón de diseño State.
 * Mantiene una referencia al estado actual (EstadoCombate) y expone la interfaz para que
 * la GUI o el controlador manejen los flujos y transiciones de las rondas en un combate de Taekwondo.
 */
public class Combate {

    // Referencia polimórfica al estado actual del combate
    private EstadoCombate estadoActual;
    
    // Control interno del número de ronda activa (por defecto inicia en 1)
    private int rondaActual = 1;

    /**
     * Constructor de Combate.
     * Inicializa el combate en su estado por defecto: una ronda activa (EstadoRonda).
     */
    public Combate() {
        this.estadoActual = new EstadoRonda();
    }

    /**
     * Permite realizar la transición de un estado a otro de manera dinámica.
     * * @param nuevoEstado El siguiente estado que tomará el combate.
     */
    public void cambiarEstado(EstadoCombate nuevoEstado) {
        this.estadoActual = nuevoEstado;
        System.out.println("[Combate] Nuevo estado -> " + nuevoEstado.getNombre());
    }

    /**
     * Gatilla la acción o lógica correspondiente al estado actual del combate.
     * Delega completamente el comportamiento al método 'manejar' del estado concreto.
     */
    public void siguientePaso() {
        estadoActual.manejar(this);
    }

    /**
     * Obtiene el número de la ronda actual.
     * @return El número de ronda.
     */
    public int getRondaActual() {
        return rondaActual;
    }

    /**
     * Incrementa en una unidad el contador de la ronda.
     */
    public void incrementarRonda() {
        rondaActual++;
    }

    /**
     * Retorna el nombre descriptivo del estado actual del combate.
     * @return Cadena de texto con el nombre del estado.
     */
    public String getEstadoNombre() {
        return estadoActual.getNombre();
    }

    /**
     * Regla de negocio crítica: Valida si en el estado actual se permite registrar puntuaciones.
     * Solo se puede anotar puntos si el combate está en una ronda activa (no en descanso ni finalizado).
     * * @return true si el estado actual es una instancia de EstadoRonda, false de lo contrario.
     */
    public boolean puedeAnotarPuntos() {
        return estadoActual instanceof EstadoRonda;
    }

    /**
     * Reinicia por completo el combate a sus condiciones iniciales (Ronda 1 y EstadoRonda).
     */
    public void reiniciar() {
        this.rondaActual = 1;
        this.estadoActual = new EstadoRonda();
    }

    /**
     * Restaura los parámetros de ronda y estado del combate (útil al cargar datos de la BD).
     * * @param ronda        Número de ronda a restaurar.
     * @param nombreEstado Nombre del estado a reasignar ("Ronda", "Descanso", "Finalizado").
     */
    public void restaurarDesde(int ronda, String nombreEstado) {
        this.rondaActual = ronda;
        this.estadoActual = crearEstadoPorNombre(nombreEstado);
    }

    /**
     * Método fábrica (Helper) que genera una instancia concreta de EstadoCombate
     * basándose en una cadena de texto (String) recuperada de la base de datos.
     * * @param nombreEstado Nombre descriptivo del estado.
     * @return El objeto de estado correspondiente (EstadoRonda por defecto).
     */
    private EstadoCombate crearEstadoPorNombre(String nombreEstado) {
        if (nombreEstado == null) {
            return new EstadoRonda(); // Fallback de seguridad
        }
        switch (nombreEstado) {
            case "Descanso":
                return new EstadoDescanso();
            case "Finalizado":
                return new EstadoFinalizado();
            default:
                return new EstadoRonda(); // Por defecto asume que está en ronda activa
        }
    }
}