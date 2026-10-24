package it.unicam.cs.mpgc.rpg126365.dominio;

import java.util.List;
import java.util.Objects;

public final class RegoleGioco {
    private static final int POZIONI_INIZIALI = 3;
    private static final int CURA_POZIONE = 14;
    private final List<Stanza> stanze;

    public RegoleGioco(List<Stanza> stanze) {
        this.stanze = List.copyOf(stanze);
        if (this.stanze.isEmpty()) {
            throw new IllegalArgumentException("La campagna deve avere almeno una stanza.");
        }
    }

    public Partita inizia(String nome, Archetipo archetipo) {
        Objects.requireNonNull(archetipo, "archetipo");
        return entra(new Eroe(nome, archetipo, archetipo.saluteMassima(), POZIONI_INIZIALI), 0);
    }

    public Stanza stanza(Partita partita) {
        if (partita.indiceStanza() >= stanze.size()) {
            throw new IllegalArgumentException("La stanza non appartiene alla campagna.");
        }
        Stanza stanza = stanze.get(partita.indiceStanza());
        if (partita.saluteNemico() > stanza.saluteNemico()) {
            throw new IllegalArgumentException("La salute del nemico supera il massimo della stanza.");
        }
        return stanza;
    }

    public Fase fase(Partita partita) {
        stanza(partita);
        if (partita.eroe().salute() == 0) {
            return Fase.SCONFITTA;
        }
        if (partita.saluteNemico() > 0) {
            return Fase.COMBATTIMENTO;
        }
        return partita.indiceStanza() == stanze.size() - 1 ? Fase.VITTORIA : Fase.TRA_STANZE;
    }

    public Partita avanza(Partita partita) {
        if (fase(partita) != Fase.TRA_STANZE) {
            throw new IllegalStateException("Puoi avanzare solo dopo aver liberato una stanza.");
        }
        return entra(partita.eroe(), partita.indiceStanza() + 1);
    }

    public Partita giocaTurno(Partita partita, Azione azione) {
        if (fase(partita) != Fase.COMBATTIMENTO) {
            throw new IllegalStateException("Non c'è uno scontro in corso.");
        }
        Eroe eroe = partita.eroe();
        Archetipo archetipo = eroe.archetipo();
        int salute = eroe.salute();
        int pozioni = eroe.pozioni();
        int saluteNemico = partita.saluteNemico();
        boolean abilitaDisponibile = partita.abilitaDisponibile();
        int difesa = 0;

        switch (azione) {
            case ATTACCA -> saluteNemico = Math.max(0, saluteNemico - archetipo.attacco());
            case USA_ABILITA -> {
                if (!abilitaDisponibile) {
                    throw new IllegalStateException("Hai già usato l'abilità in questa stanza.");
                }
                saluteNemico = Math.max(0, saluteNemico - archetipo.dannoAbilita());
                difesa = archetipo.difesaAbilita();
                abilitaDisponibile = false;
            }
            case BEVI_POZIONE -> {
                if (pozioni == 0 || salute == archetipo.saluteMassima()) {
                    throw new IllegalStateException("Puoi bere una pozione solo se ne hai una e hai perso salute.");
                }
                salute = Math.min(archetipo.saluteMassima(), salute + CURA_POZIONE);
                pozioni--;
            }
        }

        if (saluteNemico > 0) {
            int dannoRicevuto = Math.max(0, stanza(partita).attaccoNemico() - difesa);
            salute = Math.max(0, salute - dannoRicevuto);
        }
        Eroe aggiornato = new Eroe(eroe.nome(), archetipo, salute, pozioni);
        return new Partita(aggiornato, partita.indiceStanza(), saluteNemico, abilitaDisponibile);
    }

    private Partita entra(Eroe eroe, int indiceStanza) {
        return new Partita(eroe, indiceStanza, stanze.get(indiceStanza).saluteNemico(), true);
    }
}
