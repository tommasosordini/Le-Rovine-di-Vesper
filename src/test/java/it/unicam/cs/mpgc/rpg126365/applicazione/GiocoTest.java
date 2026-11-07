package it.unicam.cs.mpgc.rpg126365.applicazione;

import it.unicam.cs.mpgc.rpg126365.dominio.Archetipo;
import it.unicam.cs.mpgc.rpg126365.dominio.Azione;
import it.unicam.cs.mpgc.rpg126365.dominio.CatalogoCampagne;
import it.unicam.cs.mpgc.rpg126365.dominio.Partita;
import it.unicam.cs.mpgc.rpg126365.persistenza.ArchivioPartiteSuFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GiocoTest {
    @TempDir
    Path cartella;

    @Test
    void riprendeLaCampagnaSalvata() throws IOException {
        Path file = cartella.resolve("partita.properties");
        Gioco gioco = creaGioco(file);
        Partita partita = gioco.inizia("faro", "Éowyn", Archetipo.GUERRIERO);
        Partita primoTurno = gioco.gioca(partita, Azione.USA_ABILITA).partita();
        Partita salvata = gioco.gioca(primoTurno, Azione.ATTACCA).partita();
        gioco.salva(salvata);

        Gioco riaperto = creaGioco(file);
        Partita ripresa = riaperto.riprendi().orElseThrow();
        assertEquals(salvata, ripresa);
        assertEquals("Scala del faro", riaperto.stanza(riaperto.avanza(ripresa)).nome());
    }

    @Test
    void iSalvataggiDellaPrimaVersioneRestanoLeggibili() throws IOException {
        Path file = cartella.resolve("partita.properties");
        Files.writeString(file, """
                versione=1
                nome=Ada
                archetipo=MAGO
                salute=30
                pozioni=3
                stanza=0
                saluteNemico=0
                abilitaDisponibile=false
                """);
        Gioco gioco = creaGioco(file);
        Partita ripresa = gioco.riprendi().orElseThrow();

        assertEquals("vesper", ripresa.idCampagna());
        assertEquals("Cripta sommersa", gioco.stanza(gioco.avanza(ripresa)).nome());
    }

    @Test
    void rifiutaUnaCampagnaCheNonEsiste() throws IOException {
        Path file = cartella.resolve("partita.properties");
        Files.writeString(file, """
                versione=2
                campagna=inesistente
                nome=Ada
                archetipo=MAGO
                salute=30
                pozioni=3
                stanza=0
                saluteNemico=0
                abilitaDisponibile=false
                """);

        assertThrows(IOException.class, () -> creaGioco(file).riprendi());
    }

    @Test
    void ilSalvataggioDuranteUnoScontroVieneRifiutato() {
        Gioco gioco = creaGioco(cartella.resolve("partita.properties"));
        Partita partita = gioco.inizia("vesper", "Ada", Archetipo.MAGO);

        assertThrows(IllegalStateException.class, () -> gioco.salva(partita));
    }

    private static Gioco creaGioco(Path file) {
        return new Gioco(CatalogoCampagne.campagne(), new ArchivioPartiteSuFile(file));
    }
}
