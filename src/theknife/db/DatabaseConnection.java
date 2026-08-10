package theknife.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/theknife";
    private static final String USER = "postgres";
    private static final String PASSWORD = "inserisci_qui_la_tua_password"; // Metti la pass dell'installazione!

    private static Connection connection = null;

    // Metodo per ottenere la connessione
    public static Connection getInstance() throws SQLException {
        // Se la connessione non c'è ancora o è chiusa, la creiamo
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                // Apre effettivamente la connessione
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connessione al DB riuscita!");
            } catch (ClassNotFoundException e) {
                System.out.println("Errore: Driver JDBC non trovato. Hai aggiunto il .jar al progetto?");
                e.printStackTrace();
            }
        }
        return connection;
    }
}