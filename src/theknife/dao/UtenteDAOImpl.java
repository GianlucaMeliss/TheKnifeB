package theknife.dao;

import theknife.*;
import theknife.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;

public class UtenteDAOImpl implements UtenteDAO {

    @Override
    public Utente eseguiLogin(String username, String passwordCifrata) {
        String sql = "SELECT * FROM Utenti WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, passwordCifrata);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Recupera i dati
                int id = rs.getInt("id_utente");
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                LocalDate dataNasc = rs.getDate("data_nascita") != null ? rs.getDate("data_nascita").toLocalDate() : null;
                String domicilio = rs.getString("domicilio");
                Ruolo ruolo = Ruolo.valueOf(rs.getString("ruolo"));

                // Ricrea l'oggetto Java corretto in base al ruolo
                if (ruolo == Ruolo.CLIENTE) {
                    return new UtenteRegistrato(nome, cognome, username, passwordCifrata, dataNasc, domicilio, ruolo, id);
                } else {
                    return new Ristoratore(nome, cognome, username, passwordCifrata, dataNasc, domicilio, ruolo, id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Login fallito
    }

    @Override
    public boolean registraUtente(Utente utente) {
        String sql = "INSERT INTO Utenti (nome, cognome, username, password, data_nascita, domicilio, ruolo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, utente.nome);
            stmt.setString(2, utente.cognome);
            stmt.setString(3, utente.username);
            stmt.setString(4, utente.password); // Deve già essere cifrata da CifraturaUtils
            stmt.setDate(5, utente.dataNasc != null ? Date.valueOf(utente.dataNasc) : null);
            stmt.setString(6, utente.domicilio);
            stmt.setString(7, utente.ruolo.toString());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}