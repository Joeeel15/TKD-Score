package com.tkdscore.tecnica;

/**
 * Interfaz ITecnica que actúa como el Componente Base (Component) en el patrón Decorator.
 * Define las operaciones esenciales que cualquier técnica de Taekwondo (básica o decorada 
 * con bonificaciones especiales) debe implementar para calcular sus puntos y reportar su nombre.
 */
public interface ITecnica {
    
    /**
     * Calcula el valor total en puntos de la técnica ejecutada.
     * Si la técnica está decorada (por ejemplo, con un giro de 360 grados o impacto en el casco), 
     * este método calculará la suma del puntaje base más todos los modificadores agregados.
     * * @return Cantidad de puntos que se asignarán al marcador del atleta.
     */
    int calcularPuntos();
    
    /**
     * Obtiene el nombre representativo o la descripción de la técnica realizada.
     * En técnicas decoradas, este método acumula dinámicamente las descripciones de los modificadores 
     * (por ejemplo: "Patada Directa + Con Giro (360) + A la Cabeza").
     * * @return Cadena de texto con el nombre descriptivo de la acción técnica.
     */
    String getNombre();
}