package it.unicam.cs.mpgc.rpg126365.desktop;

import it.unicam.cs.mpgc.rpg126365.applicazione.Gioco;
import it.unicam.cs.mpgc.rpg126365.dominio.Archetipo;
import it.unicam.cs.mpgc.rpg126365.dominio.Azione;
import it.unicam.cs.mpgc.rpg126365.dominio.Campagna;
import it.unicam.cs.mpgc.rpg126365.dominio.Eroe;
import it.unicam.cs.mpgc.rpg126365.dominio.EsitoTurno;
import it.unicam.cs.mpgc.rpg126365.dominio.Fase;
import it.unicam.cs.mpgc.rpg126365.dominio.Partita;
import it.unicam.cs.mpgc.rpg126365.dominio.Stanza;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.Optional;

final class FinestraGioco {
    private static final Color INCHIOSTRO = new Color(28, 43, 52);
    private static final Color SFONDO = new Color(238, 243, 242);
    private static final Color SALUTE_EROE = new Color(37, 116, 95);
    private static final Color SALUTE_NEMICO = new Color(160, 60, 55);

    private final Gioco gioco;
    private final JFrame finestra = new JFrame("Le Rovine di Vesper");
    private final CardLayout schede = new CardLayout();
    private final JPanel contenuto = new JPanel(schede);
    private final JTextField nome = new JTextField(18);
    private final JComboBox<Archetipo> archetipo = new JComboBox<>(Archetipo.values());
    private final JComboBox<Campagna> campagna;
    private final JLabel titolo = new JLabel("Le Rovine di Vesper");
    private final JLabel luogo = new JLabel();
    private final JLabel nomeEroe = new JLabel();
    private final JLabel nomeNemico = new JLabel();
    private final JLabel inventario = new JLabel();
    private final JLabel messaggio = new JLabel();
    private final JProgressBar saluteEroe = barraSalute(SALUTE_EROE, "Salute dell'eroe");
    private final JProgressBar saluteNemico = barraSalute(SALUTE_NEMICO, "Salute del nemico");
    private final JTextArea diario = new JTextArea(6, 36);
    private final JButton attacca = pulsante("Attacca", () -> turno(Azione.ATTACCA));
    private final JButton abilita = pulsante("Abilità", () -> turno(Azione.USA_ABILITA));
    private final JButton pozione = pulsante("Bevi pozione", () -> turno(Azione.BEVI_POZIONE));
    private final JButton avanza = pulsante("Avanza", this::avanza);
    private final JButton salva = pulsante("Salva", this::salva);
    private Partita partita;

    FinestraGioco(Gioco gioco) {
        this.gioco = gioco;
        campagna = new JComboBox<>(gioco.campagne().toArray(Campagna[]::new));
        DefaultListCellRenderer rendererCampagna = new DefaultListCellRenderer();
        campagna.setRenderer((lista, valore, indice, selezionato, focus) ->
                rendererCampagna.getListCellRendererComponent(
                        lista, valore == null ? "" : valore.nome(), indice, selezionato, focus));
        nomeEroe.putClientProperty("html.disable", true);
        contenuto.add(menu(), "menu");
        contenuto.add(schermataPartita(), "partita");

        titolo.setOpaque(true);
        titolo.setBackground(INCHIOSTRO);
        titolo.setForeground(Color.WHITE);
        titolo.setFont(titolo.getFont().deriveFont(Font.BOLD, 23f));
        titolo.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        JPanel radice = new JPanel(new BorderLayout());
        radice.add(titolo, BorderLayout.NORTH);
        radice.add(contenuto, BorderLayout.CENTER);
        finestra.setContentPane(radice);
        finestra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        finestra.setMinimumSize(new Dimension(575, 455));
        finestra.setSize(690, 505);
        finestra.setLocationRelativeTo(null);
    }

    void mostra() {
        finestra.setVisible(true);
    }

    private JPanel menu() {
        JPanel pannello = pannello();
        JPanel campi = new JPanel(new GridLayout(0, 1, 0, 7));
        campi.setOpaque(false);
        aggiungiCampo(campi, "Campagna", campagna);
        aggiungiCampo(campi, "Nome dell'eroe", nome);
        aggiungiCampo(campi, "Classe", archetipo);
        JPanel comandi = new JPanel(new GridLayout(1, 2, 10, 0));
        comandi.setOpaque(false);
        comandi.add(pulsante("Inizia partita", this::inizia));
        comandi.add(pulsante("Riprendi partita salvata", this::riprendi));
        pannello.add(campi, BorderLayout.NORTH);
        pannello.add(comandi, BorderLayout.SOUTH);
        return pannello;
    }

    private JPanel schermataPartita() {
        JPanel pannello = pannello();
        JPanel testata = new JPanel(new BorderLayout(0, 9));
        testata.setOpaque(false);
        luogo.setFont(luogo.getFont().deriveFont(Font.BOLD, 17f));
        testata.add(luogo, BorderLayout.NORTH);
        JPanel combattenti = new JPanel(new GridLayout(1, 2, 10, 0));
        combattenti.setOpaque(false);
        combattenti.add(schedaCombattente(nomeEroe, saluteEroe, inventario));
        combattenti.add(schedaCombattente(nomeNemico, saluteNemico, new JLabel("Nemico")));
        testata.add(combattenti, BorderLayout.CENTER);
        pannello.add(testata, BorderLayout.NORTH);

        diario.setEditable(false);
        diario.setLineWrap(true);
        diario.setWrapStyleWord(true);
        diario.setFont(messaggio.getFont());
        diario.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane scorrimento = new JScrollPane(diario);
        scorrimento.setBorder(BorderFactory.createTitledBorder("Diario di battaglia"));
        pannello.add(scorrimento, BorderLayout.CENTER);

        JPanel fondo = new JPanel(new BorderLayout(0, 8));
        fondo.setOpaque(false);
        JPanel comandi = new JPanel(new GridLayout(2, 3, 8, 6));
        comandi.setOpaque(false);
        comandi.add(attacca);
        comandi.add(abilita);
        comandi.add(pozione);
        comandi.add(avanza);
        comandi.add(salva);
        comandi.add(pulsante("Menu iniziale", this::tornaAlMenu));
        fondo.add(messaggio, BorderLayout.NORTH);
        fondo.add(comandi, BorderLayout.CENTER);
        pannello.add(fondo, BorderLayout.SOUTH);
        return pannello;
    }

    private void inizia() {
        try {
            Campagna scelta = campagna.getItemAt(campagna.getSelectedIndex());
            partita = gioco.inizia(scelta.id(), nome.getText(), (Archetipo) archetipo.getSelectedItem());
            visualizzaPartita("Incontri " + gioco.stanza(partita).nemico() + ".");
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
            visualizzaPartita("Partita caricata.");
        } catch (IOException errore) {
            avviso("Caricamento fallito. " + errore.getMessage());
        }
    }

    private void visualizzaPartita(String evento) {
        gioco.campagne().stream()
                .filter(candidata -> candidata.id().equals(partita.idCampagna()))
                .findFirst()
                .ifPresent(candidata -> titolo.setText(candidata.nome()));
        diario.setText("");
        annota(evento);
        aggiorna();
        schede.show(contenuto, "partita");
    }

    private void turno(Azione azione) {
        EsitoTurno esito = gioco.gioca(partita, azione);
        partita = esito.partita();
        String evento = switch (esito.azione()) {
            case ATTACCA -> "Attacco: " + esito.danniInflitti() + " danni.";
            case USA_ABILITA -> nomeAbilita() + ": " + esito.danniInflitti() + " danni.";
            case BEVI_POZIONE -> "Pozione: " + esito.saluteRecuperata() + " punti salute recuperati.";
        };
        Stanza stanza = gioco.stanza(partita);
        String risposta = partita.saluteNemico() == 0 ? stanza.nemico() + " sconfitto."
                : stanza.nemico() + " contrattacca: " + esito.danniRicevuti() + " danni.";
        annota(evento + " " + risposta);
        aggiorna();
    }

    private void avanza() {
        partita = gioco.avanza(partita);
        annota("Incontri " + gioco.stanza(partita).nemico() + ".");
        aggiorna();
    }

    private void salva() {
        try {
            gioco.salva(partita);
            annota("Partita salvata.");
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
        titolo.setText(finestra.getTitle());
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
        abilita.setText(nomeAbilita());
        abilita.setEnabled(combattimento && partita.abilitaDisponibile());
        pozione.setEnabled(combattimento && eroe.pozioni() > 0
                && eroe.salute() < eroe.archetipo().saluteMassima());
        avanza.setEnabled(fase == Fase.TRA_STANZE);
        salva.setEnabled(fase == Fase.TRA_STANZE);
    }

    private String nomeAbilita() {
        return partita.eroe().archetipo() == Archetipo.GUERRIERO ? "Colpo protetto" : "Dardo arcano";
    }

    private void annota(String evento) {
        diario.append(evento + "\n");
        diario.setCaretPosition(diario.getDocument().getLength());
    }

    private void avviso(String testo) {
        JOptionPane.showMessageDialog(finestra, testo);
    }

    private static JPanel pannello() {
        JPanel pannello = new JPanel(new BorderLayout(0, 14));
        pannello.setBackground(SFONDO);
        pannello.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        return pannello;
    }

    private static JPanel schedaCombattente(JLabel nome, JProgressBar salute, JLabel dettaglio) {
        JPanel scheda = new JPanel(new GridLayout(0, 1, 0, 4));
        scheda.setBackground(Color.WHITE);
        scheda.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        nome.setFont(nome.getFont().deriveFont(Font.BOLD));
        scheda.add(nome);
        scheda.add(salute);
        scheda.add(dettaglio);
        return scheda;
    }

    private static void aggiungiCampo(JPanel pannello, String testo, JComponent campo) {
        JLabel etichetta = new JLabel(testo);
        etichetta.setLabelFor(campo);
        pannello.add(etichetta);
        pannello.add(campo);
    }

    private static JProgressBar barraSalute(Color colore, String nome) {
        JProgressBar barra = new JProgressBar();
        barra.setForeground(colore);
        barra.setStringPainted(true);
        barra.getAccessibleContext().setAccessibleName(nome);
        return barra;
    }

    private static void impostaSalute(JProgressBar barra, int salute, int massimo) {
        barra.setMaximum(massimo);
        barra.setValue(salute);
        barra.setString(salute + " / " + massimo);
    }

    private static JButton pulsante(String testo, Runnable azione) {
        JButton pulsante = new JButton(testo);
        pulsante.addActionListener(evento -> azione.run());
        return pulsante;
    }
}
