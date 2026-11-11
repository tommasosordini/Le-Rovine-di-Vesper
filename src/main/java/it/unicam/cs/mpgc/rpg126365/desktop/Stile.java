package it.unicam.cs.mpgc.rpg126365.desktop;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;

final class Stile {
    static final Color FONDO = new Color(16, 22, 29);
    static final Color SUPERFICIE = new Color(24, 33, 42);
    static final Color BORDO = new Color(54, 69, 78);
    static final Color TESTO = new Color(244, 238, 225);
    static final Color TENUE = new Color(174, 188, 192);
    static final Color ORO = new Color(238, 196, 114);
    static final Color VERDE = new Color(126, 218, 185);
    static final Color CORALLO = new Color(245, 153, 143);

    private Stile() {
    }

    static Font testo(int stile, float grandezza) {
        return new Font(Font.SANS_SERIF, stile, Math.round(grandezza));
    }

    static Font titolo(int grandezza) {
        return new Font("Georgia", Font.BOLD, grandezza);
    }

    static JLabel etichetta(String testo, Color colore, int stile, int grandezza) {
        JLabel etichetta = new JLabel(testo);
        etichetta.setForeground(colore);
        etichetta.setFont(testo(stile, grandezza));
        return etichetta;
    }

    static JPanel pannello(LayoutManager layout, Color sfondo) {
        JPanel pannello = new JPanel(layout);
        pannello.setBackground(sfondo);
        return pannello;
    }

    static Border cornice(int margine) {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDO),
                BorderFactory.createEmptyBorder(margine, margine, margine, margine));
    }

    static JButton pulsante(String testo, boolean principale, Runnable azione) {
        JButton pulsante = new JButton(testo);
        prepara(pulsante, principale);
        pulsante.addActionListener(evento -> azione.run());
        return pulsante;
    }

    static JToggleButton scelta(String testo) {
        JToggleButton pulsante = new JToggleButton(testo);
        prepara(pulsante, false);
        return pulsante;
    }

    private static void prepara(AbstractButton pulsante, boolean principale) {
        pulsante.setUI(new PellePulsante(principale));
        pulsante.setForeground(principale ? FONDO : TESTO);
        pulsante.setFont(testo(Font.BOLD, 14));
        pulsante.setOpaque(false);
        pulsante.setContentAreaFilled(false);
        pulsante.setRolloverEnabled(true);
        pulsante.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pulsante.setBorder(BorderFactory.createEmptyBorder(11, 13, 11, 13));
    }

    static JProgressBar barra(Color colore, String nome) {
        JProgressBar barra = new JProgressBar();
        barra.setPreferredSize(new Dimension(100, 9));
        barra.setBorderPainted(false);
        barra.setOpaque(false);
        barra.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics grafica, JComponent componente) {
                Graphics2D g = (Graphics2D) grafica.create();
                try {
                    smussa(g);
                    int larghezza = componente.getWidth();
                    int altezza = componente.getHeight();
                    g.setColor(BORDO);
                    g.fillRoundRect(0, 0, larghezza, altezza, altezza, altezza);
                    int pieno = (int) Math.round(larghezza * barra.getPercentComplete());
                    if (pieno > 0) {
                        g.setColor(colore);
                        g.fillRoundRect(0, 0, pieno, altezza, altezza, altezza);
                    }
                } finally {
                    g.dispose();
                }
            }
        });
        barra.getAccessibleContext().setAccessibleName(nome);
        return barra;
    }

    static void smussa(Graphics2D grafica) {
        grafica.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        grafica.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    private static final class PellePulsante extends BasicButtonUI {
        private final boolean principale;

        private PellePulsante(boolean principale) {
            this.principale = principale;
        }

        @Override
        protected void paintText(Graphics grafica, JComponent componente, Rectangle area, String testo) {
            AbstractButton pulsante = (AbstractButton) componente;
            Graphics2D g = (Graphics2D) grafica.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(pulsante.isEnabled() ? pulsante.getForeground() : TENUE.darker());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, testo, pulsante.getDisplayedMnemonicIndex(),
                        area.x, area.y + g.getFontMetrics().getAscent());
            } finally {
                g.dispose();
            }
        }

        @Override
        public void paint(Graphics grafica, JComponent componente) {
            AbstractButton pulsante = (AbstractButton) componente;
            var stato = pulsante.getModel();
            boolean attivo = pulsante.isEnabled();
            Color fondo = attivo && principale ? ORO : SUPERFICIE;
            Color bordo = attivo && (principale || stato.isSelected() || pulsante.hasFocus()) ? ORO : BORDO;
            if (attivo && stato.isPressed()) {
                fondo = fondo.darker();
            } else if (attivo && (stato.isRollover() || stato.isSelected())) {
                fondo = fondo.brighter();
            }
            Graphics2D g = (Graphics2D) grafica.create();
            try {
                smussa(g);
                int w = componente.getWidth();
                int h = componente.getHeight();
                g.setColor(fondo);
                g.fillRoundRect(0, 0, w, h, 11, 11);
                g.setColor(bordo);
                g.drawRoundRect(0, 0, w - 1, h - 1, 11, 11);
                if (pulsante.hasFocus()) {
                    g.setColor(principale ? FONDO : ORO);
                    g.drawRoundRect(3, 3, w - 7, h - 7, 8, 8);
                }
            } finally {
                g.dispose();
            }
            super.paint(grafica, componente);
        }
    }
}
