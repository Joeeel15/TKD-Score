package com.tkdscore.ui.paneles;

import com.tkdscore.singleton.ConexionBD;
import com.tkdscore.ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase PanelAtletas que permite el Registro, Visualización y Edición (CRUD)
 * de los competidores directamente sobre la base de datos SQL Server.
 */
public class PanelAtletas extends JPanel {

    private final VentanaPrincipal app;

    // Componentes del Formulario de Entrada
    private JTextField txtNombre;
    private JTextField txtClub;
    private JTextField txtCategoria;
    private JTextField txtCinturon;
    private JButton btnGuardarAtleta;
    private JButton btnCancelarEdicion;

    // Componentes de la Tabla de Visualización
    private JTable tablaAtletas;
    private DefaultTableModel modeloTabla;

    // Estado de Control para Edición
    private Integer idAtletaEdicion = null; // null = Registrando nuevo, != null = Editando existente

    /**
     * Constructor del panel.
     * Configura el Layout principal y gatilla la construcción de la interfaz.
     * @param app Referencia a la ventana principal para compartir estados.
     */
    public PanelAtletas(VentanaPrincipal app) {
        super(new BorderLayout(15, 15));
        this.app = app;
        construirUI();
        cargarAtletasEnTabla(); // Carga inicial de datos desde la BD
    }

    /**
     * Ensambla la UI distribuyendo el panel en un formulario (Izquierda) 
     * y una tabla de visualización/gestión (Derecha).
     */
    private void construirUI() {
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- ENCABEZADO ---
        add(new JLabel("<html><h2>Módulo de Gestión de Atletas</h2>"
                + "<p style='color:grey;'>Registre competidores nuevos o seleccione uno de la lista para editar sus datos.</p></html>",
                SwingConstants.LEFT), BorderLayout.NORTH);

        // --- PANEL DE DISTRIBUCIÓN CENTRAL (Doble Columna) ---
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 20, 0));

        // ==========================================
        // COLUMNA IZQUIERDA: Formulario de Registro / Edición
        // ==========================================
        JPanel panelFormulario = new JPanel(new BorderLayout(10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Competidor"));

        JPanel camposGrid = new JPanel(new GridLayout(5, 2, 10, 10));
        txtNombre = new JTextField();
        txtClub = new JTextField();
        txtCategoria = new JTextField();
        txtCinturon = new JTextField();

        camposGrid.add(new JLabel("Nombre Completo:"));   camposGrid.add(txtNombre);
        camposGrid.add(new JLabel("Club / País:"));        camposGrid.add(txtClub);
        camposGrid.add(new JLabel("Categoría de Peso:")); camposGrid.add(txtCategoria);
        camposGrid.add(new JLabel("Cinturón:"));          camposGrid.add(txtCinturon);

        // Botones de control del formulario
        btnGuardarAtleta = new JButton("Registrar Competidor");
        btnGuardarAtleta.setBackground(VentanaPrincipal.COLOR_AZUL_TKD);
        btnGuardarAtleta.setForeground(Color.WHITE);
        btnGuardarAtleta.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnCancelarEdicion = new JButton("Cancelar");
        btnCancelarEdicion.setBackground(Color.GRAY);
        btnCancelarEdicion.setForeground(Color.WHITE);
        btnCancelarEdicion.setVisible(false); // Oculto por defecto, solo visible al editar

        // Panel inferior para los botones del formulario
        JPanel panelAccionesForm = new JPanel(new GridLayout(1, 2, 10, 0));
        panelAccionesForm.add(btnCancelarEdicion);
        panelAccionesForm.add(btnGuardarAtleta);

        camposGrid.add(new JLabel()); // Espacio en blanco estructural
        camposGrid.add(panelAccionesForm);

        panelFormulario.add(camposGrid, BorderLayout.NORTH);
        panelCentral.add(panelFormulario);

        // ==========================================
        // COLUMNA DERECHA: Tabla de Atletas Registrados
        // ==========================================
        JPanel panelTabla = new JPanel(new BorderLayout(10, 10));
        panelTabla.setBorder(BorderFactory.createTitledBorder("Atletas en el Sistema"));

        // Definimos las columnas de nuestra tabla de datos
        String[] columnas = {"ID", "Nombre", "Club/País", "Categoría", "Cinturón"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Desactiva la edición directa sobre la celda de la tabla
            }
        };

        tablaAtletas = new JTable(modeloTabla);
        tablaAtletas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTabla = new JScrollPane(tablaAtletas);

        // Botones de acción para la tabla
        JButton btnEditarSeleccionado = new JButton("Editar Seleccionado");
        btnEditarSeleccionado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JPanel panelAccionesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelAccionesTabla.add(btnEditarSeleccionado);

        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        panelTabla.add(panelAccionesTabla, BorderLayout.SOUTH);

        panelCentral.add(panelTabla);
        add(panelCentral, BorderLayout.CENTER);

        // ==========================================
        // MANEJADORES DE EVENTOS (LISTENERS)
        // ==========================================

        // Evento: Registrar o Actualizar Atleta
        btnGuardarAtleta.addActionListener(e -> guardarOActualizarAtleta());

        // Evento: Cancelar modo edición
        btnCancelarEdicion.addActionListener(e -> limpiarFormulario());

        // Evento: Cargar datos seleccionados en el formulario para editar
        btnEditarSeleccionado.addActionListener(e -> activarModoEdicion());
    }

    /**
     * Consulta la base de datos para extraer todos los atletas y repoblar la JTable.
     */
    private void cargarAtletasEnTabla() {
        modeloTabla.setRowCount(0); // Limpiar filas previas
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return;

        String sql = "SELECT id_atleta, nombre, pais, categoria_peso, cinturon FROM atleta ORDER BY id_atleta DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                        rs.getInt("id_atleta"),
                        rs.getString("nombre"),
                        rs.getString("pais"),
                        rs.getString("categoria_peso"),
                        rs.getString("cinturon")
                });
            }
        } catch (SQLException ex) {
            System.out.println("[PanelAtletas] Error al cargar lista de atletas: " + ex.getMessage());
        }
    }

    /**
     * Procesa la inserción o actualización de datos según el estado de la variable 'idAtletaEdicion'.
     */
    private void guardarOActualizarAtleta() {
        String nombre = txtNombre.getText().trim();
        String club = txtClub.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String cinturon = txtCinturon.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del competidor es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (idAtletaEdicion == null) {
            // --- MODO: NUEVO REGISTRO ---
            boolean ok = app.registrarAtletaEnBD(nombre, club, categoria, cinturon);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Atleta registrado exitosamente.");
                limpiarFormulario();
                cargarAtletasEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el competidor. Verifique la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // --- MODO: EDICIÓN / ACTUALIZACIÓN ---
            boolean ok = actualizarAtletaEnBD(idAtletaEdicion, nombre, club, categoria, cinturon);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Datos del atleta actualizados correctamente.");
                limpiarFormulario();
                cargarAtletasEnTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Fallo al actualizar. Intente de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Toma el atleta seleccionado de la tabla y lo carga en el formulario izquierdo,
     * mutando la interfaz a Modo Edición.
     */
    private void activarModoEdicion() {
        int filaSeleccionada = tablaAtletas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un competidor de la tabla.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Recuperar valores directo del modelo de la tabla
        idAtletaEdicion = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        txtNombre.setText((String) modeloTabla.getValueAt(filaSeleccionada, 1));
        txtClub.setText((String) modeloTabla.getValueAt(filaSeleccionada, 2));
        txtCategoria.setText((String) modeloTabla.getValueAt(filaSeleccionada, 3));
        txtCinturon.setText((String) modeloTabla.getValueAt(filaSeleccionada, 4));

        // Adaptar UI visualmente al modo edición
        btnGuardarAtleta.setText("Actualizar Datos");
        btnGuardarAtleta.setBackground(Color.ORANGE);
        btnGuardarAtleta.setForeground(Color.BLACK);
        btnCancelarEdicion.setVisible(true);
    }

    /**
     * Restablece el formulario a su estado original de registro limpio.
     */
    private void limpiarFormulario() {
        idAtletaEdicion = null;
        txtNombre.setText("");
        txtClub.setText("");
        txtCategoria.setText("");
        txtCinturon.setText("");

        // Restaurar aspecto del botón principal
        btnGuardarAtleta.setText("Registrar Competidor");
        btnGuardarAtleta.setBackground(VentanaPrincipal.COLOR_AZUL_TKD);
        btnGuardarAtleta.setForeground(Color.WHITE);
        btnCancelarEdicion.setVisible(false);
        tablaAtletas.clearSelection();
    }

    /**
     * Query SQL directa para actualizar los datos editados de un competidor en SQL Server.
     */
    private boolean actualizarAtletaEnBD(int id, String nombre, String pais, String categoria, String cinturon) {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return false;

        String sql = "UPDATE atleta SET nombre = ?, pais = ?, categoria_peso = ?, cinturon = ? WHERE id_atleta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, pais);
            ps.setString(3, categoria);
            ps.setString(4, cinturon);
            ps.setInt(5, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println("[PanelAtletas] Error al ejecutar actualización SQL: " + ex.getMessage());
            return false;
        }
    }
}