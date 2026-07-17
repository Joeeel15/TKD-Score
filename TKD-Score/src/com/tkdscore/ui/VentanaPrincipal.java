package com.tkdscore.ui;

// Importaciones de lógica de negocio y patrones de diseño
import com.tkdscore.facade.MarcadorFacade;
import com.tkdscore.observer.Marcador;
import com.tkdscore.observer.PanelJueces;
import com.tkdscore.observer.PantallaElectronica;
import com.tkdscore.observer.SistemaSonido;
import com.tkdscore.singleton.ConexionBD;
import com.tkdscore.state.Combate;
import com.tkdscore.ui.paneles.PanelAtletas;
import com.tkdscore.ui.paneles.PanelConfiguracion;
import com.tkdscore.ui.paneles.PanelHistorial;
import com.tkdscore.ui.paneles.PanelLogin;
import com.tkdscore.ui.paneles.PanelMarcador;
import com.tkdscore.ui.paneles.PanelPlanificacion;

// Importaciones de la biblioteca gráfica Swing y AWT
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase VentanaPrincipal que actúa como la ventana contenedora principal (JFrame) del sistema.
 * Gestiona el menú de navegación lateral, el intercambio dinámico de paneles mediante CardLayout,
 * y centraliza el estado global de la sesión del árbitro, el combate activo y los componentes observables.
 */
public class VentanaPrincipal extends JFrame {

    // ==========================================
    // CONSTANTES DE DISEÑO (Paleta de Colores)
    // ==========================================
    public static final Color COLOR_FONDO_OSCURO = new Color(33, 37, 41);
    public static final Color COLOR_MENU_LATERAL = new Color(43, 48, 53);
    public static final Color COLOR_AZUL_TKD = new Color(13, 110, 253);
    public static final Color COLOR_ROJO_TKD = new Color(220, 53, 69);
    public static final Color COLOR_TEXTO_MUTED = new Color(108, 117, 125);

    // ==========================================
    // ATRIBUTOS DE LÓGICA Y PATRONES
    // ==========================================
    private MarcadorFacade facade;            // Fachada para simplificar las llamadas al subsistema de persistencia
    private final Combate combate = new Combate(); // Contexto del patrón State que controla los asaltos
    private final Marcador marcador;          // Sujeto Observable que almacena el puntaje en tiempo real

    // ==========================================
    // COMPONENTES GRÁFICOS (Layouts y Paneles)
    // ==========================================
    private JPanel contenedorNavegacion;      // Contenedor de nivel superior (Login vs App)
    private CardLayout navegadorLayout;        // Layout de nivel superior

    private JPanel contenedorModulos;         // Contenedor intermedio para las secciones de la app
    private CardLayout modulosLayout;          // Layout para alternar entre pantallas de gestión

    private PanelPlanificacion panelPlanificacion;
    private PanelHistorial panelHistorial;
    private PanelMarcador panelMarcador;

    // ==========================================
    // ESTADO DE LA SESIÓN Y COMBATE ACTIVO
    // ==========================================
    private Integer idCombateActivo;
    private Integer idAtletaAzulActivo;
    private Integer idAtletaRojoActivo;
    private String nombreAzulActivo;
    private String nombreRojoActivo;
    private Integer idArbitroSesion;
    private String nombreArbitroSesion;

    /**
     * Constructor de VentanaPrincipal.
     * Inicializa los observadores de puntuación, configura las propiedades de la ventana
     * y arranca la interfaz mostrando por defecto la pantalla de acceso (Login).
     */
    public VentanaPrincipal() {
        super("TKD-Score | Sistema de Gestión de Campeonatos");

        // 1. Inicializar el marcador centralizado (Patrón Observer)
        this.marcador = new Marcador();
        
        // 2. Adjuntar los observadores correspondientes al marcador
        this.marcador.agregarObservador(new PantallaElectronica());
        this.marcador.agregarObservador(new SistemaSonido());
        this.marcador.agregarObservador(new PanelJueces());

        // 3. Configuración estándar de la ventana de Swing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla

        // 4. Configurar la navegación principal con CardLayout e iniciar con el Panel de Login
        navegadorLayout = new CardLayout();
        contenedorNavegacion = new JPanel(navegadorLayout);
        contenedorNavegacion.add(new PanelLogin(this), "NAVEGACION_LOGIN");

        add(contenedorNavegacion);
        navegadorLayout.show(contenedorNavegacion, "NAVEGACION_LOGIN");
    }

    // =========================================================================
    // GESTIÓN DE SESIÓN Y NAVEGACIÓN ENTRE MÓDULOS
    // =========================================================================

    /**
     * Inicializa formalmente la interfaz de la aplicación una vez que las credenciales
     * del árbitro han sido validadas correctamente a través de la pantalla de login.
     * * @param facade       Fachada configurada con el proxy de arbitraje activo.
     * @param idArbitro    Identificador único del árbitro en sesión.
     * @param nombreArbitro Nombre completo del árbitro autenticado.
     */
    public void iniciarSesion(MarcadorFacade facade, Integer idArbitro, String nombreArbitro) {
        this.facade = facade;
        this.idArbitroSesion = idArbitro;
        this.nombreArbitroSesion = nombreArbitro;
        
        // Construye y añade la estructura completa del ecosistema del torneo a la navegación
        contenedorNavegacion.add(construirEstructuraApp(), "NAVEGACION_APP");
        navegadorLayout.show(contenedorNavegacion, "NAVEGACION_APP");
    }

    /**
     * Ensambla la barra lateral de navegación y el área principal de trabajo de la aplicación.
     * * @return Panel estructurado con BorderLayout que contiene el menú a la izquierda y las vistas al centro.
     */
    private JPanel construirEstructuraApp() {
        JPanel panelBase = new JPanel(new BorderLayout());

        // --- BARRA LATERAL (MENÚ DE MENÚS) ---
        JPanel panelMenuLateral = new JPanel();
        panelMenuLateral.setLayout(new BoxLayout(panelMenuLateral, BoxLayout.Y_AXIS));
        panelMenuLateral.setBackground(COLOR_MENU_LATERAL);
        panelMenuLateral.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));

        JLabel lblMenuTitulo = new JLabel("TKD ECOSYSTEM");
        lblMenuTitulo.setForeground(Color.WHITE);
        lblMenuTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMenuTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelMenuLateral.add(lblMenuTitulo);
        panelMenuLateral.add(Box.createVerticalStrut(25));

        // Creación de los botones del menú de opciones
        JButton btnAtletas = crearBotonMenu("Atletas");
        JButton btnPlanificacion = crearBotonMenu("Planificación");
        JButton btnMarcador = crearBotonMenu("Ring Digital");
        JButton btnHistorial = crearBotonMenu("Historial");
        JButton btnConfig = crearBotonMenu("Configuración");

        // Agregar botones con espaciadores para un diseño limpio
        panelMenuLateral.add(btnAtletas); panelMenuLateral.add(Box.createVerticalStrut(10));
        panelMenuLateral.add(btnPlanificacion); panelMenuLateral.add(Box.createVerticalStrut(10));
        panelMenuLateral.add(btnMarcador); panelMenuLateral.add(Box.createVerticalStrut(10));
        panelMenuLateral.add(btnHistorial); panelMenuLateral.add(Box.createVerticalStrut(10));
        panelMenuLateral.add(btnConfig);

        panelBase.add(panelMenuLateral, BorderLayout.WEST);

        // --- CONTENEDOR CENTRAL DE MÓDULOS ---
        modulosLayout = new CardLayout();
        contenedorModulos = new JPanel(modulosLayout);

        // Inicialización de paneles que requieren reconstrucción o refresco bajo demanda
        panelPlanificacion = new PanelPlanificacion(this);
        panelHistorial = new PanelHistorial(this);
        panelMarcador = new PanelMarcador(this);

        // Registro de los paneles disponibles en el CardLayout central
        contenedorModulos.add(new PanelAtletas(this), "MODULO_ATLETAS");
        contenedorModulos.add(panelPlanificacion, "MODULO_PLANIFICACION");
        contenedorModulos.add(panelMarcador, "MODULO_MARCADOR");
        contenedorModulos.add(panelHistorial, "MODULO_HISTORIAL");
        contenedorModulos.add(new PanelConfiguracion(this), "MODULO_CONFIG");

        panelBase.add(contenedorModulos, BorderLayout.CENTER);

        // --- MANEJADORES DE EVENTOS DE NAVEGACIÓN ---
        btnAtletas.addActionListener(e -> modulosLayout.show(contenedorModulos, "MODULO_ATLETAS"));
        
        btnPlanificacion.addActionListener(e -> {
            // Re-instanciamos para refrescar la lista de atletas disponibles en la planificación
            contenedorModulos.remove(panelPlanificacion);
            panelPlanificacion = new PanelPlanificacion(this);
            contenedorModulos.add(panelPlanificacion, "MODULO_PLANIFICACION");
            modulosLayout.show(contenedorModulos, "MODULO_PLANIFICACION");
        });
        
        btnMarcador.addActionListener(e -> {
            panelMarcador.refrescarEncabezado();
            modulosLayout.show(contenedorModulos, "MODULO_MARCADOR");
        });
        
        btnHistorial.addActionListener(e -> {
            // Re-instanciamos para cargar los últimos puntos registrados en la base de datos
            contenedorModulos.remove(panelHistorial);
            panelHistorial = new PanelHistorial(this);
            contenedorModulos.add(panelHistorial, "MODULO_HISTORIAL");
            modulosLayout.show(contenedorModulos, "MODULO_HISTORIAL");
        });
        
        btnConfig.addActionListener(e -> modulosLayout.show(contenedorModulos, "MODULO_CONFIG"));

        return panelBase;
    }

    /**
     * Helper para la creación de botones estilizados de la barra de navegación lateral.
     * * @param texto Etiqueta del botón.
     * @return JButton configurado con el estilo visual oscuro uniforme del sistema.
     */
    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Estira el botón a lo ancho del panel lateral
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(60, 66, 73));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    // =========================================================================
    // CONTROL DEL COMBATE ACTIVO
    // =========================================================================

    /**
     * Carga y establece un combate entre dos atletas para iniciar el arbitraje digital.
     * Restaura además el último puntaje y estado (Ronda/Descanso) guardado en la base de datos.
     * * @param azul Atleta asignado a la esquina azul.
     * @param rojo Atleta asignado a la esquina roja.
     */
    public void abrirCombate(AtletaDTO azul, AtletaDTO rojo) {
        // Busca o inserta el combate en la base de datos para obtener su ID único
        Integer idCombate = obtenerOCrearCombate(azul.id, rojo.id);
        if (idCombate == null) return;

        // Establecer el estado activo del combate en memoria
        idCombateActivo = idCombate;
        idAtletaAzulActivo = azul.id;
        idAtletaRojoActivo = rojo.id;
        nombreAzulActivo = azul.nombre;
        nombreRojoActivo = rojo.nombre;

        // 1. Restaurar las puntuaciones acumuladas en el marcador desde la BD
        int[] puntajes = facade.cargarPuntajeDesdeBD(idCombate);
        marcador.restaurarEstado(puntajes[0], puntajes[1]);

        // 2. Restaurar la ronda y el estado del combate (Patrón State) desde la BD
        Object[] estadoGuardado = facade.cargarEstadoCombate(idCombate);
        combate.restaurarDesde((int) estadoGuardado[1], (String) estadoGuardado[0]);

        // Refrescar y redirigir automáticamente a la mesa de control de combate (Ring Digital)
        panelMarcador.refrescarEncabezado();
        modulosLayout.show(contenedorModulos, "MODULO_MARCADOR");
    }

    /**
     * Restablece el marcador a 0-0 y reinicia el estado del combate activo actual (Ronda 1).
     * Sincroniza inmediatamente este cambio en la base de datos para auditoría.
     */
    public void resetearCombateActivo() {
        marcador.restaurarEstado(0, 0);
        if (idCombateActivo != null) {
            combate.reiniciar();
            // Guarda el reinicio en la base de datos
            facade.guardarEstadoCombate(idCombateActivo, combate.getEstadoNombre(), combate.getRondaActual());
            panelMarcador.refrescarEncabezado();
        }
    }

    /**
     * Reinicia por completo los registros de todo el torneo (limpia cascadas de tablas).
     * Deja todos los marcadores y flujos del sistema en sus valores iniciales por defecto.
     * * @return true si la operación fue exitosa en la base de datos, false de lo contrario.
     */
    public boolean reiniciarTorneoCompleto() {
        if (facade == null) return false;
        
        boolean ok = facade.borrarTorneoCompleto();
        if (ok) {
            // Limpia el estado en memoria de la interfaz principal
            marcador.restaurarEstado(0, 0);
            combate.reiniciar();
            idCombateActivo = null;
            idAtletaAzulActivo = null;
            idAtletaRojoActivo = null;
            nombreAzulActivo = null;
            nombreRojoActivo = null;
            panelMarcador.refrescarEncabezado();
        }
        return ok;
    }

    // =========================================================================
    // METODOS DE ACCESO GLOBAL (GETTERS)
    // =========================================================================
    public Marcador getMarcador() { return marcador; }
    public MarcadorFacade getFacade() { return facade; }
    public Combate getCombate() { return combate; }
    public Integer getIdCombateActivo() { return idCombateActivo; }
    public Integer getIdArbitroSesion() { return idArbitroSesion; }
    public String getNombreArbitroSesion() { return nombreArbitroSesion; }
    public boolean isConexionBDActiva() { return ConexionBD.obtenerInstancia().getConexion() != null; }
    public String getNombreAzulActivo() { return nombreAzulActivo; }
    public String getNombreRojoActivo() { return nombreRojoActivo; }
    public Integer getIdAtletaAzulActivo() { return idAtletaAzulActivo; }
    public Integer getIdAtletaRojoActivo() { return idAtletaRojoActivo; }

    // =========================================================================
    // ACCESO A DATOS Y OPERACIONES DIRECTAS DE BASE DE DATOS
    // =========================================================================

    /**
     * Busca en la base de datos si ya existe un árbitro con la credencial dada.
     * Si no existe, crea un nuevo registro con esa credencial de forma automática.
     * * @param nombre     Nombre del árbitro.
     * @param credencial Credencial clave del árbitro (ej. WT-2026).
     * @return ID del árbitro en la base de datos, o null en caso de error.
     */
    public Integer obtenerOCrearArbitro(String nombre, String credencial) {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return null;
        
        // 1. Intentar buscar un árbitro con la credencial ingresada
        try (PreparedStatement ps = con.prepareStatement("SELECT id_arbitro FROM arbitro WHERE credencial = ?")) {
            ps.setString(1, credencial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_arbitro");
            }
        } catch (SQLException ex) {
            System.out.println("[VentanaPrincipal] Error al buscar arbitro: " + ex.getMessage());
        }
        
        // 2. Si no se encontró ningún registro, se inserta como un nuevo árbitro en la BD
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO arbitro (nombre, credencial, nivel_certificacion) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, credencial);
            ps.setString(3, "Nacional");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("[VentanaPrincipal] Error al crear arbitro: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Registra físicamente un atleta nuevo dentro de la base de datos SQL Server.
     * * @param nombre        Nombre completo del deportista.
     * @param pais          País de procedencia o delegación.
     * @param categoriaPeso Rango de peso/categoría (ej. "Men -58kg").
     * @param cinturon      Grado actual del practicante.
     * @return true si la inserción fue exitosa, false en caso de fallo o error de conexión.
     */
    public boolean registrarAtletaEnBD(String nombre, String pais, String categoriaPeso, String cinturon) {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return false;
        
        String sql = "INSERT INTO atleta (nombre, pais, categoria_peso, cinturon) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre); 
            ps.setString(2, pais); 
            ps.setString(3, categoriaPeso); 
            ps.setString(4, cinturon);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println("[VentanaPrincipal] Error al registrar atleta: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Consulta y extrae la lista completa de atletas registrados en el sistema,
     * empaquetándolos en objetos compactos e inmutables (AtletaDTO).
     * * @return Lista de objetos AtletaDTO ordenados por ID de manera ascendente.
     */
    public List<AtletaDTO> listarAtletasCompletos() {
        List<AtletaDTO> lista = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return lista;
        
        try (PreparedStatement ps = con.prepareStatement("SELECT id_atleta, nombre FROM atleta ORDER BY id_atleta");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new AtletaDTO(rs.getInt("id_atleta"), rs.getString("nombre")));
            }
        } catch (SQLException ex) {
            System.out.println("[VentanaPrincipal] Error al listar atletas: " + ex.getMessage());
        }
        return lista;
    }

    /**
     * Busca un encuentro (combate) programado entre dos atletas específicos.
     * Si no existe un enfrentamiento previo entre ambos en el fixture, lo crea automáticamente.
     * * @param idAzul Identificador del competidor en la esquina azul.
     * @param idRojo Identificador del competidor en la esquina roja.
     * @return El identificador numérico único (id_combate) del encuentro deportivo.
     */
    public Integer obtenerOCrearCombate(int idAzul, int idRojo) {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return null;
        
        // 1. Intentar buscar el combate existente entre ambos oponentes
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_combate FROM combate WHERE id_atleta_azul = ? AND id_atleta_rojo = ?")) {
            ps.setInt(1, idAzul); 
            ps.setInt(2, idRojo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_combate");
            }
        } catch (SQLException ex) {
            System.out.println("[VentanaPrincipal] Error al buscar combate: " + ex.getMessage());
            return null;
        }
        
        // 2. Si no existe, insertarlo en la tabla combate para inicializar el enfrentamiento
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO combate (id_atleta_azul, id_atleta_rojo) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idAzul); 
            ps.setInt(2, idRojo);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("[VentanaPrincipal] Error al crear combate: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Recupera el registro histórico de todos los puntos anotados a lo largo del campeonato.
     * Integra de manera relacional la fecha/hora, nombres de los atletas, técnicas y puntajes.
     * * @return Matriz bidimensional de objetos (Object[][]) ideal para poblar un componente JTable.
     */
    public Object[][] obtenerHistorialDesdeBD() {
        List<Object[]> filas = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return new Object[0][];
        
        String sql = "SELECT p.timestamp, az.nombre AS azul, ro.nombre AS rojo, p.tipo_tecnica, "
                + "p.puntos_base, p.bonificacion, p.atleta_anotador "
                + "FROM punto p "
                + "JOIN combate c ON p.id_combate = c.id_combate "
                + "JOIN atleta az ON c.id_atleta_azul = az.id_atleta "
                + "JOIN atleta ro ON c.id_atleta_rojo = ro.id_atleta "
                + "ORDER BY p.timestamp DESC";
                
        try (PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getTimestamp("timestamp"),
                        rs.getString("azul") + " vs " + rs.getString("rojo"),
                        rs.getString("tipo_tecnica"),
                        rs.getInt("puntos_base"),
                        rs.getInt("bonificacion"),
                        rs.getString("atleta_anotador")
                });
            }
        } catch (SQLException ex) {
            System.out.println("[VentanaPrincipal] Error al leer historial: " + ex.getMessage());
        }
        return filas.toArray(new Object[0][]);
    }
}