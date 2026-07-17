package com.tkdscore;

// Importamos la ventana principal que contiene el diseño de nuestra interfaz de usuario (GUI)
import com.tkdscore.ui.VentanaPrincipal;
// Importamos la librería SwingUtilities para gestionar los hilos de ejecución de la interfaz gráfica
import javax.swing.*;

/**
 * Clase principal (Main) que sirve como punto de partida y arranque para todo el 
 * sistema de gestión de campeonatos de Taekwondo (TKD-Score).
 */
public class Main {
    
    public static void main(String[] args) {
        /*
         * SwingUtilities.invokeLater asegura que la creación y actualización de la 
         * interfaz gráfica se ejecuten en el "Event Dispatch Thread" (EDT).
         * Esto es una buena práctica crítica en Java Swing para evitar conflictos de hilos
         * y garantizar que las ventanas se rendericen de forma segura y fluida.
         */
        SwingUtilities.invokeLater(() -> {
            // 1. Instanciamos el frame de la ventana principal del sistema
            VentanaPrincipal ventana = new VentanaPrincipal();
            
            // 2. Hacemos visible la ventana para que el usuario pueda empezar a interactuar
            ventana.setVisible(true);
        });
    }
}