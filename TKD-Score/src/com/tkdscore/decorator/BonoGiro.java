package com.tkdscore.decorator;

// Importamos la interfaz base de las técnicas de combate
import com.tkdscore.tecnica.ITecnica;

/**
 * Clase BonoGiro que actúa como un decorador concreto (Concrete Decorator).
 * Se encarga de envolver una técnica de taekwondo existente y sumarle 
 * un puntaje adicional (+1 punto) por haber sido efectuada con un giro (rotación).
 */
public class BonoGiro extends PuntajeDecorator {

    /**
     * Constructor de la clase BonoGiro.
     * Recibe la técnica base que se va a decorar y la envía al constructor 
     * de la clase padre (PuntajeDecorator) usando 'super'.
     * * @param tecnica El objeto que implementa ITecnica y que será decorado.
     */
    public BonoGiro(ITecnica tecnica) {
        super(tecnica); // Pasa la referencia de la técnica a la superclase decoradora
    }

    /**
     * Calcula los puntos finales sumando el bono de giro al puntaje de la técnica base.
     * Sobrescribe el método de la interfaz ITecnica.
     * * @return El puntaje de la técnica original más 1 punto adicional por concepto de giro.
     */
    @Override
    public int calcularPuntos() {
        // Obtiene el puntaje de la técnica interna (ya sea base o previamente decorada) y le suma +1
        return tecnica.calcularPuntos() + 1;
    }

    /**
     * Obtiene el nombre descriptivo de la acción realizada, agregando el sufijo del bono de giro.
     * Sobrescribe el método de la interfaz ITecnica.
     * * @return El nombre de la técnica original concatenado con el texto " + Bono Giro".
     */
    @Override
    public String getNombre() {
        // Obtiene el nombre acumulado de la técnica y le añade la descripción de este decorador
        return tecnica.getNombre() + " + Bono Giro";
    }
}