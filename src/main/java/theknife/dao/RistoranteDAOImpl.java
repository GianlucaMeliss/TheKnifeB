/*
 * Nome: Alessandro
 * Cognome: Melnyk
 * Matricola:761001
 * Sede: VA
 *
 * Nome: Gianluca
 * Cognome: Melis
 * Matricola:761289
 * Sede: VA
 *
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
/*
 * Nome: Alessandro
 * Cognome: Melnyk
 * Matricola:761001
 * Sede: VA
 *
 * Nome: Gianluca
 * Cognome: Melis
 * Matricola:761289
 * Sede: VA
 *
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

import theknife.Ristorante;
import theknife.TipoCucina;
import theknife.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RistoranteDAOImpl implements RistoranteDAO {

    private Ristorante mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_ristorante");
        String nome = rs.getString("nome");
        String indirizzo = rs.getString("indirizzo");
        String citta = rs.getString("citta");
        String nazione = rs.getString("nazione");
        double lat = rs.getDouble("latitudine");
        double lon = rs.getDouble("longitudine");
        float prezzo = rs.getFloat("prezzo_medio");
        boolean delivery = rs.getBoolean("delivery");
        boolean online = rs.getBoolean("prenotazione_online");

        String cucinaStr = rs.getString("tipo_cucina");
        ArrayList<TipoCucina> cucine = new ArrayList<>();
        if (cucinaStr != null && !cucinaStr.isBlank()) {
            for (String c : cucinaStr.split(",")) {
                TipoCucina tc = TipoCucina.fromString(c.trim());
                if (tc != null) cucine.add(tc);
            }
        }

        Ristorante r = new Ristorante(nome, indirizzo, citta, nazione, lat, lon, prezzo, cucine);
        r.idRistorante = id;
        r.consegna = delivery;
        r.pren_online = online;
        return r;
    }

    @Override
    public ArrayList<Ristorante> getAllRistoranti() {
        ArrayList<Ristorante> list = new ArrayList<>();
        String sql = "SELECT * FROM RistorantiTheKnife ORDER BY nome ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Ristorante getRistoranteById(int id) {
        String sql = "SELECT * FROM RistorantiTheKnife WHERE id_ristorante = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Ristorante> cercaAvanzata(String citta, String nome, String tipoCucina, Float pMin, Float pMax, boolean delivery, boolean online, Double ratingMin) {
        ArrayList<Ristorante> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.* FROM RistorantiTheKnife r " +
                        "LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante AND rec.id_padre = -1 " +
                        "WHERE 1=1 "
        );

        if (citta != null && !citta.isBlank()) sql.append("AND LOWER(r.citta) = LOWER(?) ");
        if (nome != null && !nome.isBlank()) sql.append("AND LOWER(r.nome) LIKE LOWER(?) ");
        if (tipoCucina != null && !tipoCucina.isBlank()) sql.append("AND r.tipo_cucina ILIKE ? ");
        if (pMin != null) sql.append("AND r.prezzo_medio >= ? ");
        if (pMax != null) sql.append("AND r.prezzo_medio <= ? ");
        if (delivery) sql.append("AND r.delivery = TRUE ");
        if (online) sql.append("AND r.prenotazione_online = TRUE ");

        sql.append("GROUP BY r.id_ristorante ");
        if (ratingMin != null && ratingMin > 0) {
            sql.append("HAVING COALESCE(AVG(rec.voto), 0) >= ? ");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (citta != null && !citta.isBlank()) ps.setString(idx++, citta.trim());
            if (nome != null && !nome.isBlank()) ps.setString(idx++, "%" + nome.trim() + "%");
            if (tipoCucina != null && !tipoCucina.isBlank()) ps.setString(idx++, "%" + tipoCucina.trim() + "%");
            if (pMin != null) ps.setFloat(idx++, pMin);
            if (pMax != null) ps.setFloat(idx++, pMax);
            if (ratingMin != null && ratingMin > 0) ps.setDouble(idx++, ratingMin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean aggiungiRistorante(Ristorante r, int idRistoratore) {
        String sql = "INSERT INTO RistorantiTheKnife (nome, indirizzo, citta, nazione, latitudine, longitudine, prezzo_medio, delivery, prenotazione_online, tipo_cucina, id_ristoratore) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.nome);
            ps.setString(2, r.indirizzo);
            ps.setString(3, r.citta);
            ps.setString(4, r.nazione);
            ps.setDouble(5, r.latitudine != null ? r.latitudine : 0.0);
            ps.setDouble(6, r.longitudine != null ? r.longitudine : 0.0);
            ps.setFloat(7, r.prezzo);
            ps.setBoolean(8, r.consegna);
            ps.setBoolean(9, r.pren_online);
            String cucineStr = r.tipoCucina.stream().map(Enum::name).collect(Collectors.joining(","));
            ps.setString(10, cucineStr);
            ps.setInt(11, idRistoratore);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Ristorante> getRistorantiGestiti(int idRistoratore) {
        ArrayList<Ristorante> list = new ArrayList<>();
        String sql = "SELECT * FROM RistorantiTheKnife WHERE id_ristoratore = ? ORDER BY id_ristorante ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRistoratore);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean aggiungiPreferito(int idUtente, int idRistorante) {
        String sql = "INSERT INTO Preferiti (id_utente, id_ristorante) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idRistorante);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean rimuoviPreferito(int idUtente, int idRistorante) {
        String sql = "DELETE FROM Preferiti WHERE id_utente = ? AND id_ristorante = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idRistorante);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Ristorante> getPreferitiUtente(int idUtente) {
        ArrayList<Ristorante> list = new ArrayList<>();
        String sql = "SELECT r.* FROM RistorantiTheKnife r INNER JOIN Preferiti p ON r.id_ristorante = p.id_ristorante WHERE p.id_utente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}