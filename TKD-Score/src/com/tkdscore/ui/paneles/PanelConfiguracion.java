package com.tkdscore.ui.paneles;

import com.tkdscore.ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Clase PanelConfiguracion que representa el panel de control administrativo del sistema.
 * Permite visualizar el estado de la sesión activa del árbitro, verificar la conexión en tiempo real 
 * con SQL Server, leer directivas del reglamento WT y ejecutar acciones de mantenimiento crítico, 
 * tales como reiniciar el marcador del combate activo o borrar por completo el progreso del campeonato.
 */
public class PanelConfiguracion extends JPanel {

    /**
     * Constructor del panel de configuración.
     * Configura el diseño base (BorderLayout) con espaciado vertical y gatilla la construcción de la interfaz.
     * * @param app Referencia a la ventana principal para interactuar con el estado global de la aplicación.
     */
    public PanelConfiguracion(VentanaPrincipal app) {
        super(new BorderLayout(0, 18));
        setBackground(Color.WHITE); // Fondo blanco limpio para un estilo moderno y minimalista
        construirUI(app);
    }

    /**
     * Ensambla todos los componentes gráficos del panel estructurándolos mediante tarjetas informativas y de acción.
     * * @param app Instancia de VentanaPrincipal de donde se obtienen los datos de conexión, árbitro y combate.
     */
    private void construirUI(VentanaPrincipal app) {
        // Establecer un margen interno generoso para que los elementos no queden pegados a los bordes de la ventana
        setBorder(new EmptyBorder(20, 25, 25, 25));

        // --- ENCABEZADO PRINCIPAL ---
        JLabel lblTitulo = new JLabel("<html><h2 style='margin:0;'>Configuración</h2>"
                + "<p style='color:grey; margin-top:4px;'>Reglamento, estado del sistema y mantenimiento general.</p></html>");
        add(lblTitulo, BorderLayout.NORTH);

        // --- CONTENEDOR DE TARJETAS (BoxLayout Vertical) ---
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        // ==========================================
        // TARJETA 1: Sesión Activa del Árbitro
        // ==========================================
        boolean haySesion = app.getIdArbitroSesion() != null;
        String nombreArbitro = app.getNombreArbitroSesion() != null ? app.getNombreArbitroSesion() : "Desconocido";
        
        contenido.add(crearTarjeta(
                "👤  Sesión Activa",
                "Árbitro conectado: <b>" + nombreArbitro + "</b>"
                        + (haySesion ? " (ID interno #" + app.getIdArbitroSesion() + ")" : "")
                        + ".<br>Todos los puntos que anotes quedarán registrados a nombre de este árbitro.",
                VentanaPrincipal.COLOR_AZUL_TKD,
                null));

        contenido.add(Box.createVerticalStrut(16)); // Espaciador vertical estándar

        // ==========================================
        // TARJETA 2: Estado de la Conexión a la BD (SQL Server)
        // ==========================================
        boolean conexionOk = app.isConexionBDActiva();
        String estadoTexto = conexionOk
                ? "🟢  Conectado correctamente a SQL Server. Todos los cambios se están guardando."
                : "🔴  Sin conexión a la base de datos. Los cambios NO se están guardando de forma permanente.";
        
        contenido.add(crearTarjeta(
                "🗄️  Estado de la Base de Datos",
                estadoTexto,
                conexionOk ? new Color(25, 135, 84) : VentanaPrincipal.COLOR_ROJO_TKD, // Verde éxito / Rojo error
                null));

        contenido.add(Box.createVerticalStrut(16));

        // ==========================================
        // TARJETA 3: Reglamento WT Paramétrico
        // ==========================================
        contenido.add(crearTarjeta(
                "📖  Reglamento WT Dinámico",
                "Las reglas y asignaciones de puntos se manejan paramétricamente de acuerdo a las "
                        + "directivas de la Federación Mundial de Taekwondo.",
                VentanaPrincipal.COLOR_AZUL_TKD,
                null));

        contenido.add(Box.createVerticalStrut(16));

        // ==========================================
        // TARJETA 4: Mantenimiento - Reset de Combate Activo
        // ==========================================
        JButton btnResetTotal = new JButton("RESET TOTAL — REINICIAR COMPETENCIA");
        btnResetTotal.setBackground(VentanaPrincipal.COLOR_ROJO_TKD);
        btnResetTotal.setForeground(Color.WHITE);
        btnResetTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnResetTotal.setFocusPainted(false);
        btnResetTotal.setBorder(new EmptyBorder(12, 20, 12, 20));
        btnResetTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnResetTotal.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Acción: Reinicia el combate cargado actualmente a 00-00 y ronda 1 sin afectar el bracket general
        btnResetTotal.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está absolutamente seguro de resetear todo el campeonato?\nEsto reiniciará el marcador activo.",
                    "Alerta Crítica", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                app.resetearCombateActivo();
                JOptionPane.showMessageDialog(this, "Marcadores reiniciados a 00.");
            }
        });

        contenido.add(crearTarjeta(
                "⚠️  Mantenimiento del Sistema",
                "Reinicia el marcador y el estado del combate activo a su condición inicial. "
                        + "Esta acción no se puede deshacer, úsala solo si es necesario.",
                VentanaPrincipal.COLOR_ROJO_TKD,
                btnResetTotal));

        contenido.add(Box.createVerticalStrut(16));

        // ==========================================
        // TARJETA 5: Mantenimiento Crítico - Reinicio Total del Torneo
        // ==========================================
        JButton btnReiniciarTorneo = new JButton("REINICIAR TORNEO COMPLETO — BORRAR TODAS LAS PELEAS");
        btnReiniciarTorneo.setBackground(new Color(120, 20, 25)); // Color rojo oscuro de advertencia extrema
        btnReiniciarTorneo.setForeground(Color.WHITE);
        btnReiniciarTorneo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReiniciarTorneo.setFocusPainted(false);
        btnReiniciarTorneo.setBorder(new EmptyBorder(12, 20, 12, 20));
        btnReiniciarTorneo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReiniciarTorneo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Acción: Elimina cascadas, limpia puntos registrados en la BD y reinicia todo el fixture (bracket)
        btnReiniciarTorneo.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Reiniciar TODO el torneo?\n\nEsto borrará TODOS los puntos, sanciones y ganadores "
                            + "de TODOS los combates (no solo el activo). El bracket volverá a empezar desde cero.\n"
                            + "Los atletas registrados y los emparejamientos NO se borran.\n\n"
                            + "Esta acción no se puede deshacer.",
                    "Alerta Crítica — Reinicio Total", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = app.reiniciarTorneoCompleto();
                JOptionPane.showMessageDialog(this, ok
                        ? "Torneo reiniciado por completo. Ve a 'Planificación' para ver el bracket desde cero."
                        : "No se pudo reiniciar el torneo (revisa la conexión a la base de datos).",
                        "Reinicio Total", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            }
        });

        contenido.add(crearTarjeta(
                "🧨  Reinicio Total del Torneo",
                "Borra el historial completo de puntos y ganadores de TODOS los combates del campeonato "
                        + "para volver a empezar el bracket desde cero. Úsalo solo al iniciar una nueva competencia.",
                new Color(120, 20, 25),
                btnReiniciarTorneo));

        // --- SISTEMA DE SCROLL PARA EVITAR DESBORDAMIENTOS EN PANTALLAS CHICAS ---
        JScrollPane scroll = new JScrollPane(envolverEnPanel(contenido));
        scroll.setBorder(null); // Elimina el marco por defecto del scrollpane para integrarlo al fondo limpio
        scroll.getVerticalScrollBar().setUnitIncrement(16); // Hace el scroll más suave y rápido al usar la rueda del mouse
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Helper para envolver el contenedor BoxLayout en la sección norte de un BorderLayout.
     * Esto evita que las tarjetas se estiren verticalmente de manera desproporcionada si hay espacio extra en pantalla.
     * * @param contenido El panel que contiene las tarjetas apiladas verticalmente.
     * @return JPanel contenedor que envuelve los elementos.
     */
    private JPanel envolverEnPanel(JPanel contenido) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contenido, BorderLayout.NORTH);
        return wrapper;
    }

    /**
     * Factoría de interfaz de usuario para crear Tarjetas visuales adaptativas.
     * Diseña un contenedor blanco con relieve gris y un borde izquierdo grueso de un color acentuado.
     * * @param titulo          Título principal de la tarjeta.
     * @param descripcionHtml Breve descripción explicativa que admite HTML para formatear texto fácilmente.
     * @param colorAcento     Color del borde indicador izquierdo (ej. azul para información, rojo para peligro).
     * @param extra           Componente Swing opcional (como un JButton) para añadir interacción al pie de la tarjeta.
     * @return Un JPanel completamente configurado y estilizado como tarjeta.
     */
    private JPanel crearTarjeta(String titulo, String descripcionHtml, Color colorAcento, JComponent extra) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Define un tamaño máximo estimado para que no ocupen todo el espacio del scroll
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, extra == null ? 110 : 150));
        
        // Aplicar bordes combinados: MatteBorder (borde izquierdo grueso de color) + LineBorder (borde gris exterior) + EmptyBorder (padding interno)
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, colorAcento),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(228, 231, 235), 1),
                        new EmptyBorder(14, 18, 14, 18))));

        // Etiqueta del título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(8));

        // Etiqueta de la descripción que envuelve texto con un ancho máximo de 520px para evitar rupturas de layout
        JLabel lblDescripcion = new JLabel("<html><div style='width:520px;color:#555;'>" + descripcionHtml + "</div></html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(lblDescripcion);

        // Si se provee un componente extra (como los botones de reset), se añade dinámicamente al fondo
        if (extra != null) {
            tarjeta.add(Box.createVerticalStrut(14));
            extra.setAlignmentX(Component.LEFT_ALIGNMENT);
            tarjeta.add(extra);
        }

        return tarjeta;
    }
}