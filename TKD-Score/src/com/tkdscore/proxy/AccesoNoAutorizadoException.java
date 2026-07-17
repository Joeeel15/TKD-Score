package com.tkdscore.proxy;

/**
 * Clase AccesoNoAutorizadoException que define una excepción personalizada de tiempo de ejecución (RuntimeException).
 * Se utiliza en combinación con el patrón de diseño Proxy (en la clase ArbitroProxy) para señalar
 * y detener el flujo del programa de manera controlada si un árbitro o usuario intenta
 * realizar una operación para la cual no cuenta con las credenciales o el nivel de acceso requerido.
 */
public class AccesoNoAutorizadoException extends RuntimeException {

    /**
     * Constructor de la excepción personalizada.
     * Recibe un mensaje descriptivo del error de seguridad y lo propaga a la superclase RuntimeException.
     * * @param mensaje Texto explicativo que detalla la razón del bloqueo o falta de privilegios.
     */
    public AccesoNoAutorizadoException(String mensaje) {
        // Pasa el mensaje de error al constructor de la clase padre (RuntimeException)
        super(mensaje);
    }
}