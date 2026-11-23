# Le Rovine di Vesper

Gioco di ruolo a turni per desktop. Crea un Guerriero o un Mago e scegli tra due campagne illustrate, Le Rovine di Vesper e Il Faro delle Maree. Segui i combattimenti nel diario di battaglia e salva tra due stanze.

## Come eseguire il progetto

### Prerequisiti

- JDK 17
- Connessione Internet al primo build per scaricare Gradle e le dipendenze dei test

Il Gradle Wrapper è incluso nel repository.

### Istruzioni

Copia l'URL HTTPS del repository dal pulsante **Code** su GitHub e usalo al posto di `URL_DEL_REPOSITORY`.

```text
git clone https://github.com/tommasosordini/Le-Rovine-di-Vesper.git LeRovineDiVesper
cd LeRovineDiVesper
```

### Build del progetto

```sh
./gradlew build
```

Il comando esegue anche i test JUnit.

### Esecuzione

```sh
./gradlew run
```

Da PowerShell su Windows, usa questi comandi.

```powershell
.\gradlew.bat build
.\gradlew.bat run
```

Per aprirlo in IntelliJ IDEA, seleziona questa cartella con **File → Open** e imposta un JDK 17 per Gradle.

## 🤖 Uso di strumenti di AI

- **OpenAI Codex** ha contribuito al debugging e alla documentazione.
- **OpenAI ImageGen** ha generato le due illustrazioni delle campagne.

La dichiarazione dettagliata è nella pagina **Dichiarazione di uso dell'AI** della Wiki del repository.
