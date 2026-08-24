package theknife.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static String host = "localhost";
    private static int port = 5432;
    private static String dbName = "theknife";
    private static String user = "postgres";
    private static String password = "postgres";

    public static void configure(String dbHost, int dbPort, String database, String dbUser, String dbPass) {
        host = dbHost;
        port = dbPort;
        dbName = database;
        user = dbUser;
        password = dbPass;
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trovato nel classpath.", e);
        }
        String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
        return DriverManager.getConnection(url, user, password);
    }
}