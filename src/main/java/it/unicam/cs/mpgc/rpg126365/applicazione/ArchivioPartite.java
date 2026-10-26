package it.unicam.cs.mpgc.rpg126365.applicazione;

import it.unicam.cs.mpgc.rpg126365.dominio.Partita;

import java.io.IOException;
import java.util.Optional;

public interface ArchivioPartite {
    void salva(Partita partita) throws IOException;

    Optional<Partita> carica() throws IOException;
}
