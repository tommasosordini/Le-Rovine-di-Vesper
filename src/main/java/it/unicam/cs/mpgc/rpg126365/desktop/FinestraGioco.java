package it.unicam.cs.mpgc.rpg126365.desktop;

import it.unicam.cs.mpgc.rpg126365.applicazione.Gioco;
import it.unicam.cs.mpgc.rpg126365.dominio.Archetipo;
import it.unicam.cs.mpgc.rpg126365.dominio.Azione;
import it.unicam.cs.mpgc.rpg126365.dominio.Campagna;
import it.unicam.cs.mpgc.rpg126365.dominio.Eroe;
import it.unicam.cs.mpgc.rpg126365.dominio.Fase;
import it.unicam.cs.mpgc.rpg126365.dominio.Partita;
import it.unicam.cs.mpgc.rpg126365.dominio.Stanza;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.Optional;

final class FinestraGioco {
    private final Gioco gioco;
    private final JFrame finestra = new JFrame("Le Rovine di Vesper");
    private final CardLayout schede = new CardLayout();
    private final JPanel contenuto = new JPanel(schede);
    private final JTextField nome = new JTextField(18);
    private final JComboBox<Archetipo> archetipo = new JComboBox<>(Archetipo.values());
    private final JComboBox<String> campagna;
    private final JLabel luogo = new JLabel();
    private final JLabel nomeEroe = new JLabel();
    private final JLabel nomeNemico = new JLabel();
    private final JLabel inventario = new JLabel();
    private final JLabel messaggio = new JLabel();
    private final JProgressBar saluteEroe = new JProgressBar();
    private final JProgressBar saluteNemico = new JProgressBar();
    private final JButton attacca = pulsante("Attacca", () -> turno(Azione.ATTACCA));
    private final JButton abilita = pulsante("Abilità", () -> turno(Azione.USA_ABILITA));
    private final JButton pozione = pulsante("Bevi pozione", () -> turno(Azione.BEVI_POZIONE));
    private final JButton avanza = pulsante("Avanza", this::avanza);
    private final JButton salva = pulsante("Salva", this::salva);
    private Partita partita;

    FinestraGioco(Gioco gioco) {
        this.gioco = gioco;
        campagna = new JComboBox<>(gioco.campagne().stream().map(Campagna::nome).toArray(String[]::new));
        nomeEroe.putClientProperty("html.disable", true);
        contenuto.add(menu(), "menu");
        contenuto.add(schermataPartita(), "partita");
        finestra.setContentPane(contenuto);
        finestra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        finestra.pack();
        finestra.setMinimumSize(finestra.getSize());
        finestra.setLocationRelativeTo(null);
    }

    void mostra() {
        finestra.setVisible(true);
    }

    private JPanel menu() {
        JPanel pannello = new JPanel(new GridLayout(0, 1, 0, 8));
        pannello.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pannello.add(new JLabel("Campagna"));
        pannello.add(campagna);
        pannello.add(new JLabel("Nome dell'eroe"));
        pannello.add(nome);
        pannello.add(new JLabel("Classe"));
        pannello.add(archetipo);
        pannello.add(pulsante("Inizia partita", this::inizia));
        pannello.add(pulsante("Riprendi partita salvata", this::riprendi));
        return pannello;
    }

    private JPanel schermataPartita() {
        JPanel pannello = new JPanel(new BorderLayout(0, 16));
        pannello.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel stato = new JPanel(new GridLayout(0, 1, 0, 6));
        stato.add(luogo);
        stato.add(nomeEroe);
        stato.add(saluteEroe);
        stato.add(nomeNemico);
        stato.add(saluteNemico);
        stato.add(inventario);
        stato.add(messaggio);
        pannello.add(stato, BorderLayout.CENTER);

        JPanel comandi = new JPanel(new GridLayout(2, 3, 8, 8));
        comandi.add(attacca);
        comandi.add(abilita);
        comandi.add(pozione);
        comandi.add(avanza);
        comandi.add(salva);
        comandi.add(pulsante("Menu iniziale", this::tornaAlMenu));
        pannello.add(comandi, BorderLayout.SOUTH);
        return pannello;
    }

    private void inizia() {
        try {
            partita = gioco.inizia(gioco.campagne().get(campagna.getSelectedIndex()).id(), nome.getText(), (Archetipo) archetipo.getSelectedItem());
            visualizzaPartita();
        } catch (IllegalArgumentException errore) {
            avviso(errore.getMessage());
        }
    }

    private void riprendi() {
        try {
            Optional<Partita> salvata = gioco.riprendi();
            if (salvata.isEmpty()) {
                avviso("Non c'è ancora una partita salvata.");
                return;
            }
            partita = salvata.get();
            visualizzaPartita();
            messaggio.setText("Partita caricata. Puoi avanzare nella prossima stanza.");
        } catch (IOException errore) {
            avviso("Caricamento fallito. " + errore.getMessage());
        }
    }

    private void visualizzaPartita() {
        aggiorna();
        schede.show(contenuto, "partita");
    }

    private void turno(Azione azione) {
        partita = gioco.gioca(partita, azione).partita();
        aggiorna();
    }

    private void avanza() {
        partita = gioco.avanza(partita);
        aggiorna();
    }

    private void salva() {
        try {
            gioco.salva(partita);
            messaggio.setText("Partita salvata.");
        } catch (IOException errore) {
            avviso("Salvataggio fallito. " + errore.getMessage());
        }
    }

    private void tornaAlMenu() {
        Fase fase = gioco.fase(partita);
        if (fase == Fase.COMBATTIMENTO || fase == Fase.TRA_STANZE) {
            int scelta = JOptionPane.showConfirmDialog(finestra,
                    "I progressi dall'ultimo salvataggio andranno persi. Tornare al menu?",
                    "Torna al menu", JOptionPane.YES_NO_OPTION);
            if (scelta != JOptionPane.YES_OPTION) {
                return;
            }
        }
        partita = null;
        schede.show(contenuto, "menu");
    }

    private void aggiorna() {
        Eroe eroe = partita.eroe();
        Stanza stanza = gioco.stanza(partita);
        Fase fase = gioco.fase(partita);
        luogo.setText("Stanza " + (partita.indiceStanza() + 1) + " · " + stanza.nome());
        nomeEroe.setText(eroe.nome() + " · " + eroe.archetipo());
        nomeNemico.setText(stanza.nemico());
        impostaSalute(saluteEroe, eroe.salute(), eroe.archetipo().saluteMassima());
        impostaSalute(saluteNemico, partita.saluteNemico(), stanza.saluteNemico());
        inventario.setText("Pozioni: " + eroe.pozioni());
        messaggio.setText(switch (fase) {
            case COMBATTIMENTO -> "Scegli la prossima azione.";
            case TRA_STANZE -> "Stanza liberata. Puoi salvare o avanzare.";
            case VITTORIA -> "Vittoria! Hai completato la campagna.";
            case SCONFITTA -> "Sconfitta. Il tuo eroe è caduto.";
        });
        boolean combattimento = fase == Fase.COMBATTIMENTO;
        attacca.setEnabled(combattimento);
        abilita.setText(eroe.archetipo() == Archetipo.GUERRIERO ? "Colpo protetto" : "Dardo arcano");
        abilita.setEnabled(combattimento && partita.abilitaDisponibile());
        pozione.setEnabled(combattimento && eroe.pozioni() > 0
                && eroe.salute() < eroe.archetipo().saluteMassima());
        avanza.setEnabled(fase == Fase.TRA_STANZE);
        salva.setEnabled(fase == Fase.TRA_STANZE);
    }

    private void avviso(String testo) {
        JOptionPane.showMessageDialog(finestra, testo);
    }

    private static void impostaSalute(JProgressBar barra, int salute, int massimo) {
        barra.setMaximum(massimo);
        barra.setValue(salute);
        barra.setString(salute + " / " + massimo);
        barra.setStringPainted(true);
    }

    private static JButton pulsante(String testo, Runnable azione) {
        JButton pulsante = new JButton(testo);
        pulsante.addActionListener(evento -> azione.run());
        return pulsante;
    }
}
