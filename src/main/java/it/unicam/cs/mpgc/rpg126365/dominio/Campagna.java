package it.unicam.cs.mpgc.rpg126365.dominio;

import java.util.List;
import java.util.Objects;

public record Campagna(String id, String nome, List<Stanza> stanze) {
    public Campagna {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(nome, "nome");
        stanze = List.copyOf(stanze);
        if (id.isBlank() || nome.isBlank() || stanze.isEmpty()) {
            throw new IllegalArgumentException("Una campagna deve avere un identificatore, un nome e almeno una stanza.");
        }
    }
}
