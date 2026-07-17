package com.tkdscore.ui.paneles;

import com.tkdscore.ui.AtletaDTO;
import com.tkdscore.ui.VentanaPrincipal;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase PanelPlanificacion que representa el módulo del fixture y llaves del torneo.
 * Genera un bracket visual dinámico de eliminación simple. Consulta constantemente la base de datos
 * para verificar los resultados y hacer avanzar automáticamente a los competidores victoriosos
 * por las distintas rondas hasta la gran final.
 */
public class PanelPlanificacion extends JPanel {

    private final VentanaPrincipal app;
    
    // Dimensiones estandarizadas para las tarjetas de combates ordinarios
    private static final int ANCHO_TARJETA = 190;
    private static final int ALTO_TARJETA = 55;
    
    // Dimensiones estandarizadas para la tarjeta de honor del Campeón
    private static final int ANCHO_CAMPEON = 220;
    private static final int ALTO_CAMPEON = 95;

    /**
     * Constructor del panel de planificación de combates.
     * * @param app Referencia a la ventana principal para interactuar con los datos y cambiar de módulos.
     */
    public PanelPlanificacion(VentanaPrincipal app) {
        super(new BorderLayout(15, 15));
        this.app = app;
        construirUI();
    }

    /**
     * Construye la interfaz del módulo. Recupera los atletas inscritos y, si cumple con el mínimo,
     * inicializa el lienzo interactivo del Bracket dentro de un contenedor con barras de desplazamiento.
     */
    private void construirUI() {
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // --- ENCABEZADO DE SECCIÓN ---
        add(new JLabel("<html><h2>Planificación de Llaves (Fixture)</h2>"
                + "<p style='color:grey;'>Los ganadores avanzan automáticamente a la siguiente ronda "
                + "una vez que finalizas su combate en la mesa técnica.</p></html>",
                SwingConstants.LEFT), BorderLayout.NORTH);

        // Recuperar lista de atletas registrados para evaluar la viabilidad del fixture
        List<AtletaDTO> atletas = app.listarAtletasCompletos();

        // Validación: Se requiere un número mínimo de dos atletas para estructurar una competencia
        if (atletas.size() < 2) {
            JPanel vacio = new JPanel(new GridBagLayout());
            JLabel lblMensaje = new JLabel("<html><center style='color:grey;'>"
                    + "<h3>Faltan competidores</h3>"
                    + "Registre al menos 2 atletas en el módulo 'Atletas' para poder armar el bracket."
                    + "</center></html>");
            lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vacio.add(lblMensaje);
            add(vacio, BorderLayout.CENTER);
            return;
        }

        // --- LIENZO DEL BRACKET ---
        BracketPanel bracket = new BracketPanel(atletas);
        JScrollPane scroll = new JScrollPane(bracket);
        
        // Aumentar la velocidad de scroll para una navegación más fluida
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(228, 231, 235)));
        
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Clase interna BracketPanel que actúa como un lienzo (Canvas) de posicionamiento absoluto (null layout).
     * Calcula de forma recursiva/geométrica las coordenadas X, Y de cada ronda, emparejamiento,
     * líneas de conexión y tarjeta final de campeonato.
     */
    private class BracketPanel extends JPanel {

        /**
         * Constructor del panel del bracket.
         * Calcula el número de rondas y las dimensiones totales necesarias según el volumen de competidores.
         * * @param atletas Lista de atletas inscritos para la competición.
         */
        BracketPanel(List<AtletaDTO> atletas) {
            setLayout(null); // Layout nulo necesario para el posicionamiento preciso en ejes coordenados
            setBackground(Color.WHITE);

            // 1. Calcular la potencia de 2 más cercana hacia arriba para establecer los Slots requeridos
            int totalSlots = 2;
            while (totalSlots < atletas.size()) {
                totalSlots *= 2;
            }

            // Completar los espacios vacíos con objetos nulos, los cuales actúan lógicamente como pases libres (BYE)
            List<AtletaDTO> slots = new ArrayList<>(atletas);
            while (slots.size() < totalSlots) {
                slots.add(null); 
            }

            // 2. Determinar variables geométricas y estructurales de las llaves
            int numRondas = (int) (Math.log(totalSlots) / Math.log(2));
            int espacioEntreCartasRonda1 = ALTO_TARJETA + 25; // Espaciado vertical base
            
            // Dimensionamiento total del lienzo de dibujo
            int alturaTotal = (totalSlots / 2) * espacioEntreCartasRonda1 + ALTO_CAMPEON + 40;
            int anchoTotal = numRondas * (ANCHO_TARJETA + 70) + ANCHO_CAMPEON + 60;
            setPreferredSize(new Dimension(anchoTotal, alturaTotal));

            int x = 20;
            int ySeparacion = espacioEntreCartasRonda1;
            int yInicial = 20;

            List<Point> centrosRondaActual = new ArrayList<>();
            List<AtletaDTO> ganadoresRondaActual = new ArrayList<>();

            // ==========================================
            // RONDA 1: Emparejamientos Iniciales del Fixture
            // ==========================================
            for (int i = 0; i < slots.size(); i += 2) {
                AtletaDTO azul = slots.get(i);
                AtletaDTO rojo = slots.get(i + 1);
                int y = yInicial + (i / 2) * ySeparacion;

                // Determinar quién califica o avanza directamente
                AtletaDTO ganador = determinarGanador(azul, rojo);
                agregarTarjetaCombate(x, y, azul, rojo, ganador);

                // Guardar coordenadas de salida para dibujar / posicionar las rondas siguientes
                centrosRondaActual.add(new Point(x + ANCHO_TARJETA, y + ALTO_TARJETA / 2));
                ganadoresRondaActual.add(ganador);
            }

            // ==========================================
            // RONDAS SIGUIENTES: Estructuradas sobre los ganadores previos
            // ==========================================
            int xRonda = x;
            int ySepRonda = ySeparacion;
            List<Point> centrosAnterior = centrosRondaActual;
            List<AtletaDTO> ganadoresAnterior = ganadoresRondaActual;

            for (int ronda = 2; ronda <= numRondas; ronda++) {
                xRonda = xRonda + ANCHO_TARJETA + 70; // Desplazamiento horizontal para la nueva ronda
                ySepRonda = ySepRonda * 2;            // Duplicar el margen vertical debido al filtro del embudo
                
                List<Point> centrosNuevos = new ArrayList<>();
                List<AtletaDTO> ganadoresNuevos = new ArrayList<>();

                int cantidadPartidos = centrosAnterior.size() / 2;
                for (int i = 0; i < cantidadPartidos; i++) {
                    Point pA = centrosAnterior.get(i * 2);
                    Point pB = centrosAnterior.get(i * 2 + 1);
                    
                    // Hallar matemáticamente el punto medio entre los dos combates predecesores
                    int yMedio = (pA.y + pB.y) / 2 - ALTO_TARJETA / 2;

                    AtletaDTO candidatoA = ganadoresAnterior.get(i * 2);
                    AtletaDTO candidatoB = ganadoresAnterior.get(i * 2 + 1);

                    AtletaDTO ganador;
                    if (candidatoA != null && candidatoB != null) {
                        // Ambos atletas están definidos y calificados: Combate real listo para disputarse
                        ganador = determinarGanador(candidatoA, candidatoB);
                        agregarTarjetaCombate(xRonda, yMedio, candidatoA, candidatoB, ganador);
                    } else {
                        // Al menos uno de los rivales sigue en disputa en rondas anteriores
                        String titulo = (ronda == numRondas) ? "FINAL — Por definir" : "Por definir";
                        agregarTarjetaPlaceholder(xRonda, yMedio, titulo);
                        ganador = null;
                    }

                    centrosNuevos.add(new Point(xRonda + ANCHO_TARJETA, yMedio + ALTO_TARJETA / 2));
                    ganadoresNuevos.add(ganador);
                }
                centrosAnterior = centrosNuevos;
                ganadoresAnterior = ganadoresNuevos;
            }

            // ==========================================
            // PANTALLA FINAL: Coronación del Campeón
            // ==========================================
            if (!ganadoresAnterior.isEmpty() && ganadoresAnterior.get(0) != null) {
                mostrarCampeon(xRonda + ANCHO_TARJETA + 40,
                        centrosAnterior.get(0).y - ALTO_CAMPEON / 2, ganadoresAnterior.get(0));
            }

            revalidate(); // Revalidar la jerarquía de componentes para forzar el dibujo
        }

        /**
         * Resuelve quién califica a la siguiente fase. 
         * Si es un emparejamiento con un espacio libre (BYE), el atleta real avanza de forma inmediata.
         * Si es un combate ordinario, consulta en la base de datos si ya se registró un ganador.
         * * @param azul Competidor de esquina Azul.
         * @param rojo Competidor de esquina Roja.
         * @return AtletaDTO del atleta victorioso o calificado; null si aún no se disputa.
         */
        private AtletaDTO determinarGanador(AtletaDTO azul, AtletaDTO rojo) {
            if (azul == null && rojo == null) return null;
            if (azul == null) return rojo;   // Pase directo (BYE) para el competidor Rojo
            if (rojo == null) return azul;   // Pase directo (BYE) para el competidor Azul

            // Buscar u originar el ID único del combate de estos dos oponentes
            Integer idCombate = app.obtenerOCrearCombate(azul.id, rojo.id);
            if (idCombate == null || app.getFacade() == null) return null;

            // Invocar la lógica de persistencia de la fachada para comprobar si hay un ganador definido
            Integer idGanador = app.getFacade().cargarGanador(idCombate);
            if (idGanador == null) return null;
            if (idGanador.equals(azul.id)) return azul;
            if (idGanador.equals(rojo.id)) return rojo;
            
            return null;
        }

        /**
         * Agrega físicamente un widget JPanel que ilustra a los dos oponentes de una llave.
         * Incluye un botón para abrir el marcador del Ring Digital (si el combate está pendiente)
         * o para ver el resultado definitivo (si ya concluyó).
         */
        private void agregarTarjetaCombate(int x, int y, AtletaDTO azul, AtletaDTO rojo, AtletaDTO ganador) {
            if (azul == null && rojo == null) return;
            
            JPanel tarjeta = new JPanel(new BorderLayout(8, 0));
            tarjeta.setBounds(x, y, ANCHO_TARJETA, ALTO_TARJETA);
            tarjeta.setBackground(ganador != null ? new Color(235, 250, 240) : Color.WHITE);
            
            // Estilo visual de la tarjeta: Borde verde grueso para combates con ganador resuelto
            tarjeta.setBorder(BorderFactory.createLineBorder(
                    ganador != null ? new Color(25, 135, 84) : new Color(200, 205, 210), ganador != null ? 2 : 1));

            // Configurar textos de visualización manejando pases de cortesía (BYE)
            String nombreAzul = azul != null ? azul.nombre : "(BYE)";
            String nombreRojo = rojo != null ? rojo.nombre : "(BYE)";
            String marcaAzul = (ganador != null && azul != null && ganador.id == azul.id) ? " 🏆" : "";
            String marcaRojo = (ganador != null && rojo != null && ganador.id == rojo.id) ? " 🏆" : "";

            JLabel lbl = new JLabel("<html><b style='color:#0d6efd;'>" + nombreAzul + marcaAzul
                    + "</b><br><b style='color:#dc3545;'>" + nombreRojo + marcaRojo + "</b></html>");
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
            tarjeta.add(lbl, BorderLayout.CENTER);

            // Agregar el botón de interacción únicamente si es un combate elegible (sin BYEs)
            if (azul != null && rojo != null) {
                JButton btn = new JButton(ganador != null ? "Ver" : "▶");
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                // Acción: Carga la mesa técnica en la VentanaPrincipal enfocada en este combate
                btn.addActionListener(e -> app.abrirCombate(azul, rojo));
                tarjeta.add(btn, BorderLayout.EAST);
            }

            add(tarjeta);
        }

        /**
         * Agrega un panel contenedor neutral con fines visuales (tarjeta vacía o pendiente).
         * Utilizado para modelar llaves que dependen de resultados de rondas de clasificación.
         */
        private void agregarTarjetaPlaceholder(int x, int y, String texto) {
            JPanel tarjeta = new JPanel(new GridBagLayout());
            tarjeta.setBounds(x, y, ANCHO_TARJETA, ALTO_TARJETA);
            tarjeta.setBackground(new Color(245, 246, 248));
            tarjeta.setBorder(BorderFactory.createLineBorder(new Color(210, 214, 218), 1));
            
            JLabel lbl = new JLabel(texto);
            lbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lbl.setForeground(Color.GRAY);
            
            tarjeta.add(lbl);
            add(tarjeta);
        }

        /**
         * Renderiza una tarjeta especial y altamente destacada en el lienzo para mostrar de manera
         * imponente al campeón definitivo del torneo una vez finalizados todos los combates.
         */
        private void mostrarCampeon(int x, int y, AtletaDTO campeon) {
            JPanel tarjeta = new JPanel(new GridBagLayout());
            tarjeta.setBounds(x, y, ANCHO_CAMPEON, ALTO_CAMPEON);
            tarjeta.setBackground(new Color(255, 248, 225)); // Fondo dorado cálido
            tarjeta.setBorder(BorderFactory.createLineBorder(new Color(255, 193, 7), 2)); // Contorno dorado
            
            // Label HTML con diseño fluido (sin anchos estáticos rígidos) para evitar desbordamiento de texto
            JLabel lbl = new JLabel("<html><div style='text-align: center;'>"
                    + "<span style='font-size: 16px;'>🏆</span><br>"
                    + "<span style='color: #8a6d00; font-size: 11px; font-weight: bold;'>CAMPEÓN</span><br>"
                    + "<span style='font-size: 13px; color: #333333; font-weight: bold;'>" + campeon.nombre + "</span>"
                    + "</div></html>");
            
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            
            tarjeta.add(lbl);
            add(tarjeta);
        }
    }
}