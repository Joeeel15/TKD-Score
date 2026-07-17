package com.tkdscore.ui.paneles;

import com.tkdscore.ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Clase PanelHistorial que representa la interfaz de auditoría del sistema.
 * Renderiza una tabla detallada con el historial de puntos, técnicas y bonificaciones 
 * registradas en tiempo real durante los combates del torneo en la base de datos.
 */
public class PanelHistorial extends JPanel {

    /**
     * Constructor del panel de historial.
     * Configura el Layout principal con márgenes de separación y delega la construcción de los componentes.
     * * @param app Referencia a la ventana principal para interactuar con los servicios de datos.
     */
    public PanelHistorial(VentanaPrincipal app) {
        super(new BorderLayout(15, 15));
        construirUI(app);
    }

    /**
     * Ensambla los componentes gráficos del panel, recupera los registros desde la base de datos
     * y los presenta en un componente JTable estructurado e ineditable.
     * * @param app Instancia de VentanaPrincipal de donde se extrae la matriz de puntos históricos.
     */
    private void construirUI(VentanaPrincipal app) {
        // Establecer un margen interno de 20 píxeles en todos los lados del panel
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- ENCABEZADO DE AUDITORÍA ---
        add(new JLabel("<html><h2>Historial Estadístico de Combates</h2>"
                + "<p style='color:grey;'>Auditoría de puntos capturados en tiempo real a lo largo de la competencia.</p></html>",
                SwingConstants.LEFT), BorderLayout.NORTH);

        // --- DEFINICIÓN DE COLUMNAS DE LA TABLA ---
        String[] columnas = {"Fecha / Hora", "Enfrentamiento", "Técnica Aplicada", "Puntos Base", "Bonificación", "Atleta Anotador"};
        
        // Recuperar los datos de auditoría directamente desde la base de datos SQL Server
        Object[][] datos = app.obtenerHistorialDesdeBD();

        // --- VALIDACIÓN DE REGISTROS VACÍOS ---
        if (datos == null || datos.length == 0) {
            // Si no hay datos, mostramos un mensaje amigable centrado en el panel en lugar de una tabla vacía
            JPanel panelVacio = new JPanel(new GridBagLayout());
            JLabel lblMensaje = new JLabel("<html><center style='color:grey;'>"
                    + "<h3>Aún no hay puntos registrados</h3>"
                    + "Los eventos de puntuación aparecerán aquí una vez inicie un combate."
                    + "</center></html>");
            lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panelVacio.add(lblMensaje);
            
            add(panelVacio, BorderLayout.CENTER);
        } else {
            // --- CONSTRUCCIÓN DEL MODELO DE TABLA INEDITABLE ---
            // Sobrescribimos 'isCellEditable' para evitar que el usuario altere los valores mostrados en la UI
            DefaultTableModel modeloTabla = new DefaultTableModel(datos, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Desactiva la edición directa sobre la celda
                }
            };

            // --- DISEÑO Y CONFIGURACIÓN DE LA JTABLE ---
            JTable tablaHistorial = new JTable(modeloTabla);
            tablaHistorial.setRowHeight(28); // Altura de fila cómoda y legible
            tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Selección de una fila a la vez
            tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Estilizar el encabezado de la tabla para que combine con el estilo moderno del sistema
            tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            tablaHistorial.getTableHeader().setReorderingAllowed(false); // Evita que el usuario reordene las columnas arrastrándolas

            // --- CONTENEDOR DE SCROLL ---
            JScrollPane scrollPane = new JScrollPane(tablaHistorial);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(228, 231, 235))); // Borde gris sutil
            
            add(scrollPane, BorderLayout.CENTER);
        }
    }
}