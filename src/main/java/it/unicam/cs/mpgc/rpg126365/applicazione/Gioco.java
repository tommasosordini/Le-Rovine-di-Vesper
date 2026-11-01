package it.unicam.cs.mpgc.rpg126365.applicazione;

import it.unicam.cs.mpgc.rpg126365.dominio.Archetipo;
import it.unicam.cs.mpgc.rpg126365.dominio.Azione;
import it.unicam.cs.mpgc.rpg126365.dominio.Campagna;
import it.unicam.cs.mpgc.rpg126365.dominio.EsitoTurno;
import it.unicam.cs.mpgc.rpg126365.dominio.Fase;
import it.unicam.cs.mpgc.rpg126365.dominio.Partita;
import it.unicam.cs.mpgc.rpg126365.dominio.RegoleGioco;
import it.unicam.cs.mpgc.rpg126365.dominio.Stanza;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Gioco {
    private final List<Campagna> campagne;
    private final Map<String, RegoleGioco> regole;
    private final ArchivioPartite archivio;

    public Gioco(List<Campagna> campagne, ArchivioPartite archivio) {
        this.campagne = List.copyOf(campagne);
        if (this.campagne.isEmpty()) {
            throw new IllegalArgumentException("Il catalogo deve contenere almeno una campagna.");
        }
        this.regole = this.campagne.stream()
                .collect(Collectors.toUnmodifiableMap(Campagna::id, RegoleGioco::new));
        this.archivio = Objects.requireNonNull(archivio);
    }

    public List<Campagna> campagne() {
        return campagne;
    }

    public Partita inizia(String idCampagna, String nome, Archetipo archetipo) {
        return regole(idCampagna).inizia(nome, archetipo);
    }

    public EsitoTurno gioca(Partita partita, Azione azione) {
        return regole(partita.idCampagna()).giocaTurno(partita, azione);
    }

    public Partita avanza(Partita partita) {
        return regole(partita.idCampagna()).avanza(partita);
    }

    public Fase fase(Partita partita) {
        return regole(partita.idCampagna()).fase(partita);
    }

    public Stanza stanza(Partita partita) {
        return regole(partita.idCampagna()).stanza(partita);
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

    private RegoleGioco regole(String idCampagna) {
        RegoleGioco motore = regole.get(idCampagna);
        if (motore == null) {
            throw new IllegalArgumentException("Campagna sconosciuta: " + idCampagna);
        }
        return motore;
    }
}
