/*
 * Nome: Alessandro
 * Cognome: Melnyk
 * Matricola:761001
 * Sede: VA
 *
 * Nome: Gianluca
 * Cognome: Melis
 * Matricola:761289
 *
 * Sede: VA
 * Nome: Simone
 * Cognome: Zamberletti
 * Matricola:761355
 * Sede: VA
 *
 * Nome: Davide
 * Cognome: Redemagni
 * Matricola:760043
 * Sede: VA
 */
package theknife.server;

import theknife.db.DatabaseConnection;
import theknife.remote.TheKnifeService;
import theknife.remote.TheKnifeServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ServerTK {
    private static final int RMI_PORT = 1099;
    private static final String SERVICE_NAME = "TheKnifeService";

    public static void main(String[] args) {
        System.out.println("          AVVIO SERVER THEKNIFE          ");


        Scanner scanner = new Scanner(System.in);

        System.out.print("Inserisci Host DB PostgreSQL [default: localhost]: ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) host = "localhost";

        System.out.print("Inserisci Porta DB [default: 5432]: ");
        String portStr = scanner.nextLine().trim();
        int port = portStr.isEmpty() ? 5432 : Integer.parseInt(portStr);

        System.out.print("Inserisci Nome Database [default: theknife]: ");
        String dbName = scanner.nextLine().trim();
        if (dbName.isEmpty()) dbName = "theknife";

        System.out.print("Inserisci Username DB [default: postgres]: ");
        String user = scanner.nextLine().trim();
        if (user.isEmpty()) user = "postgres";

        System.out.print("Inserisci Password DB: ");
        String password = scanner.nextLine().trim();

        System.out.println("\nVerifica connessione al database PostgreSQL...");
        DatabaseConnection.configure(host, port, dbName, user, password);

        if (!DatabaseConnection.testConnection()) {
            System.err.println("ERRORE: Impossibile stabilire la connessione a PostgreSQL.");
            System.err.println("Verificare che i parametri siano corretti e che il DBMS sia attivo.");
            System.exit(1);
        }

        System.out.println("Connessione al database riuscita!");

        try {
            System.out.println("Inizializzazione Registry RMI sulla porta " + RMI_PORT + "...");
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(RMI_PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(RMI_PORT);
            }

            TheKnifeService service = new TheKnifeServiceImpl();
            registry.rebind(SERVICE_NAME, service);

            System.out.println(" ServerTK avviato con successo!");
            System.out.println(" In attesa di richieste da clientTK...");

        } catch (Exception e) {
            System.err.println("ERRORE FATALE nell'avvio del server RMI: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}