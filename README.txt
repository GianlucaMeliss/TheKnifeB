Progetto TheKnife - Architettura Client/Server

======================================================================
1. PREREQUISITI SOFTWARE
======================================================================
Prima di procedere, è strettamente necessario assicurarsi che sul sistema sia installato e configurato il seguente software:

1.1. Java Development Kit (JDK)
È richiesta una versione 21 o superiore. Si consiglia di scaricare una distribuzione standard come Eclipse Temurin (Adoptium). Assicurarsi che la variabile d'ambiente JAVA_HOME sia configurata correttamente.

1.2. PostgreSQL
È richiesta un'istanza attiva del database relazionale, in esecuzione sulla porta di default (5432).
- Aprire pgAdmin (o la riga di comando psql).
- Creare un database completamente vuoto denominato esattamente: theknife.

1.3. Apache Maven (Configurazione Variabili d'Ambiente)
Maven è richiesto per automatizzare la build e inizializzare il database. Il comando "mvn" deve essere riconosciuto dal terminale.

Come configurare Maven su Windows (Passo-Passo):
- Scaricare l'archivio ZIP dal sito ufficiale di Apache Maven (es. apache-maven-3.9.x-bin.zip).
- Estrarre la cartella in un percorso fisso (es. C:\Program Files\apache-maven).
- Aprire il menu Start di Windows, cercare "Modifica le variabili di ambiente relative al sistema" e premere Invio.
- Cliccare sul pulsante "Variabili d'ambiente..." in basso a destra.
- Nella metà inferiore della finestra (Variabili di sistema), scorrere fino a trovare la variabile "Path". Selezionarla e cliccare su "Modifica...".
- Cliccare su "Nuovo" e incollare il percorso esatto della cartella "bin" di Maven (es. C:\Program Files\apache-maven\bin).
- Cliccare OK su tutte le finestre per salvare. Attenzione: chiudere e riaprire eventuali finestre del Prompt dei Comandi già aperte per applicare le modifiche.

Come configurare Maven su macOS / Linux:
- Su macOS (tramite Homebrew): aprire il terminale ed eseguire "brew install maven".
- Su Linux (Debian/Ubuntu): aprire il terminale ed eseguire "sudo apt install maven".

(Nota: le librerie JavaFX sono integrate all'interno del progetto. Non è necessario scaricare SDK esterni).


======================================================================
2. INIZIALIZZAZIONE DEL DATABASE (POPOLAMENTO)
======================================================================
Il progetto utilizza un plugin Maven per creare automaticamente le tabelle e inserire i dati iniziali di test (mock data).

- Assicurarsi di aver creato il database vuoto "theknife" (come descritto al punto 1.2).
- Il sistema tenta l'accesso a PostgreSQL con Username: "postgres" e Password: "postgres". Se il vostro database locale utilizza credenziali diverse, vi preghiamo di aggiornarle all'interno del file pom.xml (nel tag del sql-maven-plugin) e nella classe Java theknife.db.DatabaseConnection.
- Aprire un terminale nella cartella radice del progetto (dove si trova il file pom.xml).
- Eseguire il seguente comando: 
  mvn sql:execute
- Attendere il messaggio "BUILD SUCCESS" sul terminale. A questo punto, il database è popolato e pronto all'uso.


======================================================================
3. COMPILAZIONE DEL PROGETTO
======================================================================
Il progetto genera automaticamente i file eseguibili per Client e Server.

- Nello stesso terminale aperto nella cartella radice del progetto, eseguire il comando: 
  mvn clean package
- Al termine dell'operazione (BUILD SUCCESS), i file eseguibili "ServerTK.jar" e "ClientTK.jar" verranno generati e posizionati automaticamente all'interno della cartella "bin" del progetto.


======================================================================
4. ESECUZIONE DELL'APPLICAZIONE
======================================================================
L'applicazione necessita dell'avvio separato del modulo server e del modulo client. Entrambi gli eseguibili si trovano nella cartella "bin".

Fase 1: Avvio del Server
Il server gestisce la comunicazione RMI e l'interazione con il database PostgreSQL.
- Aprire un terminale o prompt dei comandi e navigare fino alla cartella "bin".
- Eseguire il comando: 
  java -jar ServerTK.jar
- Seguire le istruzioni a schermo inserendo i parametri richiesti per la connessione al database (Host, Porta, Nome Database, Username e Password).
- Una volta visualizzato il messaggio di conferma e di attesa connessioni, lasciare la finestra del terminale aperta in background.

Fase 2: Avvio del Client (Interfaccia Grafica)
Dopo aver avviato correttamente il server, è possibile lanciare l'interfaccia utente.

* Per utenti Windows: 
  Aprire la cartella "bin" ed eseguire l'applicazione con un semplice doppio clic sul file "ClientTK.jar". (In alternativa, o in caso di problemi con le associazioni di file, aprire un terminale nella cartella "bin" ed eseguire: java -jar ClientTK.jar).

* Per utenti macOS / Linux: 
  Poiché JavaFX richiede librerie grafiche native che differiscono in base al sistema operativo e all'architettura dei processori (es. Intel vs Apple Silicon), l'eseguibile "ClientTK.jar" fornito di default nella cartella "bin" è pre-compilato per ambiente Windows. 
  Per avviare il Client correttamente su macOS o Linux, aprire un terminale nella cartella radice del progetto ed eseguire nuovamente il comando:
  mvn clean package
  
  (Questo comando individuerà il vostro sistema operativo, scaricherà in automatico le librerie native corrette e genererà un nuovo "ClientTK.jar" compatibile all'interno della cartella "bin", pronto per essere avviato).