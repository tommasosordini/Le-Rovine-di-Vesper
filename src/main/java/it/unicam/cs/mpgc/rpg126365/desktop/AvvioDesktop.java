package it.unicam.cs.mpgc.rpg126365.desktop;

import it.unicam.cs.mpgc.rpg126365.applicazione.Gioco;
import it.unicam.cs.mpgc.rpg126365.dominio.CatalogoCampagne;
import it.unicam.cs.mpgc.rpg126365.persistenza.ArchivioPartiteSuFile;

import javax.swing.SwingUtilities;
import java.nio.file.Path;

public final class AvvioDesktop {
    private AvvioDesktop() {
    }

    public static void main(String[] args) {
        Path file = Path.of(System.getProperty("user.home"), ".rovine-di-vesper", "partita.properties");
        Gioco gioco = new Gioco(CatalogoCampagne.campagne(), new ArchivioPartiteSuFile(file));
        SwingUtilities.invokeLater(() -> new FinestraGioco(gioco).mostra());
    }
}
