package it.unicam.cs.mpgc.rpg126365.dominio;

import java.util.Objects;

public record Partita(Eroe eroe, int indiceStanza, int saluteNemico, boolean abilitaDisponibile) {
    public Partita {
        Objects.requireNonNull(eroe, "eroe");
        if (indiceStanza < 0 || saluteNemico < 0 || (eroe.salute() == 0 && saluteNemico == 0)) {
            throw new IllegalArgumentException("Lo stato della partita non è valido.");
        }
    }
}
