package it.unicam.cs.mpgc.rpg126365.dominio;

import java.util.List;

public final class CatalogoCampagne {
    private CatalogoCampagne() {
    }

    public static List<Campagna> campagne() {
        return List.of(
                new Campagna("vesper", "Le Rovine di Vesper", List.of(
                        new Stanza("Ingresso delle rovine", "Sentinella", 18, 4),
                        new Stanza("Cripta sommersa", "Spettro", 27, 5),
                        new Stanza("Sala del sigillo", "Guardiano di Vesper", 38, 7))),
                new Campagna("faro", "Il Faro delle Maree", List.of(
                        new Stanza("Molo allagato", "Granchio corazzato", 20, 3),
                        new Stanza("Scala del faro", "Custode annegato", 30, 5),
                        new Stanza("Lanterna in tempesta", "Spirito della marea", 34, 8)))
        );
    }
}
