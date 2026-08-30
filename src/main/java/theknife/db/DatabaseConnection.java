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
package theknife.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestore della connessione al database SQL (PostgreSQL).
 * Fornisce i metodi per configurare e ottenere istanze di {@link Connection} utilizzate dai DAO.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public class DatabaseConnection {
    private static String host = null;
    private static int port = 0;
    private static String dbName = null;
    private static String user = null;
    private static String password = null;
    private static boolean configured = false;

    /**
     * Configura i parametri di connessione al database.
     * 
     * @param dbHost indirizzo host del database
     * @param dbPort porta del servizio database
     * @param database nome del database
     * @param dbUser username per l'accesso
     * @param dbPass password per l'accesso
     */
    public static synchronized void configure(String dbHost, int dbPort, String database, String dbUser, String dbPass) {
        host = dbHost;
        port = dbPort;
        dbName = database;
        user = dbUser;
        password = dbPass;
        configured = true;
    }

    /**
     * Verifica se la configurazione attuale permette di stabilire una connessione.
     * 
     * @return true se il test di connessione ha successo
     */
    public static boolean testConnection() {
        if (!configured) return false;
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Restituisce una nuova connessione al database utilizzando i parametri configurati.
     * 
     * @return un oggetto {@link Connection} attivo
     * @throws SQLException in caso di mancata configurazione o errore di connessione
     */
    public static Connection getConnection() throws SQLException {
        if (!configured) {
            throw new SQLException("DatabaseConnection non configurato. Avviare prima ServerTK per inserire le credenziali.");
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trovato nel classpath.", e);
        }
        String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
        return DriverManager.getConnection(url, user, password);
    }
}