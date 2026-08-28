package theknife.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static String host = null;
    private static int port = 0;
    private static String dbName = null;
    private static String user = null;
    private static String password = null;
    private static boolean configured = false;

    public static synchronized void configure(String dbHost, int dbPort, String database, String dbUser, String dbPass) {
        host = dbHost;
        port = dbPort;
        dbName = database;
        user = dbUser;
        password = dbPass;
        configured = true;
    }

    public static boolean testConnection() {
        if (!configured) return false;
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

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