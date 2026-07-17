package com.tkdscore.decorator;

// Importamos la interfaz base que define el comportamiento común de todas las técnicas de combate
import com.tkdscore.tecnica.ITecnica;

/**
 * Clase abstracta PuntajeDecorator que actúa como el decorador base (Base Decorator).
 * Implementa la interfaz 'ITecnica' para que cualquier decorador sea tratado 
 * exactamente como si fuera una técnica más del sistema (polimorfismo).
 */
public abstract class PuntajeDecorator implements ITecnica {

    /**
     * Objeto protegido que almacena la técnica interna que está siendo envuelta o decorada.
     * Al ser 'protected', las clases hijas (como BonoCabeza y BonoGiro) pueden acceder 
     * directamente a este objeto para delegar o complementar su lógica de cálculo.
     */
    protected ITecnica tecnica;

    /**
     * Constructor del decorador base.
     * Recibe obligatoriamente un objeto que implemente ITecnica para envolverlo.
     * * @param tecnica El componente o técnica que va a ser decorada.
     */
    public PuntajeDecorator(ITecnica tecnica) {
        this.tecnica = tecnica; // Guardamos la referencia de la técnica original o decorada previamente
    }

    /**
     * Delegación por defecto del método getNombre().
     * Por comportamiento base, un decorador simplemente le solicita el nombre a la 
     * técnica que tiene envuelta en su interior.
     * * @return El nombre descriptivo de la técnica envuelta.
     */
    @Override
    public String getNombre() {
        return tecnica.getNombre(); // Redirige la llamada al objeto interno 'tecnica'
    }
}