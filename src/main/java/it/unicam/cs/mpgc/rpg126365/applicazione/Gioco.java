package it.unicam.cs.mpgc.rpg126365.applicazione;

import it.unicam.cs.mpgc.rpg126365.dominio.Archetipo;
import it.unicam.cs.mpgc.rpg126365.dominio.Azione;
import it.unicam.cs.mpgc.rpg126365.dominio.Fase;
import it.unicam.cs.mpgc.rpg126365.dominio.Partita;
import it.unicam.cs.mpgc.rpg126365.dominio.RegoleGioco;
import it.unicam.cs.mpgc.rpg126365.dominio.Stanza;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public final class Gioco {
    private final RegoleGioco regole;
    private final ArchivioPartite archivio;

    public Gioco(RegoleGioco regole, ArchivioPartite archivio) {
        this.regole = Objects.requireNonNull(regole);
        this.archivio = Objects.requireNonNull(archivio);
    }

    public Partita inizia(String nome, Archetipo archetipo) {
        return regole.inizia(nome, archetipo);
    }

    public Partita gioca(Partita partita, Azione azione) {
        return regole.giocaTurno(partita, azione);
    }

    public Partita avanza(Partita partita) {
        return regole.avanza(partita);
    }

    public Fase fase(Partita partita) {
        return regole.fase(partita);
    }

    public Stanza stanza(Partita partita) {
        return regole.stanza(partita);
    }

    public void salva(Partita partita) throws IOException {
        if (fase(partita) != Fase.TRA_STANZE) {
            throw new IllegalStateException("Puoi salvare solo tra due stanze.");
        }
        archivio.salva(partita);
    }

    public Optional<Partita> riprendi() throws IOException {
        Optional<Partita> salvata = archivio.carica();
        if (salvata.isPresent()) {
            try {
                if (fase(salvata.get()) != Fase.TRA_STANZE) {
                    throw new IllegalArgumentException("La partita salvata deve trovarsi tra due stanze.");
                }
            } catch (IllegalArgumentException errore) {
                throw new IOException("Lo stato salvato non è valido per questa campagna.", errore);
            }
        }
        return salvata;
    }
}
