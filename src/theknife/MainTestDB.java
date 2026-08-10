package theknife;

import theknife.dao.UtenteDAO;
import theknife.dao.UtenteDAOImpl;
import theknife.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class MainTestDB {

    public static void main(String[] args) {
        System.out.println("--- INIZIO TEST DATABASE ---");

        // TEST 1: Verifica della Connessione
        System.out.println("\n1. Test Connessione...");
        try {
            Connection conn = DatabaseConnection.getInstance();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ SUCCESSO: Connesso a PostgreSQL!");
            }
        } catch (SQLException e) {
            System.err.println("❌ ERRORE: Impossibile connettersi al DB. Controlla password e driver.");
            e.printStackTrace();
            return; // Se non si connette, fermiamo il test qui
        }

        // Inizializziamo il DAO
        UtenteDAO dao = new UtenteDAOImpl();

        // Dati finti per il test
        String testUsername = "mario.rossi";
        // Usiamo una stringa finta che simula una password cifrata come le vostre
        String testPasswordCifrata = "xYzPasswordCifrata123==";

        // TEST 2: Registrazione Utente
        System.out.println("\n2. Test Registrazione Utente...");
        Utente nuovoUtente = new UtenteRegistrato(
                "Mario",
                "Rossi",
                testUsername,
                testPasswordCifrata,
                LocalDate.of(1990, 5, 20),
                "Roma",
                Ruolo.CLIENTE
        );

        boolean registrato = dao.registraUtente(nuovoUtente);
        if (registrato) {
            System.out.println("✅ SUCCESSO: Utente inserito nel database!");
        } else {
            System.out.println("⚠️ ATTENZIONE: Utente non inserito. (Forse l'username esiste già?)");
        }

        // TEST 3: Login Utente
        System.out.println("\n3. Test Login Utente...");
        Utente utenteLoggato = dao.eseguiLogin(testUsername, testPasswordCifrata);

        if (utenteLoggato != null) {
            System.out.println("✅ SUCCESSO: Login effettuato correttamente!");
            System.out.println("Dati recuperati dal DB: " + utenteLoggato.nome + " " + utenteLoggato.cognome + " - Ruolo: " + utenteLoggato.ruolo);
        } else {
            System.err.println("❌ ERRORE: Login fallito. Credenziali non trovate.");
        }

        System.out.println("\n--- FINE TEST ---");
    }
}