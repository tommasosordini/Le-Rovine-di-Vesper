package it.unicam.cs.mpgc.rpg126365;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

final class VedutaCampagna extends JPanel {
    private static final long serialVersionUID = 1L;

    private final Map<String, BufferedImage> illustrazioni = new HashMap<>();
    private final boolean orizzontale;
    private BufferedImage illustrazione;

    VedutaCampagna(boolean orizzontale) {
        super(new BorderLayout());
        this.orizzontale = orizzontale;
        setBackground(Stile.SUPERFICIE);
    }

    void mostra(String id) {
        illustrazione = illustrazioni.computeIfAbsent(id, VedutaCampagna::carica);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics grafica) {
        super.paintComponent(grafica);
        Graphics2D g = (Graphics2D) grafica.create();
        try {
            Stile.smussa(g);
            int w = getWidth();
            int h = getHeight();
            if (illustrazione != null) {
                double scala = Math.max((double) w / illustrazione.getWidth(),
                        (double) h / illustrazione.getHeight());
                int larghezza = (int) Math.ceil(illustrazione.getWidth() * scala);
                int altezza = (int) Math.ceil(illustrazione.getHeight() * scala);
                int y = (int) ((h - altezza) * (orizzontale ? 0.25 : 0.5));
                g.drawImage(illustrazione, (w - larghezza) / 2, y, larghezza, altezza, this);
            }
            Color ombra = new Color(8, 14, 21, 244);
            Color trasparente = new Color(8, 14, 21, 0);
            g.setPaint(orizzontale
                    ? new GradientPaint(0, 0, ombra, Math.max(1, w * 0.82f), 0, trasparente)
                    : new GradientPaint(0, h, ombra, 0, h * 0.40f - 1, trasparente));
            g.fillRect(0, 0, w, h);
        } finally {
            g.dispose();
        }
    }

    private static BufferedImage carica(String id) {
        try (var risorsa = VedutaCampagna.class.getResourceAsStream("/illustrazioni/" + id + ".jpg")) {
            return risorsa == null ? null : ImageIO.read(risorsa);
        } catch (IOException errore) {
            return null;
        }
    }
}
