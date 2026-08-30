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

import theknife.Recensione;
import theknife.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

/**
 * Implementazione dell'interfaccia {@link RecensioneDAO} per la persistenza su database SQL.
 * Gestisce il ciclo di vita delle recensioni e delle risposte dei ristoratori.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public class RecensioneDAOImpl implements RecensioneDAO {

    /**
     * Aggiunge una nuova recensione al database.
     * Se la recensione è una risposta di un ristoratore, verifica che non ne esista già una.
     * 
     * @param r l'oggetto recensione da inserire
     * @return true se l'inserimento ha successo, false altrimenti
     */
    @Override
    public boolean aggiungiRecensione(Recensione r) {
        int idPadre = (r.idRecensionePadre != null) ? r.idRecensionePadre : -1;

        // Se è una risposta, verifichiamo atomicamente che non ne esista già un'altra
        if (idPadre != -1) {
            String checkSql = "SELECT COUNT(*) FROM Recensioni WHERE id_recensione_padre = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, idPadre);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.err.println("Violazione vincolo: esiste già una risposta per la recensione " + idPadre);
                        return false;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        String sql = "INSERT INTO Recensioni (fk_id_ristorante, fk_id_utente, id_recensione_padre, voto, commento, data) VALUES (?, ?, ?, ?, ?, ?)";        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.fkIdRistorante);
            ps.setInt(2, r.fkIdUtente);
            if (idPadre == -1) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, idPadre);
            }
            if (r.voto == -1) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, r.voto);
            }
            ps.setString(5, r.commento);
            ps.setDate(6, r.data != null ? Date.valueOf(r.data) : Date.valueOf(java.time.LocalDate.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Modifica una recensione esistente nel database.
     * 
     * @param idRecensione l'identificativo della recensione da modificare
     * @param r l'oggetto recensione con i nuovi dati
     * @return true se l'operazione ha successo, false altrimenti
     */
    @Override
    public boolean modificaRecensione(int idRecensione, Recensione r) {
        String sql = "UPDATE Recensioni SET voto = ?, commento = ?, data = ? WHERE id_recensione = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.voto);
            ps.setString(2, r.commento);
            ps.setDate(3, r.data != null ? Date.valueOf(r.data) : Date.valueOf(java.time.LocalDate.now()));
            ps.setInt(4, idRecensione);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una recensione dal database.
     * L'operazione è transazionale: elimina prima tutte le risposte associate.
     * 
     * @param idRecensione l'identificativo della recensione da eliminare
     * @return true se l'operazione ha successo, false altrimenti
     */
    @Override
    public boolean eliminaRecensione(int idRecensione) {
        String sqlReplies = "DELETE FROM Recensioni WHERE id_recensione_padre = ?";
        String sqlMain = "DELETE FROM Recensioni WHERE id_recensione = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlReplies);
                 PreparedStatement ps2 = conn.prepareStatement(sqlMain)) {
                ps1.setInt(1, idRecensione);
                ps1.executeUpdate();

                ps2.setInt(1, idRecensione);
                int affected = ps2.executeUpdate();
                conn.commit();
                return affected > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera tutte le recensioni di un determinato ristorante.
     * Include lo username dell'autore tramite JOIN con la tabella Utenti.
     * 
     * @param idRistorante l'identificativo del ristorante
     * @return una lista di oggetti recensione
     */
    @Override
    public ArrayList<Recensione> getRecensioniByRistorante(int idRistorante) {
        ArrayList<Recensione> list = new ArrayList<>();
        String sql = "SELECT rec.*, u.username FROM Recensioni rec LEFT JOIN Utenti u ON rec.fk_id_utente = u.id_utente WHERE rec.fk_id_ristorante = ? ORDER BY rec.data DESC, rec.id_recensione ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPadreLetto = rs.getInt("id_recensione_padre");
                    Integer idPadreDaPassare = rs.wasNull() ? -1 : idPadreLetto;

                    int votoLetto = rs.getInt("voto");
                    int votoDaPassare = rs.wasNull() ? -1 : votoLetto;

                    Recensione r = new Recensione(
                            rs.getInt("fk_id_ristorante"),
                            rs.getInt("fk_id_utente"),
                            votoDaPassare, // <-- Usiamo la variabile calcolata per il voto!
                            rs.getString("commento"),
                            rs.getDate("data") != null ? rs.getDate("data").toLocalDate() : null,
                            rs.getInt("id_recensione"),
                            idPadreDaPassare
                    );
                    r.authorUsername = rs.getString("username");
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Recupera le recensioni scritte da un utente specifico.
     * Include il nome del ristorante tramite JOIN.
     * 
     * @param idUtente l'identificativo dell'utente
     * @return una lista di oggetti recensione
     */
    @Override
    public ArrayList<Recensione> getRecensioniByUtente(int idUtente) {
        ArrayList<Recensione> list = new ArrayList<>();
        String sql = "SELECT rec.*, r.nome AS rest_nome FROM Recensioni rec LEFT JOIN RistorantiTheKnife r ON rec.fk_id_ristorante = r.id_ristorante WHERE rec.fk_id_utente = ? AND rec.id_recensione_padre IS NULL ORDER BY rec.data DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPadreLetto = rs.getInt("id_recensione_padre");
                    Integer idPadreDaPassare = rs.wasNull() ? -1 : idPadreLetto;

                    Recensione r = new Recensione(
                            rs.getInt("fk_id_ristorante"),
                            rs.getInt("fk_id_utente"),
                            rs.getInt("voto"),
                            rs.getString("commento"),
                            rs.getDate("data") != null ? rs.getDate("data").toLocalDate() : null,
                            rs.getInt("id_recensione"),
                            idPadreDaPassare // <-- Ora passiamo la variabile calcolata qui
                    );
                    r.restaurantName = rs.getString("rest_nome");
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Calcola le statistiche (media voti e numero totale) per un ristorante.
     * 
     * @param idRistorante l'identificativo del ristorante
     * @return un array con [media, totale]
     */
    @Override
    public double[] getStatisticheRistorante(int idRistorante) {
        String sql = "SELECT AVG(voto) AS media, COUNT(*) AS totale FROM Recensioni WHERE fk_id_ristorante = ? AND id_recensione_padre IS NULL";        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new double[]{rs.getDouble("media"), rs.getDouble("totale")};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new double[]{0.0, 0.0};
    }
}