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
package theknife.dao;

import theknife.*;
import theknife.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;

/**
 * Implementazione dell'interfaccia {@link UtenteDAO} per la gestione degli utenti su database SQL.
 * Gestisce l'autenticazione e la creazione di nuovi profili utente.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public class UtenteDAOImpl implements UtenteDAO {

    /**
     * Esegue il login di un utente interrogando la tabella 'Utenti'.
     * In base al ruolo memorizzato nel database, restituisce un'istanza di {@link UtenteRegistrato} o {@link Ristoratore}.
     *
     * @param username lo username dell'utente
     * @param passwordCifrata la password già cifrata tramite AES
     * @return l'oggetto {@link Utente} corrispondente, null se le credenziali sono errate o in caso di errore SQL
     */
    @Override
    public Utente eseguiLogin(String username, String passwordCifrata) {
        String sql = "SELECT * FROM Utenti WHERE username = ? AND password = ?";
        // ... (rest of the code remains the same)

        try (Connection conn = DatabaseConnection.getConnection();
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

    /**
     * Inserisce un nuovo record nella tabella 'Utenti' con i dati forniti.
     *
     * @param utente l'oggetto {@link Utente} contenente i dati da persistere
     * @return true se l'inserimento è avvenuto correttamente, false altrimenti
     */
    @Override
    public boolean registraUtente(Utente utente) {
        String sql = "INSERT INTO Utenti (nome, cognome, username, password, data_nascita, domicilio, ruolo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Usiamo i GETTER invece di accedere direttamente alle variabili!
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getCognome());
            stmt.setString(3, utente.getUsername());
            stmt.setString(4, utente.getPassword());
            stmt.setDate(5, utente.getDataNasc() != null ? Date.valueOf(utente.getDataNasc()) : null);
            stmt.setString(6, utente.getDomicilio());
            stmt.setString(7, utente.getRuolo().toString());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}