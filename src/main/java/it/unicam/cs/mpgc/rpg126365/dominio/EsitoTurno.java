package it.unicam.cs.mpgc.rpg126365.dominio;

public record EsitoTurno(Partita partita, Azione azione, int danniInflitti, int danniRicevuti,
                         int saluteRecuperata) {
}
