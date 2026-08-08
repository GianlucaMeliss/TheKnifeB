Progetto TheKnife
1). Prerequisiti Software

Prima di procedere, è necessario assicurarsi che sul sistema sia installato il seguente software:

1.  Java Development Kit (JDK): È richiesta una versione 17 o superiore. Si consiglia di scaricare una distribuzione standard come [Eclipse Temurin (Adoptium)](https://adoptium.net/).
2.  JavaFX SDK: JavaFX non è più incluso nel JDK e deve essere scaricato separatamente.
       All’interno del progetto nella cartella lib sono contenute due versioni di javafx, la versione denominata “javafx-sdk-24.0.1” è quella predisposta per l’utilizzo su sistemi operativi windows mente la cartella denominata “javafx-sdk-24.0-2.2” è quella predisposta per l’utilizzo su sistemi operativi mac

-----

2). Compilazione ed Esecuzione da Sorgente (con IntelliJ IDEA)

Questa è la procedura consigliata per esaminare il codice ed eseguire l'applicazione in un ambiente di sviluppo.

2.1. Configurazione del Progetto

1.  Aprire il Progetto: Aprire la cartella del progetto in IntelliJ IDEA tramite `File > Open`.
2.  Impostare l'SDK del Progetto:
       Andare su `File > Project Structure... > Project`.
       Assicurarsi che nel campo `SDK` sia selezionato un JDK 17 o superiore.
3.  Aggiungere le Librerie:
       Nella stessa finestra (`Project Structure`), andare su Libraries.
       Cliccare il pulsante `+` e scegliere `Java`.
       Navigare e selezionare la cartella `lib` interna al progetto (quella che contiene sia il JavaFX SDK che il file `.jar` di Gson).
4.  Configurare la Struttura dei Moduli:
       Sempre in `Project Structure`, andare su Modules.
       Assicurarsi che la cartella `src` sia marcata come Sources (blu).
       Assicurarsi che la cartella `resources` sia marcata come Resources.
5.  Creare la Configurazione di Avvio:
       Andare su `Run > Edit Configurations...`.
       Cliccare `+` e scegliere `Application`.
       Name: Inserire un nome (es. `Avvia TheKnife`).
       Main class: Cliccare sui tre puntini (`...`) e selezionare la classe ` theknife.TheKnife `.

2.2. Impostazione delle VM Options (Passaggio Cruciale)

Nella stessa finestra di `Edit Configurations`, è necessario specificare dove si trovano i moduli di JavaFX al momento dell'avvio.

   Nel campo VM options, inserire la seguente riga. Questa configurazione è universale e funzionerà su qualsiasi sistema operativo (Windows, macOS, Linux) senza bisogno di modifiche, a patto che la struttura del progetto con la cartella `lib` sia mantenuta.
    --module-path "lib/javafx-sdk-24.0-2.2/lib" --add-modules javafx.controls,javafx.fxml
    (Nota: sostituire `24.0-2.2` con `24.0.1` nel caso in cui utilizziate un sistema windows)

2.3. Avvio

1.  Cliccare `Apply` e `OK` per salvare la configurazione.
2.  Eseguire un `Build > Rebuild Project` per assicurarsi che tutte le impostazioni siano state applicate.
3.  Avviare l'applicazione usando il pulsante "Play"
-----
2.4.Esecuzione da Terminale
Questo metodo non richiede un IDE.

Compilazione:

Aprire un terminale o prompt dei comandi nella cartella principale del progetto (TheKnife).

Eseguire il seguente comando che permette di posizionarsi nella cartella dove viene estratto il progetto e il programma verrà lanciato (adattando la versione di JavaFX e il separatore di percorso: : per Mac/Linux, ; per Windows):

Su Windows:
cd C:\Users\Simoz\IdeaProjects\Melnyk_761001 (sostituire il percorso con quello della cartella dove è stato estratto l’archivio)
e dopo eseguire:
java --module-path "lib\javafx-sdk-24.0.1\lib" --add-modules javafx.controls,javafx.fxml -jar Melnyk_761001.jar

Su Mac/Linux:
cd cd /Users/alessandro/Desktop/Melnyk_761001cd /Users/alessandro/Desktop/Melnyk_761001 (sostituire il percorso con quello della cartella dove è stato estratto l’archivio)
e dopo eseguire:
java --module-path "lib\javafx-sdk-24.0-2.2\lib" --add-modules javafx.controls,javafx.fxml -jar Melnyk_761001.jar (sostituire il percorso con quello della cartella dove è stato estratto l’archivio)



3). Esecuzione del File .jar Compilato

Se si desidera eseguire l'applicazione senza un IDE, è necessario un file `.jar` eseguibile.

3.1. Prerequisiti

L'utente deve avere installato:

   Una versione di Java (JRE o JDK) 17 o superiore.
   Il JavaFX SDK scaricato e decompresso in una cartella sul proprio computer.

3.2. Comando di Avvio

Per eseguire il file ` Melnyk_761001.jar` , è necessario usare il terminale o il prompt dei comandi e specificare il percorso ai moduli JavaFX.

1.  Aprire un terminale.

2.  Navigare con il comando `cd` fino alla cartella dove si trova il file `.jar`. 
(ES. cd C:\Users\Simoz\IdeaProjects\Melnyk_761001)

3.  Eseguire il seguente comando, sostituendo il percorso del JavaFX SDK con quello corretto per la propria macchina: (java --module-path /percorso/completo/del/vostro/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar Melnyk_761001.jar)


Esempi di Percorso

  Windows: `"C:\javafx-sdk-24.0.1\lib"`
  macOS/Linux: `"/Users/nomeutente/sdk/javafx-sdk-24.0.1/lib"`

(Nota: se il percorso contiene spazi, deve essere racchiuso tra virgolette `"`)


AVVIO PERSONALE=cd Desktop/Melnyk_761001;
java --module-path /Users/gianluca/Desktop/Melnyk_761001/lib/javafx-sdk-24.0-2.2/lib --add-modules javafx.controls,javafx.fxml -jar Melnyk_761001.jar