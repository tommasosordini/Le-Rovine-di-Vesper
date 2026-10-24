package it.unicam.cs.mpgc.rpg126365.dominio;

public enum Archetipo {
    GUERRIERO(42, 8, 12, 4),
    MAGO(34, 7, 16, 0);

    private final int saluteMassima;
    private final int attacco;
    private final int dannoAbilita;
    private final int difesaAbilita;

    Archetipo(int saluteMassima, int attacco, int dannoAbilita, int difesaAbilita) {
        this.saluteMassima = saluteMassima;
        this.attacco = attacco;
        this.dannoAbilita = dannoAbilita;
        this.difesaAbilita = difesaAbilita;
    }

    public int saluteMassima() {
        return saluteMassima;
    }

    int attacco() {
        return attacco;
    }

    int dannoAbilita() {
        return dannoAbilita;
    }

    int difesaAbilita() {
        return difesaAbilita;
    }
}
