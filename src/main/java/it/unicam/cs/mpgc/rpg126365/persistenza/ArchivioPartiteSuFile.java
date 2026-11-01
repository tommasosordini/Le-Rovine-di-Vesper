package it.unicam.cs.mpgc.rpg126365.persistenza;

import it.unicam.cs.mpgc.rpg126365.applicazione.ArchivioPartite;
import it.unicam.cs.mpgc.rpg126365.dominio.Archetipo;
import it.unicam.cs.mpgc.rpg126365.dominio.Eroe;
import it.unicam.cs.mpgc.rpg126365.dominio.Partita;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Properties;

public final class ArchivioPartiteSuFile implements ArchivioPartite {
    private static final String VERSIONE = "2";
    private final Path file;

    public ArchivioPartiteSuFile(Path file) {
        this.file = file.toAbsolutePath().normalize();
    }

    @Override
    public void salva(Partita partita) throws IOException {
        Eroe eroe = partita.eroe();
        Properties dati = new Properties();
        dati.setProperty("versione", VERSIONE);
        dati.setProperty("campagna", partita.idCampagna());
        dati.setProperty("nome", eroe.nome());
        dati.setProperty("archetipo", eroe.archetipo().name());
        dati.setProperty("salute", Integer.toString(eroe.salute()));
        dati.setProperty("pozioni", Integer.toString(eroe.pozioni()));
        dati.setProperty("stanza", Integer.toString(partita.indiceStanza()));
        dati.setProperty("saluteNemico", Integer.toString(partita.saluteNemico()));
        dati.setProperty("abilitaDisponibile", Boolean.toString(partita.abilitaDisponibile()));

        Files.createDirectories(file.getParent());
        Path temporaneo = Files.createTempFile(file.getParent(), "partita-", ".tmp");
        try {
            try (var writer = Files.newBufferedWriter(temporaneo)) {
                dati.store(writer, "Le Rovine di Vesper");
            }
            Files.move(temporaneo, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaneo);
        }
    }

    @Override
    public Optional<Partita> carica() throws IOException {
        Properties dati = new Properties();
        try (var reader = Files.newBufferedReader(file)) {
            dati.load(reader);
            String versione = campo(dati, "versione");
            String idCampagna = switch (versione) {
                case "1" -> "vesper";
                case VERSIONE -> campo(dati, "campagna");
                default -> throw new IOException("Versione del salvataggio non supportata.");
            };
            Eroe eroe = new Eroe(campo(dati, "nome"), Archetipo.valueOf(campo(dati, "archetipo")),
                    intero(dati, "salute"), intero(dati, "pozioni"));
            String abilita = campo(dati, "abilitaDisponibile");
            if (!"true".equals(abilita) && !"false".equals(abilita)) {
                throw new IllegalArgumentException("Valore dell'abilità non valido.");
            }
            return Optional.of(new Partita(idCampagna, eroe, intero(dati, "stanza"),
                    intero(dati, "saluteNemico"), Boolean.parseBoolean(abilita)));
        } catch (NoSuchFileException assente) {
            return Optional.empty();
        } catch (IllegalArgumentException errore) {
            throw new IOException("Il file di salvataggio contiene dati non validi.", errore);
        }
    }

    private static String campo(Properties dati, String chiave) {
        String valore = dati.getProperty(chiave);
        if (valore == null) {
            throw new IllegalArgumentException("Campo mancante: " + chiave);
        }
        return valore;
    }

    private static int intero(Properties dati, String chiave) {
        return Integer.parseInt(campo(dati, chiave));
    }
}
