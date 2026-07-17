package com.tkdscore.singleton;

// Importamos las clases necesarias de JDBC para gestionar la conexión y los drivers de SQL Server
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase ConexionBD que implementa el patrón de diseño Singleton.
 * Asegura la existencia de una única instancia de conexión a la base de datos 
 * en toda la aplicación, proporcionando un punto de acceso global y seguro a ella.
 */
public class ConexionBD {

    /**
     * Variable estática privada que almacenará la única instancia permitida de esta clase.
     * Al ser estática, pertenece a la clase y persiste durante la ejecución de la aplicación.
     */
    private static ConexionBD instancia;
    
    // Objeto Connection propio de la API JDBC que mantiene el canal físico abierto con SQL Server
    private Connection conexion;

    // Configuración de los parámetros de conexión hacia Microsoft SQL Server (Puerto por defecto: 1433)
    private static final String URL =
        "jdbc:sqlserver://localhost:1433;databaseName=tkdscore;encrypt=false;trustServerCertificate=true";
    private static final String USUARIO = "sa";      // Usuario administrador por defecto de SQL Server
    private static final String CLAVE = "Admin123";  // Contraseña de acceso a la base de datos

    /**
     * Constructor Privado.
     * Al declararse como 'private', se prohíbe terminantemente la creación de instancias 
     * externas usando la palabra reservada 'new' (ej. new ConexionBD() dará error de compilación).
     */
    private ConexionBD() {
        try {
            // 1. Cargar el Driver JDBC de SQL Server de forma dinámica en memoria
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            // 2. Establecer el canal físico de conexión mediante el DriverManager
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
            System.out.println("[ConexionBD] Conexion establecida con SQL Server.");
        } catch (ClassNotFoundException | SQLException e) {
            // Captura errores de falta de librerías del Driver o credenciales/dirección IP incorrectas
            System.out.println("[ConexionBD] No se pudo conectar a SQL Server: " + e.getMessage());
        }
    }

    /**
     * Método de acceso global estático (Punto de Entrada del Singleton).
     * Se encarga de evaluar si la conexión ya ha sido creada con anterioridad.
     * Si no existe, invoca al constructor privado por única vez; de lo contrario, 
     * retorna la conexión ya existente en memoria.
     * * * @return La instancia única de ConexionBD.
     */
    public static ConexionBD obtenerInstancia() {
        // Evaluación perezosa (Lazy Initialization): Se crea únicamente cuando se solicita por primera vez
        if (instancia == null) {
            instancia = new ConexionBD(); // Llamada exclusiva al constructor privado
        }
        return instancia; // Retorna la instancia única existente
    }

    /**
     * Método getter para obtener el objeto de conexión activo.
     * Permite que otros paquetes o clases (como MarcadorFacade) ejecuten consultas SQL (Queries).
     * * * @return Objeto Connection de JDBC activo.
     */
    public Connection getConexion() {
        return conexion;
    }
}