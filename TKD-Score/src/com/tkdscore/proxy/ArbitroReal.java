package com.tkdscore.proxy;

// Importamos la interfaz base de las técnicas para poder leer los datos del punto anotado
import com.tkdscore.tecnica.ITecnica;

/**
 * Clase ArbitroReal que actúa como el Objeto Real (Real Subject) en el patrón Proxy.
 * Implementa la interfaz 'IArbitro'. Es el componente de negocio principal que realiza 
 * el trabajo final de registro una vez que el Proxy de seguridad ('ArbitroProxy') 
 * ha validado y autorizado las credenciales del usuario.
 */
public class ArbitroReal implements IArbitro {

    // Almacena el nombre descriptivo del árbitro que está operando en la mesa de control
    private String nombre;

    /**
     * Constructor de la clase ArbitroReal.
     * Es instanciado internamente por el proxy únicamente tras una autenticación exitosa.
     * * @param nombre Nombre del árbitro validado en el sistema.
     */
    public ArbitroReal(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Registra de manera definitiva la técnica efectuada por un atleta en el sistema.
     * Este método contiene la lógica central de negocio que se ejecuta libre de validaciones 
     * redundantes de seguridad, ya que asume que el Proxy ya hizo el filtrado previo.
     * * @param atleta  Esquina del competidor que puntuó ("AZUL" o "ROJO").
     * @param tecnica Objeto técnico (decorado o base) que contiene el nombre y los puntos acumulados.
     */
    @Override
    public void registrarPunto(String atleta, ITecnica tecnica) {
        // Imprime en la consola de auditoría el detalle técnico del punto registrado por este árbitro
        System.out.println("[ArbitroReal " + nombre + "] Registrando " + tecnica.getNombre()
                + " para " + atleta + " (" + tecnica.calcularPuntos() + " pts)");
    }
}