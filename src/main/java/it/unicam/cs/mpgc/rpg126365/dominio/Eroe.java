package it.unicam.cs.mpgc.rpg126365.dominio;

import java.util.Objects;

public record Eroe(String nome, Archetipo archetipo, int salute, int pozioni) {
    public Eroe {
        nome = Objects.requireNonNull(nome, "nome").strip();
        Objects.requireNonNull(archetipo, "archetipo");
        if (nome.isEmpty() || nome.length() > 30 || nome.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Il nome deve contenere da 1 a 30 caratteri e stare su una riga.");
        }
        if (salute < 0 || salute > archetipo.saluteMassima() || pozioni < 0) {
            throw new IllegalArgumentException("Salute o numero di pozioni non validi.");
        }
    }
}
