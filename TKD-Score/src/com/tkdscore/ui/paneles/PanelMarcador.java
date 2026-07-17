package com.tkdscore.ui.paneles;

import com.tkdscore.observer.IObservador;
import com.tkdscore.ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Panel del marcador en vivo (Ring Digital): puntos azul/rojo, tecnicas, estado del combate e IVR. */
public class PanelMarcador extends JPanel implements IObservador {

    private final VentanaPrincipal app;

    private JLabel lblCombateInfo;
    private TarjetaPuntaje tarjetaAzul;
    private TarjetaPuntaje tarjetaRojo;
    private JLabel lblEstadoCombate;
    private JComboBox<String> comboTecnica;
    private JCheckBox chkBonoGiro;
    private JCheckBox chkBonoCabeza;
    private JButton btnAnotarAzul;
    private JButton btnAnotarRojo;
    private JButton btnSiguienteEstado;
    private JButton btnRevisionIVR;

    public PanelMarcador(VentanaPrincipal app) {
        super(new BorderLayout(15, 15));
        this.app = app;
        setBackground(Color.WHITE);
        construirUI();
        app.getMarcador().agregarObservador(this);
    }

    /** Callback del patron Observer: se llama automaticamente cuando cambia el puntaje. */
    @Override
    public void actualizar(int puntosAzul, int puntosRojo) {
        if (tarjetaAzul != null) tarjetaAzul.setPuntos(puntosAzul);
        if (tarjetaRojo != null) tarjetaRojo.setPuntos(puntosRojo);
    }

    private void construirUI() {
        setBorder(new EmptyBorder(15, 20, 15, 20));

        // ---- Bloque superior: banner de info + tarjetas de puntaje ----
        JPanel bloqueSuperior = new JPanel(new BorderLayout(0, 12));
        bloqueSuperior.setOpaque(false);

        lblCombateInfo = new JLabel("RING DIGITAL — Ningún combate seleccionado", SwingConstants.CENTER);
        lblCombateInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCombateInfo.setForeground(Color.WHITE);
        lblCombateInfo.setOpaque(true);
        lblCombateInfo.setBackground(VentanaPrincipal.COLOR_MENU_LATERAL);
        lblCombateInfo.setBorder(new EmptyBorder(12, 10, 12, 10));
        bloqueSuperior.add(lblCombateInfo, BorderLayout.NORTH);

        JPanel panelMarcador = new JPanel(new GridLayout(1, 2, 10, 0));
        panelMarcador.setOpaque(false);
        panelMarcador.setPreferredSize(new Dimension(100, 210));
        tarjetaAzul = new TarjetaPuntaje("AZUL · CHUNG", VentanaPrincipal.COLOR_AZUL_TKD);
        tarjetaRojo = new TarjetaPuntaje("ROJO · HONG", VentanaPrincipal.COLOR_ROJO_TKD);
        panelMarcador.add(tarjetaAzul);
        panelMarcador.add(tarjetaRojo);
        bloqueSuperior.add(panelMarcador, BorderLayout.CENTER);

        add(bloqueSuperior, BorderLayout.NORTH);

        // ---- Panel de controles (tecnica y botones de anotar) ----
        JPanel panelControles = new JPanel(new GridBagLayout());
        panelControles.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 232)), "Panel Técnico de Anotación",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboTecnica = new JComboBox<>(new String[]{"PUNETAZO", "PATADA_CUERPO", "PATADA_CABEZA", "PATADA_GIRATORIA"});
        comboTecnica.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkBonoGiro = new JCheckBox("Bonificación Técnica por Giro (+1 pt)");
        chkBonoCabeza = new JCheckBox("Bonificación Extra impacto Cabeza (+2 pts)");
        chkBonoGiro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkBonoCabeza.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gbc.gridx = 0; gbc.gridy = 0; panelControles.add(new JLabel("Técnica Realizada:"), gbc);
        gbc.gridx = 1; panelControles.add(comboTecnica, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; panelControles.add(chkBonoGiro, gbc);
        gbc.gridy = 2; panelControles.add(chkBonoCabeza, gbc);

        JPanel panelBotonesAnotar = new JPanel(new GridLayout(1, 2, 15, 0));
        panelBotonesAnotar.setOpaque(false);
        btnAnotarAzul = crearBotonAnotar("Punto CHUNG (Azul)", VentanaPrincipal.COLOR_AZUL_TKD);
        btnAnotarRojo = crearBotonAnotar("Punto HONG (Rojo)", VentanaPrincipal.COLOR_ROJO_TKD);

        btnAnotarAzul.addActionListener(e -> anotar("AZUL"));
        btnAnotarRojo.addActionListener(e -> anotar("ROJO"));

        panelBotonesAnotar.add(btnAnotarAzul);
        panelBotonesAnotar.add(btnAnotarRojo);

        gbc.gridy = 3; gbc.insets = new Insets(18, 12, 8, 12);
        panelControles.add(panelBotonesAnotar, gbc);

        add(panelControles, BorderLayout.CENTER);

        // ---- Bloque inferior: estado del combate + botones auxiliares ----
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
        panelInferior.setOpaque(false);
        lblEstadoCombate = new JLabel("Estado: " + app.getCombate().getEstadoNombre(), SwingConstants.CENTER);
        lblEstadoCombate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelInferior.add(lblEstadoCombate, BorderLayout.NORTH);

        JPanel panelBotonesAbajo = new JPanel(new GridLayout(1, 2, 15, 0));
        panelBotonesAbajo.setOpaque(false);
        btnSiguienteEstado = new JButton("Siguiente Estado / Descanso ⏱");
        btnRevisionIVR = new JButton("Solicitar Revisión de Video (IVR) ↩");
        btnSiguienteEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRevisionIVR.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSiguienteEstado.setFocusPainted(false);
        btnRevisionIVR.setFocusPainted(false);

        btnSiguienteEstado.addActionListener(e -> {
            app.getCombate().siguientePaso();
            lblEstadoCombate.setText("Estado: " + app.getCombate().getEstadoNombre()
                    + " (Ronda " + app.getCombate().getRondaActual() + ")");
            if (app.getIdCombateActivo() != null) {
                app.getFacade().guardarEstadoCombate(app.getIdCombateActivo(),
                        app.getCombate().getEstadoNombre(), app.getCombate().getRondaActual());

                if ("Finalizado".equals(app.getCombate().getEstadoNombre())) {
                    Integer idGanador = app.getFacade().guardarGanador(app.getIdCombateActivo());
                    if (idGanador != null) {
                        String nombreGanador = idGanador.equals(app.getIdAtletaAzulActivo())
                                ? app.getNombreAzulActivo() : app.getNombreRojoActivo();
                        JOptionPane.showMessageDialog(this,
                                "Combate finalizado.\nGanador: " + nombreGanador
                                        + "\n\nYa puedes verlo avanzar en 'Planificación'.",
                                "Combate finalizado", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Combate finalizado en EMPATE.\nNo se puede registrar un ganador automático: "
                                        + "define el punto de oro / desempate y vuelve a anotar antes de continuar,"
                                        + " o el bracket en 'Planificación' quedará pendiente en esta llave.",
                                "Empate sin resolver", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            actualizarDisponibilidadBotones();
        });

        btnRevisionIVR.addActionListener(e -> {
            boolean seRevirtio = app.getFacade().revertirUltimoPunto();
            if (seRevirtio) {
                JOptionPane.showMessageDialog(this, "El punto fue anulado por revisión de video.",
                        "Revisión IVR", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No hay ningún punto reciente para revisar.",
                        "Revisión IVR", JOptionPane.WARNING_MESSAGE);
            }
        });

        panelBotonesAbajo.add(btnSiguienteEstado);
        panelBotonesAbajo.add(btnRevisionIVR);
        panelInferior.add(panelBotonesAbajo, BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);

        refrescarEncabezado();
    }

    private JButton crearBotonAnotar(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(14, 10, 14, 10));
        return btn;
    }

    private void anotar(String colorAtleta) {
        if (app.getIdCombateActivo() == null) return;

        if (!app.getCombate().puedeAnotarPuntos()) {
            JOptionPane.showMessageDialog(this,
                    "No se pueden anotar puntos en este momento (" + app.getCombate().getEstadoNombre() + ").",
                    "Combate no activo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String tipoTecnica = (String) comboTecnica.getSelectedItem();
            boolean conBonoGiro = chkBonoGiro.isSelected();
            boolean conBonoCabeza = chkBonoCabeza.isSelected();

            app.getFacade().anotarPunto(app.getIdCombateActivo(), app.getIdArbitroSesion(),
                    colorAtleta, tipoTecnica, conBonoGiro, conBonoCabeza);

            chkBonoGiro.setSelected(false);
            chkBonoCabeza.setSelected(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al procesar punto: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Llamado por VentanaPrincipal cada vez que se abre un combate o se entra a este modulo. */
    public void refrescarEncabezado() {
        boolean hayCombate = app.getIdCombateActivo() != null;
        lblCombateInfo.setText(hayCombate
                ? "RING DIGITAL #" + app.getIdCombateActivo() + "  ──  "
                        + app.getNombreAzulActivo() + "  vs  " + app.getNombreRojoActivo()
                : "RING DIGITAL — Ningún combate seleccionado. Ve a 'Planificación' para iniciar.");
        btnRevisionIVR.setEnabled(hayCombate);
        lblEstadoCombate.setText("Estado: " + app.getCombate().getEstadoNombre()
                + " (Ronda " + app.getCombate().getRondaActual() + ")");
        actualizarDisponibilidadBotones();
    }

    /** Deshabilita anotar puntos y avanzar de estado una vez que el combate ya esta Finalizado. */
    private void actualizarDisponibilidadBotones() {
        boolean hayCombate = app.getIdCombateActivo() != null;
        boolean finalizado = "Finalizado".equals(app.getCombate().getEstadoNombre());
        btnAnotarAzul.setEnabled(hayCombate && !finalizado);
        btnAnotarRojo.setEnabled(hayCombate && !finalizado);
        btnSiguienteEstado.setEnabled(hayCombate && !finalizado);
        if (hayCombate) {
            lblEstadoCombate.setText("Estado: " + app.getCombate().getEstadoNombre()
                    + " (Ronda " + app.getCombate().getRondaActual() + ")"
                    + (finalizado ? "  🏁" : ""));
        }
    }

    /** Tarjeta de puntaje estilo "marcador de ring": rectangulo redondeado de color solido con el numero grande. */
    private static class TarjetaPuntaje extends JPanel {
        private final Color color;
        private final String titulo;
        private int puntos = 0;

        TarjetaPuntaje(String titulo, Color color) {
            this.titulo = titulo;
            this.color = color;
            setOpaque(false);
        }

        void setPuntos(int puntos) {
            this.puntos = puntos;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Double(4, 4, w - 8, h - 8, 22, 22));

            g2.setColor(new Color(255, 255, 255, 235));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            FontMetrics fmTitulo = g2.getFontMetrics();
            int tituloX = (w - fmTitulo.stringWidth(titulo)) / 2;
            g2.drawString(titulo, tituloX, 34);

            String texto = String.format("%02d", puntos);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Impact", Font.PLAIN, Math.min(95, h / 2)));
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(texto)) / 2;
            int y = (h + fm.getAscent()) / 2 + 8;
            g2.drawString(texto, x, y);

            g2.dispose();
        }
    }
}