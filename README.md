# Le Rovine di Vesper

Gioco di ruolo a turni per desktop. Crea un Guerriero o un Mago e scegli tra Le Rovine di Vesper e Il Faro delle Maree. Segui i combattimenti nel diario di battaglia e salva tra due stanze.

Servono un JDK 17 e una connessione Internet per scaricare Gradle e le dipendenze dei test al primo build. Dalla cartella del progetto, su Linux e macOS:

```sh
./gradlew build
./gradlew run
```

Su Windows, da PowerShell:

```powershell
.\gradlew.bat build
.\gradlew.bat run
```

Per aprirlo in IntelliJ IDEA, seleziona questa cartella con **File → Open** e imposta un JDK 17 per Gradle.

Il comando `build` esegue anche i test JUnit.

**Uso dell'AI.** Codex è stato utilizzato per la progettazione, la generazione del codice e delle verifiche, e la stesura della documentazione. La dichiarazione dettagliata è nella Wiki del repository.
