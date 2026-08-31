Progetto TheKnife - Architettura Client/Server

1). Prerequisiti Software

Prima di procedere, è necessario assicurarsi che sul sistema sia installato il seguente software:

Java Development Kit (JDK): È richiesta una versione 21 o superiore. Si consiglia di scaricare una distribuzione standard come Eclipse Temurin (Adoptium).

PostgreSQL: È richiesta un'istanza attiva del database relazionale. Il database deve contenere lo schema e le tabelle necessarie (ristoranti, utenti, recensioni, ecc.) importabili tramite lo script fornito nel progetto.

Nota: A differenza delle versioni precedenti, le librerie JavaFX sono ora integrate direttamente all'interno dell'eseguibile del Client grazie all'utilizzo di Maven. Non è più necessario scaricare SDK esterni o configurare percorsi complessi.

2). Compilazione del Progetto (tramite Maven)

Il progetto utilizza Maven per la gestione delle dipendenze e l'automazione della build.

Aprire un terminale o prompt dei comandi nella cartella principale del progetto (dove si trova il file pom.xml).

Eseguire il seguente comando:
mvn clean package

Al termine dell'operazione, i file eseguibili "ServerTK.jar" e "ClientTK.jar" verranno generati automaticamente e posizionati direttamente all'interno della cartella "bin", pronti per l'esecuzione.

3). Esecuzione dell'Applicazione

L'applicazione è basata su un'architettura distribuita e necessita dell'avvio separato del modulo server e del modulo client. Entrambi gli eseguibili si trovano nella cartella "bin".

3.1. Fase 1: Avvio del Server
Il server gestisce la comunicazione RMI e l'interazione con il database PostgreSQL.

Aprire un terminale o prompt dei comandi e navigare fino alla cartella "bin".

Eseguire il comando:
java -jar ServerTK.jar

Seguire le istruzioni a schermo inserendo i parametri richiesti per la connessione al database: Host, Porta, Nome Database, Username e Password.

Una volta visualizzato il messaggio di conferma e di attesa connessioni, lasciare la finestra del terminale aperta in background.

3.2. Fase 2: Avvio del Client (Interfaccia Grafica)
Dopo aver avviato correttamente il server, è possibile lanciare l'interfaccia utente.

Aprire la cartella "bin".

Eseguire l'applicazione con un semplice doppio clic sul file "ClientTK.jar".

In alternativa (o in caso di problemi con le associazioni di file del proprio sistema operativo), aprire una nuova finestra del terminale nella cartella "bin" ed eseguire il comando:
java -jar ClientTK.jar