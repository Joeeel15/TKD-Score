package com.tkdscore.ui.paneles;

import com.tkdscore.facade.MarcadorFacade;
import com.tkdscore.memento.IVRCaretaker;
import com.tkdscore.proxy.AccesoNoAutorizadoException;
import com.tkdscore.proxy.ArbitroProxy;
import com.tkdscore.ui.VentanaPrincipal;

import javax.swing.*;
import java.awt.*;

/**
 * Clase PanelLogin que representa la interfaz de acceso de seguridad del sistema.
 * Solicita el nombre y la credencial oficial del árbitro, validándola mediante un Proxy de acceso.
 * Una vez validada la identidad, inicializa la fachada centralizada del marcador junto con su
 * sistema de histórico Memento (Caretaker) para habilitar el resto de la aplicación.
 */
public class PanelLogin extends JPanel {

    private final VentanaPrincipal app;

    /**
     * Constructor del panel de login.
     * Define un esquema de posicionamiento flexible GridBagLayout ideal para centrar formularios.
     * * @param app Referencia a la ventana principal para coordinar la navegación e inicialización del sistema.
     */
    public PanelLogin(VentanaPrincipal app) {
        super(new GridBagLayout());
        this.app = app;
        construirUI();
    }

    /**
     * Ensambla todos los componentes del formulario de acceso utilizando GridBagConstraints 
     * para organizar con precisión las etiquetas, campos de texto y botones en una rejilla.
     */
    private void construirUI() {
        setBackground(Color.WHITE); // Fondo limpio y minimalista
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Padding lateral interno para desahogo visual
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Hace que los componentes se expandan horizontalmente en su celda
        gbc.insets = new Insets(8, 8, 8, 8); // Margen interno constante de 8px alrededor de cada componente

        // --- TÍTULO PRINCIPAL DE LA PANTALLA ---
        JLabel lblTitulo = new JLabel("ACCESO DE ÁRBITRO OFICIAL", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(VentanaPrincipal.COLOR_MENU_LATERAL);
        
        gbc.gridwidth = 2; // El título ocupará las dos columnas del Grid
        gbc.gridy = 0;     // Fila 0
        gbc.gridx = 0;     // Columna 0
        add(lblTitulo, gbc);

        // --- CAMPO: NOMBRE DEL ÁRBITRO ---
        gbc.gridwidth = 1; // Restablecer el ancho de celdas a 1 columna
        gbc.gridy = 1;     // Fila 1
        gbc.gridx = 0;     // Columna 0
        add(new JLabel("Nombre del Árbitro:"), gbc);

        JTextField txtNombre = new JTextField(15);
        gbc.gridx = 1;     // Columna 1
        add(txtNombre, gbc);

        // --- CAMPO: CREDENCIAL DE SEGURIDAD (PASSWORD) ---
        gbc.gridx = 0;     // Volver a columna 0
        gbc.gridy = 2;     // Fila 2
        add(new JLabel("Credencial WT:"), gbc);

        // Nota: Se utiliza un JPasswordField para enmascarar la credencial de seguridad del árbitro
        JTextField txtCredencial = new JPasswordField(15);
        gbc.gridx = 1;     // Columna 1
        add(txtCredencial, gbc);

        // --- BOTÓN: INGRESAR AL SISTEMA ---
        JButton btnIngresar = new JButton("Ingresar al Sistema");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setBackground(VentanaPrincipal.COLOR_AZUL_TKD);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false); // Elimina el molesto recuadro de foco interno de Swing
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mano al pasar por encima

        gbc.gridx = 0;     // Fila de ancho doble iniciada en columna 0
        gbc.gridy = 3;     // Fila 3
        gbc.gridwidth = 2; // Expansión sobre ambas columnas
        gbc.insets = new Insets(20, 8, 8, 8); // Margen superior extra de 20px para separar del formulario
        add(btnIngresar, gbc);

        // --- MANEJADOR DE EVENTO ---
        // Se capturan los textos limpios (sin espacios en blanco indeseados) al pulsar el botón de ingreso
        btnIngresar.addActionListener(e -> intentarIngresar(txtNombre.getText().trim(), txtCredencial.getText().trim()));
    }

    /**
     * Valida los datos ingresados en el formulario de login.
     * Utiliza un proxy para autorizar al árbitro y, si tiene éxito, genera la fachada centralizada
     * de administración (Facade) y arranca la sesión dentro de la aplicación principal.
     * * @param nombre     Nombre ingresado por el usuario.
     * @param credencial Código de credencial de arbitraje oficial.
     */
    private void intentarIngresar(String nombre, String credencial) {
        // Validación local de campos no vacíos
        if (nombre.isEmpty() || credencial.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Por favor, complete todos los campos requeridos.", 
                    "Campos Vacíos", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Instanciación e intento de validación del árbitro oficial mediante el Patrón Proxy.
            // Si las credenciales no son válidas de acuerdo con la federación, arrojará AccesoNoAutorizadoException.
            ArbitroProxy arbitroValidado = new ArbitroProxy(nombre, credencial);

            // 2. Inicialización de la Fachada (Facade) que coordinará el marcador, el árbitro en sesión
            // y el conserje (Caretaker) encargado de los rollbacks de puntuación (Memento / Instantáneas del marcador).
            MarcadorFacade facade = new MarcadorFacade(app.getMarcador(), arbitroValidado, new IVRCaretaker());

            // 3. Sincronizar o crear el registro del oficial en la base de datos para obtener su ID relacional
            Integer idArbitro = app.obtenerOCrearArbitro(nombre, credencial);

            // 4. Delegar en el contenedor de nivel superior el cambio a la pantalla de módulos del torneo
            app.iniciarSesion(facade, idArbitro, nombre);

        } catch (AccesoNoAutorizadoException ex) {
            // Excepción atrapada si el proxy rechaza la credencial
            JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Acceso Denegado", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}