package com.tkdscore.ui;

/**
 * Clase AtletaDTO que actúa como un Objeto de Transferencia de Datos (Data Transfer Object).
 * Se utiliza para empaquetar y transportar los datos esenciales de un competidor (ID y Nombre)
 * entre los diferentes paneles e interfaces del sistema (UI) sin exponer la lógica de negocio 
 * ni las entidades directas de la base de datos.
 * * Al definir sus atributos como 'public final', se garantiza la inmutabilidad de los datos
 * durante su viaje entre pantallas, eliminando la necesidad de métodos getter y setter.
 */
public class AtletaDTO {
    
    /**
     * Identificador único del competidor en la base de datos.
     * Al ser 'final', una vez asignado en el constructor no puede ser modificado.
     */
    public final int id;
    
    /**
     * Nombre completo o de presentación del atleta.
     * Al ser 'final', asegura que la información se mantenga íntegra durante el transporte.
     */
    public final String nombre;

    /**
     * Constructor único de AtletaDTO.
     * Inicializa los campos obligatorios para el transporte de la información.
     * * @param id     Identificador numérico asignado al atleta.
     * @param nombre Nombre completo del competidor.
     */
    public AtletaDTO(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}