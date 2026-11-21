package it.unicam.cs.mpgc.rpg126365;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegoleGiocoTest {
    private final RegoleGioco regole = new RegoleGioco(CatalogoCampagne.campagne().get(0));

    @Test
    void ilColpoFinaleNonFaScattareIlContrattacco() {
        Partita partita = regole.inizia("Ada", Archetipo.MAGO);
        EsitoTurno primo = regole.giocaTurno(partita, Azione.USA_ABILITA);
        EsitoTurno finale = regole.giocaTurno(primo.partita(), Azione.ATTACCA);

        assertEquals(16, primo.danniInflitti());
        assertEquals(4, primo.danniRicevuti());
        assertEquals(2, finale.danniInflitti());
        assertEquals(0, finale.danniRicevuti());
        assertEquals(primo.partita().eroe().salute(), finale.partita().eroe().salute());
        assertEquals(Fase.TRA_STANZE, regole.fase(finale.partita()));
    }

    @Test
    void abilitaUnaVoltaPerStanza() {
        Partita partita = regole.inizia("Ada", Archetipo.GUERRIERO);
        EsitoTurno colpo = regole.giocaTurno(partita, Azione.USA_ABILITA);

        assertEquals(0, colpo.danniRicevuti());
        assertFalse(colpo.partita().abilitaDisponibile());
        assertThrows(IllegalStateException.class, () -> regole.giocaTurno(colpo.partita(), Azione.USA_ABILITA));

        Partita liberata = regole.giocaTurno(colpo.partita(), Azione.ATTACCA).partita();
        assertTrue(regole.avanza(liberata).abilitaDisponibile());
    }

    @Test
    void laPozioneDistingueCuraEffettivaEDannoDelContrattacco() {
        Partita partita = regole.inizia("Ada", Archetipo.MAGO);
        Partita ferita = regole.giocaTurno(partita, Azione.ATTACCA).partita();
        EsitoTurno cura = regole.giocaTurno(ferita, Azione.BEVI_POZIONE);

        assertEquals(4, cura.saluteRecuperata());
        assertEquals(4, cura.danniRicevuti());
        assertEquals(2, cura.partita().eroe().pozioni());
        assertEquals(ferita.eroe().salute(), cura.partita().eroe().salute());
    }
}
