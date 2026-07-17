package com.tkdscore.facade;

// Importaciones de decoradores para añadir puntos extra dinámicamente
import com.tkdscore.decorator.BonoCabeza;
import com.tkdscore.decorator.BonoGiro;
// Importaciones de Memento para la funcionalidad de deshacer (IVR - Instant Video Replay)
import com.tkdscore.memento.IVRCaretaker;
import com.tkdscore.memento.MarcadorMemento;
// Importación del Observer que notifica los cambios de puntaje a las pantallas
import com.tkdscore.observer.Marcador;
// Importación del Proxy de seguridad y registro de árbitros
import com.tkdscore.proxy.ArbitroProxy;
// Importación del Singleton de conexión a base de datos
import com.tkdscore.singleton.ConexionBD;
// Importaciones del Factory de técnicas de combate
import com.tkdscore.tecnica.ITecnica;
import com.tkdscore.tecnica.TecnicaFactory;

// Importaciones necesarias para operaciones SQL (JDBC)
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase MarcadorFacade que actúa como una Fachada (Facade Pattern).
 * Centraliza y coordina el flujo de negocio del marcador de Taekwondo, simplificando
 * las llamadas complejas a base de datos, lógica de puntos, guardado de mementos y alertas.
 */
public class MarcadorFacade {

    // Componentes del sistema que coordina la fachada
    private final Marcador marcador;              // El sujeto/observable del marcador en tiempo real
    private final ArbitroProxy arbitro;            // Proxy para registrar acciones del árbitro con seguridad
    private final IVRCaretaker ivrCaretaker;      // Encargado de almacenar los estados previos (Mementos) para IVR

    /**
     * Constructor de la fachada. Inicializa las referencias de los subsistemas.
     */
    public MarcadorFacade(Marcador marcador, ArbitroProxy arbitro, IVRCaretaker ivrCaretaker) {
        this.marcador = marcador;
        this.arbitro = arbitro;
        this.ivrCaretaker = ivrCaretaker;
        // Garantiza que la instancia única de la base de datos esté creada al iniciar la fachada
        ConexionBD.obtenerInstancia();
    }

    /**
     * Registra un punto en el marcador, calcula bonos usando Decorator,
     * guarda un Memento para posible reversión y persiste la información en la BD.
     */
    public void anotarPunto(int idCombate, Integer idArbitro, String atleta, String tipoTecnica,
                            boolean conBonoGiro, boolean conBonoCabeza) {
        // 1. Respaldar puntajes previos para crear el Memento (por si se solicita IVR)
        int puntosAzulAntes = marcador.getPuntosAzul();
        int puntosRojoAntes = marcador.getPuntosRojo();

        // 2. Usar Factory para instanciar la técnica base de Taekwondo (ej. Patada, Puño)
        ITecnica tecnica = TecnicaFactory.crearTecnica(tipoTecnica);
        int puntosBase = tecnica.calcularPuntos();

        // 3. Aplicar dinámicamente los Decoradores según los bonos solicitados
        if (conBonoGiro) {
            tecnica = new BonoGiro(tecnica); // Suma +1 punto
        }
        if (conBonoCabeza) {
            tecnica = new BonoCabeza(tecnica); // Suma +2 puntos
        }
        
        // 4. Calcular el puntaje final decorado y la bonificación total sumada
        int puntosFinales = tecnica.calcularPuntos();
        int bonificacion = puntosFinales - puntosBase;

        // 5. Utilizar el Proxy para validar y registrar la acción técnica del árbitro
        arbitro.registrarPunto(atleta, tecnica);

        // 6. Persistir el punto en la Base de Datos y capturar su ID autogenerado
        Integer idPunto = guardarPuntoEnBD(idCombate, idArbitro, tipoTecnica, puntosBase, bonificacion, atleta);

        // 7. Guardar el estado previo en el Caretaker del Memento para permitir "Deshacer" (IVR)
        ivrCaretaker.guardar(new MarcadorMemento(puntosAzulAntes, puntosRojoAntes, idPunto, idCombate));

        // 8. Actualizar el marcador en tiempo real (notificará automáticamente a los observadores/pantallas)
        marcador.actualizarPuntaje(atleta, puntosFinales);
    }

    /**
     * Registra una sanción (Kyong-go / Gam-jeom) en un combate.
     * Al sancionar a un atleta, su oponente recibe automáticamente +1 punto.
     */
    public void registrarSancion(int idCombate, int idAtletaSancionado, String colorSancionado, String motivo) {
        // Guardar la penalización en la BD
        guardarSancionEnBD(idCombate, idAtletaSancionado, motivo);
        
        // Determinar quién es el oponente para sumarle el punto de penalización
        String colorOponente = "AZUL".equalsIgnoreCase(colorSancionado) ? "ROJO" : "AZUL";
        
        // El oponente recibe un punto de manera automática
        marcador.actualizarPuntaje(colorOponente, 1);
    }

    /**
     * Inserta una penalización directo en la base de datos.
     */
    private void guardarSancionEnBD(int idCombate, int idAtleta, String motivo) {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return;
        
        String sql = "INSERT INTO penalizacion (id_combate, id_atleta, motivo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCombate);
            ps.setInt(2, idAtleta);
            ps.setString(3, motivo);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al guardar la sancion: " + ex.getMessage());
        }
    }

    /**
     * Revierte la última acción de puntuación efectuada mediante una revisión de video (IVR).
     * @return true si se pudo restaurar con éxito, false en caso contrario.
     */
    public boolean revertirUltimoPunto() {
        // 1. Obtener el último estado guardado en el Caretaker (Memento)
        MarcadorMemento anterior = ivrCaretaker.restaurar();
        if (anterior == null) {
            System.out.println("[MarcadorFacade] No hay puntos previos para revertir.");
            return false;
        }
        
        // 2. Restaurar los puntajes del marcador a sus valores anteriores
        marcador.restaurarEstado(anterior.getPuntosAzul(), anterior.getPuntosRojo());
        
        // 3. Registrar en la BD que dicho punto ha sido anulado por auditoría/revisión IVR
        guardarRevisionEnBD(anterior.getIdCombate(), anterior.getIdPunto());
        System.out.println("[MarcadorFacade] Punto revertido por revision IVR.");
        return true;
    }

    /**
     * Inserta un punto en la tabla "punto" de la BD y retorna su ID generado automáticamente.
     */
    private Integer guardarPuntoEnBD(int idCombate, Integer idArbitro, String tipoTecnica,
                                     int puntosBase, int bonificacion, String atleta) {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null || idArbitro == null) {
            System.out.println("[MarcadorFacade] No se persistio el punto (sin conexion o sin arbitro de sesion).");
            return null;
        }
        
        String sql = "INSERT INTO punto (id_combate, id_arbitro, tipo_tecnica, puntos_base, bonificacion, atleta_anotador) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idCombate);
            ps.setInt(2, idArbitro);
            ps.setString(3, tipoTecnica);
            ps.setInt(4, puntosBase);
            ps.setInt(5, bonificacion);
            ps.setString(6, atleta);
            ps.executeUpdate();
            
            // Recuperar el ID generado automáticamente por la BD (serial / autoincrementable)
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al guardar el punto: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Inserta el registro de anulación en la tabla "revision_ivr" en la BD.
     */
    private void guardarRevisionEnBD(int idCombate, Integer idPunto) {
        if (idPunto == null) {
            System.out.println("[MarcadorFacade] No se guardo la revision (el punto no tenia id, no se persistio en su momento).");
            return;
        }
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return;

        String sql = "INSERT INTO revision_ivr (id_combate, id_punto, resultado) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCombate);
            ps.setInt(2, idPunto);
            ps.setString(3, "ANULADO"); // Marcamos el punto auditado como inhabilitado
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al guardar la revision IVR: " + ex.getMessage());
        }
    }

    /**
     * Calcula y retorna el puntaje acumulado actual (Azul vs Rojo) cargando los datos de la BD,
     * sumando puntos por técnicas y aplicando puntos extras derivados de sanciones del rival.
     */
    public int[] cargarPuntajeDesdeBD(int idCombate) {
        int[] totales = {0, 0}; // totales[0] = Azul, totales[1] = Rojo
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return totales;

        // 1. Consultar y sumar los puntos válidos de las técnicas efectuadas
        String sql = "SELECT atleta_anotador, SUM(puntos_base + bonificacion) AS total "
                + "FROM punto WHERE id_combate = ? GROUP BY atleta_anotador";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCombate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String atleta = rs.getString("atleta_anotador");
                    int total = rs.getInt("total");
                    if ("AZUL".equalsIgnoreCase(atleta)) {
                        totales[0] = total;
                    } else {
                        totales[1] = total;
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al cargar puntaje: " + ex.getMessage());
        }

        // 2. Sumar puntos extras derivados de las amonestaciones/sanciones aplicadas al oponente
        int[] idsAtletas = obtenerIdsAtletas(idCombate);
        if (idsAtletas != null) {
            int idAzul = idsAtletas[0];
            int idRojo = idsAtletas[1];
            String sqlPenal = "SELECT id_atleta, COUNT(*) AS cantidad FROM penalizacion WHERE id_combate = ? GROUP BY id_atleta";
            try (PreparedStatement ps = con.prepareStatement(sqlPenal)) {
                ps.setInt(1, idCombate);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int idAtletaSancionado = rs.getInt("id_atleta");
                        int cantidad = rs.getInt("cantidad");
                        if (idAtletaSancionado == idAzul) {
                            // Si el azul fue sancionado, el rojo gana los puntos adicionales
                            totales[1] += cantidad;
                        } else if (idAtletaSancionado == idRojo) {
                            // Si el rojo fue sancionado, el azul gana los puntos adicionales
                            totales[0] += cantidad;
                        }
                    }
                }
            } catch (SQLException ex) {
                System.out.println("[MarcadorFacade] Error al cargar sanciones: " + ex.getMessage());
            }
        }
        return totales;
    }

    /**
     * Recupera los identificadores de BD de los dos atletas participantes en un combate.
     * @return Arreglo donde [0] es ID Atleta Azul y [1] es ID Atleta Rojo.
     */
    private int[] obtenerIdsAtletas(int idCombate) {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return null;
        String sql = "SELECT id_atleta_azul, id_atleta_rojo FROM combate WHERE id_combate = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCombate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[]{rs.getInt("id_atleta_azul"), rs.getInt("id_atleta_rojo")};
                }
            }
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al obtener atletas del combate: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Actualiza en la base de datos el estado actual del combate y la ronda en curso.
     */
    public void guardarEstadoCombate(int idCombate, String estadoNombre, int rondaActual) {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return;
        String sql = "UPDATE combate SET estado_actual = ?, ronda_actual = ? WHERE id_combate = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estadoNombre);
            ps.setInt(2, rondaActual);
            ps.setInt(3, idCombate);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al guardar el estado del combate: " + ex.getMessage());
        }
    }

    /**
     * Recupera el estado guardado y la ronda en curso de un combate específico de la BD.
     */
    public Object[] cargarEstadoCombate(int idCombate) {
        Object[] resultado = {"Ronda en curso", 1};
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return resultado;

        String sql = "SELECT estado_actual, ronda_actual FROM combate WHERE id_combate = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCombate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String estado = rs.getString("estado_actual");
                    int ronda = rs.getInt("ronda_actual");
                    if (estado != null) resultado[0] = estado;
                    if (ronda > 0) resultado[1] = ronda;
                }
            }
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al cargar el estado del combate: " + ex.getMessage());
        }
        return resultado;
    }

    /**
     * Compara el marcador actual y guarda el ganador en la tabla "combate".
     * Se debe llamar cuando el combate pasa a estado "Finalizado".
     * Si hay empate, no se guarda ganador (queda en NULL). Devuelve el id
     * del atleta ganador, o null si hubo empate o error.
     */
    public Integer guardarGanador(int idCombate) {
        int[] idsAtletas = obtenerIdsAtletas(idCombate);
        if (idsAtletas == null) return null;

        int puntosAzul = marcador.getPuntosAzul();
        int puntosRojo = marcador.getPuntosRojo();

        Integer idGanador = null;
        if (puntosAzul > puntosRojo) {
            idGanador = idsAtletas[0]; // Gana Atleta Azul
        } else if (puntosRojo > puntosAzul) {
            idGanador = idsAtletas[1]; // Gana Atleta Rojo
        }

        if (idGanador == null) {
            System.out.println("[MarcadorFacade] Combate empatado (" + puntosAzul + "-" + puntosRojo + "), no se registro ganador.");
            return null;
        }

        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return null;
        String sql = "UPDATE combate SET id_ganador = ? WHERE id_combate = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGanador);
            ps.setInt(2, idCombate);
            ps.executeUpdate();
            System.out.println("[MarcadorFacade] Ganador guardado: atleta #" + idGanador);
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al guardar el ganador: " + ex.getMessage());
            return null;
        }
        return idGanador;
    }

    /** * Borra el ganador asignado a un combate (útil al reestablecer peleas finalizadas). 
     */
    public void borrarGanador(int idCombate) {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return;
        String sql = "UPDATE combate SET id_ganador = NULL WHERE id_combate = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCombate);
            ps.executeUpdate();
            System.out.println("[MarcadorFacade] Ganador borrado para combate #" + idCombate);
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al borrar el ganador: " + ex.getMessage());
        }
    }

    /** * Consulta y devuelve el id del atleta ganador registrado para un combate.
     */
    public Integer cargarGanador(int idCombate) {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return null;
        String sql = "SELECT id_ganador FROM combate WHERE id_combate = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCombate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idGanador = rs.getInt("id_ganador");
                    return rs.wasNull() ? null : idGanador;
                }
            }
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al cargar el ganador: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Reinicia TODO el torneo: borra todos los puntos, sanciones y revisiones IVR,
     * y deja cada combate en estado inicial (Ronda 1, sin ganador). No borra atletas
     * ni los emparejamientos (id_atleta_azul / id_atleta_rojo se mantienen).
     */
    public boolean borrarTorneoCompleto() {
        Connection con = ConexionBD.obtenerInstancia().getConexion();
        if (con == null) return false;
        try {
            // Desactivar el autoCommit para manejar transacciones manuales seguras
            con.setAutoCommit(false);
            
            try (Statement st = con.createStatement()) {
                // Borrar datos derivados respetando las claves foráneas
                st.executeUpdate("DELETE FROM revision_ivr");
                st.executeUpdate("DELETE FROM punto");
                st.executeUpdate("DELETE FROM penalizacion");
                // Resetear estados y ganadores en la tabla principal de combates
                st.executeUpdate("UPDATE combate SET estado_actual = 'RONDA_1', ronda_actual = 1, id_ganador = NULL");
            }
            
            // Confirmar transacción si todo fue exitoso
            con.commit();
            System.out.println("[MarcadorFacade] Torneo reiniciado por completo.");
            return true;
        } catch (SQLException ex) {
            System.out.println("[MarcadorFacade] Error al reiniciar el torneo: " + ex.getMessage());
            try { 
                con.rollback(); // Cancelar cambios si ocurrió algún error imprevisto
            } catch (SQLException ignored) {}
            return false;
        } finally {
            try { 
                con.setAutoCommit(true); // Restaurar comportamiento por defecto
            } catch (SQLException ignored) {}
        }
    }

    /**
     * Getter para obtener la instancia del marcador observable coordinado.
     */
    public Marcador getMarcador() {
        return marcador;
    }
}