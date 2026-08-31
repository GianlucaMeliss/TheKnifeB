Progetto TheKnife - Architettura Client/Server

1). Prerequisiti Software

Prima di procedere, è necessario assicurarsi che sul sistema sia installato il seguente software:

1. Java Development Kit (JDK): È richiesta una versione 21 o superiore. Si consiglia di scaricare una distribuzione standard come Eclipse Temurin (Adoptium).


2. PostgreSQL: È richiesta un'istanza attiva del database relazionale. Il database deve contenere lo schema e le tabelle necessarie (ristoranti, utenti, recensioni, ecc.) importabili tramite lo script fornito nel progetto.



Nota: A differenza delle versioni precedenti, le librerie JavaFX sono ora integrate direttamente all'interno dell'eseguibile del Client grazie all'utilizzo di Maven. Non è più necessario scaricare SDK esterni o configurare percorsi complessi.

---

2). Compilazione del Progetto (tramite Maven)

Il progetto utilizza Maven per la gestione delle dipendenze e l'automazione della build.

1. Aprire un terminale o prompt dei comandi nella cartella principale del progetto (dove si trova il file pom.xml).


2. Eseguire il seguente comando:
mvn clean package
3. Al termine dell'operazione, i file eseguibili "ServerTK.jar" e "ClientTK.jar" verranno generati automaticamente all'interno della cartella "target".


4. Per la corretta esecuzione secondo le specifiche di consegna, assicurarsi di spostare i due file .jar all'interno della cartella "bin".



---

3). Esecuzione dell'Applicazione

L'applicazione è basata su un'architettura distribuita e necessita dell'avvio separato del modulo server e del modulo client. Entrambi gli eseguibili si trovano nella cartella "bin".

3.1. Fase 1: Avvio del Server
Il server gestisce la comunicazione RMI e l'interazione con il database PostgreSQL.

1. Aprire un terminale o prompt dei comandi e navigare fino alla cartella "bin".


2. Eseguire il comando:
java -jar ServerTK.jar
3. Seguire le istruzioni a schermo inserendo i parametri richiesti per la connessione al database: Host, Porta, Nome Database, Username e Password.


4. Una volta visualizzato il messaggio di conferma e di attesa connessioni, lasciare la finestra del terminale aperta in background.



3.2. Fase 2: Avvio del Client (Interfaccia Grafica)
Dopo aver avviato correttamente il server, è possibile lanciare l'interfaccia utente.

1. Aprire la cartella "bin".


2. Eseguire l'applicazione con un semplice doppio clic sul file "ClientTK.jar".
3. In alternativa (o in caso di problemi con le associazioni di file del proprio sistema operativo), aprire una nuova finestra del terminale nella cartella "bin" ed eseguire il comando:
java -jar ClientTK.jar