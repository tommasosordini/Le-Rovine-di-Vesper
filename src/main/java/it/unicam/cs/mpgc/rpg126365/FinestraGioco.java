package it.unicam.cs.mpgc.rpg126365;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Locale;

final class FinestraGioco {
    private final Gioco gioco;
    private final JFrame finestra = new JFrame("Le Rovine di Vesper");
    private final CardLayout schede = new CardLayout();
    private final JPanel contenuto = new JPanel(schede);
    private final VedutaCampagna copertina = new VedutaCampagna(false);
    private final VedutaCampagna scenario = new VedutaCampagna(true);
    private final JTextField nome = new JTextField();
    private final JLabel titoloCampagna = titolo(36);
    private final JLabel descrizioneCampagna = etichetta(Stile.TESTO, 15);
    private final JLabel descrizioneClasse = etichetta(Stile.TENUE, 13);
    private final JLabel statoMenu = etichetta(Stile.CORALLO, 13);
    private final JLabel avanzamento = etichetta(Stile.ORO, 13);
    private final JLabel luogo = titolo(32);
    private final JLabel nomeEroe = titolo(23);
    private final JLabel nomeNemico = titolo(23);
    private final JLabel inventario = etichetta(Stile.TENUE, 13);
    private final JLabel attaccoNemico = etichetta(Stile.TENUE, 13);
    private final JLabel faseCorrente = titolo(23);
    private final JLabel messaggio = etichetta(Stile.TENUE, 13);
    private final JLabel statoSalvataggio = etichetta(Stile.VERDE, 13);
    private final JLabel puntiEroe = etichetta(Stile.VERDE, 13);
    private final JLabel puntiNemico = etichetta(Stile.CORALLO, 13);
    private final JProgressBar saluteEroe = Stile.barra(Stile.VERDE, "Salute dell'eroe");
    private final JProgressBar saluteNemico = Stile.barra(Stile.CORALLO, "Salute del nemico");
    private final JTextArea diario = new JTextArea();
    private final JButton inizia = Stile.pulsante("Inizia partita", true, this::inizia);
    private final JButton attacca = Stile.pulsante("Attacca", true, () -> turno(Azione.ATTACCA));
    private final JButton abilita = Stile.pulsante("Abilità", false, () -> turno(Azione.USA_ABILITA));
    private final JButton pozione = Stile.pulsante("Bevi pozione", false, () -> turno(Azione.BEVI_POZIONE));
    private final JButton avanza = Stile.pulsante("Avanza", true, this::avanza);
    private final JButton salva = Stile.pulsante("Salva", false, this::salva);
    private Campagna campagna;
    private Archetipo archetipo;
    private Partita partita;
    private Partita ultimoSalvataggio;

    FinestraGioco(Gioco gioco) {
        this.gioco = gioco;
        contenuto.add(menu(), "menu");
        contenuto.add(schermataPartita(), "partita");
        finestra.setContentPane(contenuto);
        finestra.getRootPane().setDefaultButton(inizia);
        finestra.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        finestra.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                if (confermaUscita("Chiudere il gioco?")) {
                    finestra.dispose();
                }
            }
        });
        finestra.setMinimumSize(new Dimension(875, 650));
        finestra.setSize(1070, 720);
        finestra.setLocationRelativeTo(null);
        associaTasto(KeyEvent.VK_1, attacca);
        associaTasto(KeyEvent.VK_2, abilita);
        associaTasto(KeyEvent.VK_3, pozione);
    }

    void mostra() {
        finestra.setVisible(true);
    }

    private JPanel menu() {
        JPanel pagina = Stile.pannello(new BorderLayout(), Stile.FONDO);
        JPanel didascalia = colonna();
        didascalia.setBorder(BorderFactory.createEmptyBorder(24, 31, 38, 24));
        aggiungi(didascalia, titoloCampagna, 10);
        aggiungi(didascalia, descrizioneCampagna, 0);
        copertina.add(didascalia, BorderLayout.SOUTH);
        pagina.add(copertina, BorderLayout.CENTER);

        JPanel modulo = Stile.pannello(new BorderLayout(), Stile.FONDO);
        modulo.setPreferredSize(new Dimension(405, 580));
        modulo.setBorder(BorderFactory.createEmptyBorder(30, 26, 26, 26));
        JPanel campi = colonna();
        JLabel intestazione = titolo(28);
        intestazione.setText("Nuova partita");
        aggiungi(campi, intestazione, 22);

        aggiungi(campi, etichetta("Campagna", Stile.TESTO, 14, Font.BOLD), 8);
        JPanel campagne = trasparente(new GridLayout(0, 2, 7, 7));
        ButtonGroup gruppoCampagne = new ButtonGroup();
        for (Campagna opzione : gioco.campagne()) {
            JToggleButton scelta = Stile.scelta(opzione.nome());
            scelta.addActionListener(evento -> scegliCampagna(opzione));
            gruppoCampagne.add(scelta);
            campagne.add(scelta);
        }
        gruppoCampagne.getElements().nextElement().setSelected(true);
        scegliCampagna(gioco.campagne().get(0));
        aggiungi(campi, campagne, 18);

        JLabel etichettaNome = etichetta("Nome dell'eroe", Stile.TESTO, 14, Font.BOLD);
        etichettaNome.setLabelFor(nome);
        aggiungi(campi, etichettaNome, 8);
        nome.setFont(Stile.testo(Font.PLAIN, 16));
        nome.setForeground(Stile.TESTO);
        nome.setCaretColor(Stile.ORO);
        nome.setBackground(Stile.SUPERFICIE);
        nome.setBorder(Stile.cornice(9));
        aggiungi(campi, nome, 18);

        aggiungi(campi, etichetta("Classe", Stile.TESTO, 14, Font.BOLD), 8);
        JPanel classi = trasparente(new GridLayout(1, 0, 7, 0));
        ButtonGroup gruppoClassi = new ButtonGroup();
        for (Archetipo opzione : Archetipo.values()) {
            JToggleButton scelta = Stile.scelta(nomeClasse(opzione));
            scelta.addActionListener(evento -> scegliClasse(opzione));
            gruppoClassi.add(scelta);
            classi.add(scelta);
        }
        gruppoClassi.getElements().nextElement().setSelected(true);
        scegliClasse(Archetipo.values()[0]);
        aggiungi(campi, classi, 8);
        aggiungi(campi, descrizioneClasse, 12);
        aggiungi(campi, statoMenu, 0);
        modulo.add(campi, BorderLayout.NORTH);

        JPanel comandi = colonna();
        aggiungi(comandi, inizia, 8);
        aggiungi(comandi, Stile.pulsante("Riprendi partita salvata", false, this::riprendi), 0);
        modulo.add(comandi, BorderLayout.SOUTH);
        pagina.add(modulo, BorderLayout.EAST);
        return pagina;
    }

    private JPanel schermataPartita() {
        JPanel pagina = Stile.pannello(new BorderLayout(0, 13), Stile.FONDO);
        pagina.setBorder(BorderFactory.createEmptyBorder(18, 20, 19, 20));
        scenario.setPreferredSize(new Dimension(700, 142));
        JPanel testata = colonna();
        testata.setBorder(BorderFactory.createEmptyBorder(19, 23, 17, 18));
        aggiungi(testata, avanzamento, 8);
        aggiungi(testata, luogo, 0);
        scenario.add(testata, BorderLayout.WEST);
        pagina.add(scenario, BorderLayout.NORTH);

        JPanel area = trasparente(new BorderLayout(0, 12));
        JPanel combattenti = trasparente(new GridLayout(1, 2, 12, 0));
        combattenti.add(schedaCombattente("Eroe", nomeEroe, puntiEroe, saluteEroe, inventario));
        combattenti.add(schedaCombattente("Nemico", nomeNemico, puntiNemico, saluteNemico, attaccoNemico));
        area.add(combattenti, BorderLayout.NORTH);

        JPanel pannelloDiario = Stile.pannello(new BorderLayout(0, 10), Stile.SUPERFICIE);
        pannelloDiario.setBorder(Stile.cornice(13));
        pannelloDiario.add(etichetta("Diario di battaglia", Stile.TESTO, 15, Font.BOLD), BorderLayout.NORTH);
        diario.setEditable(false);
        diario.setLineWrap(true);
        diario.setWrapStyleWord(true);
        diario.setForeground(Stile.TESTO);
        diario.setBackground(Stile.FONDO);
        diario.setFont(Stile.testo(Font.PLAIN, 14));
        diario.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));
        diario.getAccessibleContext().setAccessibleName("Diario di battaglia");
        JScrollPane scorrimento = new JScrollPane(diario);
        scorrimento.setBorder(Stile.cornice(0));
        pannelloDiario.add(scorrimento, BorderLayout.CENTER);

        JPanel parteBassa = trasparente(new BorderLayout(12, 0));
        parteBassa.add(pannelloDiario, BorderLayout.CENTER);
        parteBassa.add(comandiPartita(), BorderLayout.EAST);
        area.add(parteBassa, BorderLayout.CENTER);
        pagina.add(area, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel comandiPartita() {
        JPanel pannello = Stile.pannello(new BorderLayout(0, 10), Stile.SUPERFICIE);
        pannello.setPreferredSize(new Dimension(297, 0));
        pannello.setBorder(Stile.cornice(12));
        JPanel azioni = colonna();
        aggiungi(azioni, faseCorrente, 4);
        aggiungi(azioni, messaggio, 9);
        aggiungi(azioni, attacca, 5);
        JPanel abilitaEPozione = trasparente(new GridLayout(1, 0, 6, 0));
        abilitaEPozione.add(abilita);
        abilitaEPozione.add(pozione);
        aggiungi(azioni, abilitaEPozione, 0);
        pannello.add(azioni, BorderLayout.NORTH);

        JPanel navigazione = colonna();
        aggiungi(navigazione, statoSalvataggio, 6);
        JPanel percorso = trasparente(new GridLayout(1, 0, 6, 0));
        percorso.add(salva);
        percorso.add(avanza);
        aggiungi(navigazione, percorso, 5);
        aggiungi(navigazione, Stile.pulsante("Menu iniziale", false, this::tornaAlMenu), 0);
        pannello.add(navigazione, BorderLayout.SOUTH);
        return pannello;
    }

    private void scegliCampagna(Campagna scelta) {
        campagna = scelta;
        copertina.mostra(scelta.id());
        titoloCampagna.setText(scelta.nome());
        descrizioneCampagna.setText(switch (scelta.id()) {
            case "vesper" -> "Scendi nella cripta e affronta il guardiano del sigillo.";
            case "faro" -> "Raggiungi la lanterna del faro prima che la marea salga.";
            default -> scelta.stanze().size() + " stanze da esplorare.";
        });
    }

    private void scegliClasse(Archetipo scelta) {
        archetipo = scelta;
        String effetto = scelta == Archetipo.GUERRIERO
                ? "Colpo protetto riduce il contrattacco."
                : "Dardo arcano infligge più danni.";
        descrizioneClasse.setText(scelta.saluteMassima() + " PV · " + effetto);
    }

    private void inizia() {
        try {
            partita = gioco.inizia(campagna.id(), nome.getText(), archetipo);
            ultimoSalvataggio = null;
            visualizzaPartita("Incontri " + gioco.stanza(partita).nemico() + ".");
        } catch (IllegalArgumentException errore) {
            statoMenu.setText("Inserisci un nome valido di massimo 30 caratteri.");
            nome.requestFocusInWindow();
        }
    }

    private void riprendi() {
        try {
            var salvata = gioco.riprendi();
            if (salvata.isEmpty()) {
                statoMenu.setText("Non c'è ancora una partita salvata.");
                return;
            }
            partita = salvata.orElseThrow();
            ultimoSalvataggio = partita;
            visualizzaPartita("Partita caricata.");
        } catch (IOException errore) {
            statoMenu.setText("Caricamento fallito. Controlla il salvataggio.");
            avviso(errore.getMessage());
        }
    }

    private void visualizzaPartita(String evento) {
        scenario.mostra(partita.idCampagna());
        diario.setText("");
        annota(evento);
        aggiorna();
        schede.show(contenuto, "partita");
        finestra.getRootPane().setDefaultButton(null);
    }

    private void turno(Azione azione) {
        EsitoTurno esito = gioco.gioca(partita, azione);
        partita = esito.partita();
        String evento = switch (esito.azione()) {
            case ATTACCA -> "Attacco: " + esito.danniInflitti() + " danni.";
            case USA_ABILITA -> nomeAbilita() + ": " + esito.danniInflitti() + " danni.";
            case BEVI_POZIONE -> "Pozione: " + esito.saluteRecuperata() + " PV recuperati.";
        };
        Stanza stanza = gioco.stanza(partita);
        String risposta = partita.saluteNemico() == 0 ? stanza.nemico() + ": sconfitto."
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
            ultimoSalvataggio = partita;
            annota("Partita salvata.");
            aggiorna();
        } catch (IOException errore) {
            avviso("Salvataggio fallito. " + errore.getMessage());
        }
    }

    private void tornaAlMenu() {
        if (confermaUscita("Tornare al menu iniziale?")) {
            partita = null;
            statoMenu.setText("");
            schede.show(contenuto, "menu");
            finestra.getRootPane().setDefaultButton(inizia);
            nome.requestFocusInWindow();
        }
    }

    private boolean confermaUscita(String domanda) {
        if (partita == null || partita.equals(ultimoSalvataggio)) {
            return true;
        }
        Fase fase = gioco.fase(partita);
        return fase == Fase.VITTORIA || fase == Fase.SCONFITTA
                || JOptionPane.showConfirmDialog(finestra, domanda + " I progressi non salvati andranno persi.",
                "Progressi non salvati", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void aggiorna() {
        Eroe eroe = partita.eroe();
        Stanza stanza = gioco.stanza(partita);
        Fase fase = gioco.fase(partita);
        Campagna corrente = gioco.campagne().stream()
                .filter(opzione -> opzione.id().equals(partita.idCampagna())).findFirst().orElseThrow();
        avanzamento.setText(corrente.nome() + "   /   Stanza " + (partita.indiceStanza() + 1)
                + " di " + corrente.stanze().size());
        luogo.setText(stanza.nome());
        nomeEroe.setText(eroe.nome());
        nomeNemico.setText(stanza.nemico());
        impostaSalute(saluteEroe, puntiEroe, eroe.salute(), eroe.archetipo().saluteMassima());
        impostaSalute(saluteNemico, puntiNemico, partita.saluteNemico(), stanza.saluteNemico());
        inventario.setText(nomeClasse(eroe.archetipo()) + "   /   Pozioni " + eroe.pozioni());
        attaccoNemico.setText("Attacco base " + stanza.attaccoNemico());
        faseCorrente.setText(switch (fase) {
            case COMBATTIMENTO -> "In combattimento";
            case TRA_STANZE -> "Stanza liberata";
            case VITTORIA -> "Vittoria";
            case SCONFITTA -> "Sconfitta";
        });
        faseCorrente.setForeground(fase == Fase.SCONFITTA ? Stile.CORALLO : Stile.ORO);
        messaggio.setText(switch (fase) {
            case COMBATTIMENTO -> "Scegli un'azione o premi 1, 2, 3.";
            case TRA_STANZE -> "Salva i progressi oppure avanza.";
            case VITTORIA -> "Hai completato la campagna.";
            case SCONFITTA -> "Il tuo eroe è caduto.";
        });
        boolean combattimento = fase == Fase.COMBATTIMENTO;
        boolean traStanze = fase == Fase.TRA_STANZE;
        abilita.setText(nomeAbilita());
        abilita.setToolTipText(partita.abilitaDisponibile() ? "Tasto 2" : "Abilità già usata in questa stanza");
        attacca.setToolTipText("Tasto 1");
        pozione.setToolTipText("Tasto 3 · Cura fino a 14 PV");
        attacca.setEnabled(combattimento);
        abilita.setEnabled(combattimento && partita.abilitaDisponibile());
        pozione.setEnabled(combattimento && eroe.pozioni() > 0 && eroe.salute() < eroe.archetipo().saluteMassima());
        salva.setEnabled(traStanze);
        avanza.setEnabled(traStanze);
        statoSalvataggio.setText(partita.equals(ultimoSalvataggio) ? "Progressi salvati" : " ");
    }

    private String nomeAbilita() {
        return partita.eroe().archetipo() == Archetipo.GUERRIERO ? "Colpo protetto" : "Dardo arcano";
    }

    private void annota(String evento) {
        diario.append(evento + "\n\n");
        diario.setCaretPosition(diario.getDocument().getLength());
    }

    private void avviso(String testo) {
        JOptionPane.showMessageDialog(finestra, testo);
    }

    private void associaTasto(int codice, JButton pulsante) {
        String chiave = "azione-" + codice;
        contenuto.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(codice, 0), chiave);
        contenuto.getActionMap().put(chiave, new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evento) {
                if (pulsante.isShowing() && pulsante.isEnabled()) {
                    pulsante.doClick();
                }
            }
        });
    }

    private static JPanel schedaCombattente(String ruolo, JLabel nome, JLabel punti,
                                            JProgressBar salute, JLabel dettaglio) {
        JPanel scheda = Stile.pannello(new BorderLayout(0, 5), Stile.SUPERFICIE);
        scheda.setBorder(Stile.cornice(12));
        JPanel testata = colonna();
        aggiungi(testata, etichetta(ruolo, Stile.TENUE, 13, Font.BOLD), 3);
        aggiungi(testata, nome, 0);
        JPanel valori = colonna();
        aggiungi(valori, punti, 5);
        aggiungi(valori, salute, 5);
        aggiungi(valori, dettaglio, 0);
        scheda.add(testata, BorderLayout.NORTH);
        scheda.add(valori, BorderLayout.SOUTH);
        return scheda;
    }

    private static void impostaSalute(JProgressBar barra, JLabel punti, int salute, int massimo) {
        barra.setMaximum(massimo);
        barra.setValue(salute);
        punti.setText(salute + " / " + massimo + " PV");
    }

    private static String nomeClasse(Archetipo scelta) {
        String minuscolo = scelta.name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(minuscolo.charAt(0)) + minuscolo.substring(1);
    }

    private static JLabel titolo(int dimensione) {
        JLabel titolo = etichetta(Stile.TESTO, dimensione);
        titolo.setFont(Stile.titolo(dimensione));
        return titolo;
    }

    private static JLabel etichetta(Color colore, int dimensione) {
        return etichetta(" ", colore, dimensione, Font.PLAIN);
    }

    private static JLabel etichetta(String testo, Color colore, int dimensione, int stile) {
        JLabel etichetta = Stile.etichetta(testo, colore, stile, dimensione);
        etichetta.putClientProperty("html.disable", true);
        return etichetta;
    }

    private static JPanel colonna() {
        JPanel pannello = new JPanel();
        pannello.setOpaque(false);
        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
        return pannello;
    }

    private static JPanel trasparente(LayoutManager layout) {
        JPanel pannello = new JPanel(layout);
        pannello.setOpaque(false);
        return pannello;
    }

    private static void aggiungi(JPanel pannello, JComponent componente, int spazio) {
        componente.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, componente.getPreferredSize().height));
        pannello.add(componente);
        if (spazio > 0) {
            pannello.add(Box.createVerticalStrut(spazio));
        }
    }
}
