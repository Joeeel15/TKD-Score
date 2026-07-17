import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelControlAtletas extends JPanel {

    private JTable tablaAtletas;
    private DefaultTableModel modeloTabla;
    private JButton btnEditar;
    private JButton btnReinicio;

    public PanelControlAtletas() {
        // Usamos un BorderLayout para organizar la interfaz de manera limpia
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Inicialización de la Tabla y Datos de prueba
        String[] columnas = {"ID", "Nombre del Participante", "Categoría", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que se edite directamente sobre las celdas
            }
        };

        // Agregar datos de prueba (Simulando registros de Taekwondo/Deportes)
        modeloTabla.addRow(new Object[]{1, "Amir Santos", "Cinturón Negro", "Activo"});
        modeloTabla.addRow(new Object[]{2, "Rosa Vivas", "Cinturón Azul", "Activo"});
        modeloTabla.addRow(new Object[]{3, "Miguel Condor", "Cinturón Verde", "Activo"});
        modeloTabla.addRow(new Object[]{4, "Freddy Galindo", "Cinturón Rojo", "Activo"});

        tablaAtletas = new JTable(modeloTabla);
        tablaAtletas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo permite seleccionar una fila
        tablaAtletas.getTableHeader().setReorderingAllowed(false); // Bloquea el movimiento de columnas
        
        JScrollPane scrollPane = new JScrollPane(tablaAtletas);
        add(scrollPane, BorderLayout.CENTER);

        // 2. Creación del Panel de Botones (Lado Inferior)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        // --- BOTÓN EDITAR ---
        btnEditar = new JButton("Editar Participante ✏️");
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEditar.setBackground(new Color(255, 193, 7)); // Color Amarillo de advertencia/edición
        btnEditar.setForeground(Color.BLACK);
        btnEditar.setFocusPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEditar.addActionListener(e -> {
            int filaSeleccionada = tablaAtletas.getSelectedRow();
            
            // Validar que se haya seleccionado un elemento
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, 
                        "Por favor, seleccione un participante de la lista para editar.", 
                        "Atención", 
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtener el ID y el nombre actual de la tabla
            int idAtleta = (int) tablaAtletas.getValueAt(filaSeleccionada, 0);
            String nombreActual = (String) tablaAtletas.getValueAt(filaSeleccionada, 1);

            // Mostrar el cuadro de diálogo para modificar el nombre
            String nuevoNombre = JOptionPane.showInputDialog(this, 
                    "Modificar el nombre del participante (ID: " + idAtleta + "):", 
                    nombreActual);
            
            // Guardar cambios si el usuario ingresa un nombre válido y presiona OK
            if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                // Actualizar visualmente en la tabla
                tablaAtletas.setValueAt(nuevoNombre.trim(), filaSeleccionada, 1);
                
                /* * NOTA: Aquí puedes conectar tu Base de Datos o Fachada:
                 * app.getFacade().actualizarAtleta(idAtleta, nuevoNombre.trim());
                 */
                
                JOptionPane.showMessageDialog(this, 
                        "Nombre actualizado correctamente.", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // --- BOTÓN REINICIO ---
        btnReinicio = new JButton("Reiniciar Participantes 🔄");
        btnReinicio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReinicio.setBackground(new Color(220, 53, 69)); // Color Rojo de advertencia crítica
        btnReinicio.setForeground(Color.WHITE);
        btnReinicio.setFocusPainted(false);
        btnReinicio.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnReinicio.addActionListener(e -> {
            // Cuadro de confirmación doble antes de borrar los datos
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está completamente seguro de que desea REINICIAR todos los participantes?\n"
                    + "Esta acción vaciará la lista actual y no se puede deshacer.",
                    "Confirmar Reinicio Crítico",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                // Limpiar todas las filas del modelo de la tabla
                modeloTabla.setRowCount(0);

                /* * NOTA: Aquí puedes conectar tu Base de Datos o Fachada para truncar la tabla:
                 * app.getFacade().reiniciarTodosLosAtletas();
                 */

                JOptionPane.showMessageDialog(this, 
                        "Se han restablecido y eliminado todos los participantes de la lista.", 
                        "Sistema Reiniciado", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Agregar los botones al contenedor inferior
        panelBotones.add(btnEditar);
        panelBotones.add(btnReinicio);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Método principal de prueba para ver el diseño inmediatamente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Módulo de Gestión de Participantes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null); // Centra la ventana en pantalla
            frame.add(new PanelControlAtletas());
            frame.setVisible(true);
        });
    }
}