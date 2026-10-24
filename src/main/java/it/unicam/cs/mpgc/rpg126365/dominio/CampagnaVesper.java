package it.unicam.cs.mpgc.rpg126365.dominio;

import java.util.List;

public final class CampagnaVesper {
    private CampagnaVesper() {
    }

    public static RegoleGioco crea() {
        return new RegoleGioco(List.of(
                new Stanza("Ingresso delle rovine", "Sentinella", 18, 4),
                new Stanza("Cripta sommersa", "Spettro", 27, 5),
                new Stanza("Sala del sigillo", "Guardiano di Vesper", 38, 7)
        ));
    }
}
