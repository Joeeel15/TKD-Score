package com.tkdscore.decorator;

// Importamos la interfaz base de las técnicas de combate
import com.tkdscore.tecnica.ITecnica;

/**
 * Clase BonoCabeza que actúa como un decorador concreto (Concrete Decorator).
 * Se encarga de envolver una técnica de taekwondo existente y sumarle 
 * un puntaje adicional (+2 puntos) por haber sido efectuada con éxito en la zona de la cabeza.
 */
public class BonoCabeza extends PuntajeDecorator {

    /**
     * Constructor de la clase BonoCabeza.
     * Recibe la técnica base que se va a decorar y la envía al constructor 
     * de la clase padre (PuntajeDecorator) usando 'super'.
     * * @param tecnica El objeto que implementa ITecnica y que será decorado.
     */
    public BonoCabeza(ITecnica tecnica) {
        super(tecnica); // Pasa la referencia de la técnica a la superclase decoradora
    }

    /**
     * Calcula los puntos finales sumando el bono de cabeza al puntaje de la técnica base.
     * Sobrescribe el método de la interfaz ITecnica.
     * * @return El puntaje de la técnica original más los 2 puntos adicionales de bono por cabeza.
     */
    @Override
    public int calcularPuntos() {
        // Ejecuta el cálculo de puntos de la técnica interna y le añade el valor constante de +2
        return tecnica.calcularPuntos() + 2;
    }

    /**
     * Obtiene el nombre descriptivo de la acción realizada, agregando el sufijo del bono.
     * Sobrescribe el método de la interfaz ITecnica.
     * * @return El nombre de la técnica original concatenado con el texto " + Bono Cabeza".
     */
    @Override
    public String getNombre() {
        // Obtiene el nombre de la técnica decorada y le concatena la descripción del bono
        return tecnica.getNombre() + " + Bono Cabeza";
    }
}