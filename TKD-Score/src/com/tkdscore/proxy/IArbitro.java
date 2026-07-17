package com.tkdscore.proxy;

// Importamos la interfaz base de las técnicas de combate
import com.tkdscore.tecnica.ITecnica;

/**
 * Interfaz IArbitro que actúa como el Sujeto Común (Subject) en el patrón Proxy.
 * Define el contrato estándar para el registro de puntuaciones. Esto permite que
 * el proxy (ArbitroProxy) y el objeto real (ArbitroReal) puedan ser utilizados
 * de manera intercambiable en el sistema mediante polimorfismo.
 */
public interface IArbitro {
    
    /**
     * Define la operación obligatoria para registrar un punto anotado en el sistema.
     * * @param atleta  Esquina del competidor que sumará el punto ("AZUL" o "ROJO").
     * @param tecnica Técnica de combate ejecutada con éxito (puede estar decorada con bonos).
     */
    void registrarPunto(String atleta, ITecnica tecnica);
}