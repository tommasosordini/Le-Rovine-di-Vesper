package it.unicam.cs.mpgc.rpg126365.dominio;

import java.util.Objects;

public record Stanza(String nome, String nemico, int saluteNemico, int attaccoNemico) {
    public Stanza {
        Objects.requireNonNull(nome, "nome");
        Objects.requireNonNull(nemico, "nemico");
        if (nome.isBlank() || nemico.isBlank() || saluteNemico <= 0 || attaccoNemico <= 0) {
            throw new IllegalArgumentException("I dati della stanza non sono validi.");
        }
    }
}
