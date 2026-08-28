package theknife.dao;

import theknife.Ristorante;
import theknife.TipoCucina;
import theknife.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class RistoranteDAOImpl implements RistoranteDAO {

    // 1. IL METODO TRADUTTORE (Da Numero a Enum)
    private TipoCucina traduciIdInEnum(int idCucina) {
        // Solitamente nel DB gli ID partono da 1, mentre in Java gli Enum partono da 0.
        // Se nel vostro DB l'id 1 corrisponde al primo tipo di cucina, facciamo -1.
        TipoCucina[] valori = TipoCucina.values();
        if (idCucina > 0 && idCucina <= valori.length) {
            return valori[idCucina - 1];
        }
        return null;
    }

    // 2. IL METODO CHE INTERROGA LA TABELLA RELAZIONALE
    // Modifica: ora riceve la Connection come parametro per non aprirne una nuova ogni volta
    private ArrayList<TipoCucina> getCucinePerRistorante(int idRistorante, Connection conn) {
        ArrayList<TipoCucina> cucine = new ArrayList<>();
        String sql = "SELECT fk_id_tipo_cucina FROM ristorante_cucina WHERE fk_id_ristorante = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRistorante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idCucina = rs.getInt("fk_id_tipo_cucina");
                    TipoCucina tc = traduciIdInEnum(idCucina);
                    if (tc != null) cucine.add(tc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cucine;
    }

    // Modifica: ora riceve la Connection come parametro per passarla a getCucinePerRistorante
    private Ristorante mapRow(ResultSet rs, Connection conn) throws SQLException {
        int id = rs.getInt("id_ristorante");
        String nome = rs.getString("nome");
        String indirizzo = rs.getString("indirizzo");
        String citta = rs.getString("citta");
        String nazione = rs.getString("nazione");
        double lat = rs.getDouble("latitudine");
        double lon = rs.getDouble("longitudine");

        float prezzo = rs.getFloat("prezzo");
        boolean delivery = rs.getBoolean("consegna");
        boolean online = rs.getBoolean("pren_online");

        // 3. UNIAMO I DATI PRIMA DELLA CREAZIONE DELL'OGGETTO (L'idea di Melis!)
        ArrayList<TipoCucina> cucine = getCucinePerRistorante(id, conn);

        Ristorante r = new Ristorante(nome, indirizzo, citta, nazione, lat, lon, prezzo, cucine);
        r.idRistorante = id;
        r.consegna = delivery;
        r.pren_online = online;
        return r;
    }

    @Override
    public ArrayList<Ristorante> getAllRistoranti() {
        ArrayList<Ristorante> list = new ArrayList<>();
        String sql = "SELECT * FROM ristorantitheknife ORDER BY nome ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs, conn));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Ristorante getRistoranteById(int id) {
        String sql = "SELECT * FROM ristorantitheknife WHERE id_ristorante = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs, conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Ristorante> cercaAvanzata(String citta, Double lat, Double lon, String nome, String tipoCucina, Float pMin, Float pMax, boolean delivery, boolean online, Double ratingMin) {
        ArrayList<Ristorante> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT r.* FROM ristorantitheknife r " +
                        "LEFT JOIN recensioni rec ON r.id_ristorante = rec.fk_id_ristorante AND rec.id_recensione_padre IS NULL " +
                        "WHERE 1=1 "
        );

        // --- BIVIO LOGICO: Coordinate o Città ---
        if (lat != null && lon != null) {
            // Formula di Haversine in SQL: raggio di 20 km
            sql.append("AND (6371 * acos(least(greatest(cos(radians(?)) * cos(radians(r.latitudine)) * cos(radians(r.longitudine) - radians(?)) + sin(radians(?)) * sin(radians(r.latitudine)), -1.0), 1.0))) <= 20 ");
        } else if (citta != null && !citta.isBlank()) {
            sql.append("AND r.citta ILIKE ? ");
        }

        if (nome != null && !nome.isBlank()) sql.append("AND LOWER(r.nome) LIKE LOWER(?) ");

        if (tipoCucina != null && !tipoCucina.isBlank()) {
            sql.append("AND r.id_ristorante IN (SELECT fk_id_ristorante FROM ristorante_cucina WHERE fk_id_tipo_cucina = ?) ");
        }

        if (pMin != null) sql.append("AND r.prezzo >= ? ");
        if (pMax != null) sql.append("AND r.prezzo <= ? ");
        if (delivery) sql.append("AND r.consegna = TRUE ");
        if (online) sql.append("AND r.pren_online = TRUE ");

        sql.append("GROUP BY r.id_ristorante ");
        if (ratingMin != null && ratingMin > 0) {
            sql.append("HAVING COALESCE(AVG(rec.voto), 0) >= ? ");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;

            // Iniezione dei parametri per il bivio logico
            if (lat != null && lon != null) {
                ps.setDouble(idx++, lat);
                ps.setDouble(idx++, lon);
                ps.setDouble(idx++, lat);
            } else if (citta != null && !citta.isBlank()) {
                ps.setString(idx++, "%" + citta.trim() + "%");
            }

            if (nome != null && !nome.isBlank()) ps.setString(idx++, "%" + nome.trim() + "%");

            if (tipoCucina != null && !tipoCucina.isBlank()) {
                TipoCucina tcEnum = TipoCucina.fromString(tipoCucina.trim());
                if (tcEnum != null) {
                    ps.setInt(idx++, tcEnum.ordinal() + 1);
                } else {
                    ps.setInt(idx++, -1);
                }
            }

            if (pMin != null) ps.setFloat(idx++, pMin);
            if (pMax != null) ps.setFloat(idx++, pMax);
            if (ratingMin != null && ratingMin > 0) ps.setDouble(idx++, ratingMin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs, conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean aggiungiRistorante(Ristorante r, int idRistoratore) {
        String sql = "INSERT INTO ristorantitheknife (nome, indirizzo, citta, nazione, latitudine, longitudine, prezzo, consegna, pren_online, id_ristoratore) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.nome);
            ps.setString(2, r.indirizzo);
            ps.setString(3, r.citta);
            ps.setString(4, r.nazione);
            ps.setDouble(5, r.latitudine != null ? r.latitudine : 0.0);
            ps.setDouble(6, r.longitudine != null ? r.longitudine : 0.0);
            ps.setFloat(7, r.prezzo);
            ps.setBoolean(8, r.consegna);
            ps.setBoolean(9, r.pren_online);
            ps.setInt(10, idRistoratore);

            // 5. SE INSERISCE CON SUCCESSO IL RISTORANTE, DEVE SALVARE ANCHE LE CUCINE (Relazioni)
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int nuovoIdRistorante = generatedKeys.getInt(1);
                        salvaCucineRistorante(nuovoIdRistorante, r.tipoCucina, conn);
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metodo di supporto per salvare le cucine quando si crea un nuovo ristorante
    // Modifica: ora riceve la Connection come parametro
    private void salvaCucineRistorante(int idRistorante, ArrayList<TipoCucina> cucine, Connection conn) {
        if (cucine == null || cucine.isEmpty()) return;
        String sql = "INSERT INTO ristorante_cucina (fk_id_ristorante, fk_id_tipo_cucina) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (TipoCucina tc : cucine) {
                ps.setInt(1, idRistorante);
                ps.setInt(2, tc.ordinal() + 1);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Ristorante> getRistorantiGestiti(int idRistoratore) {
        ArrayList<Ristorante> list = new ArrayList<>();
        String sql = "SELECT * FROM ristorantitheknife WHERE id_ristoratore = ? ORDER BY id_ristorante ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRistoratore);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs, conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean aggiungiPreferito(int idUtente, int idRistorante) {
        String sql = "INSERT INTO preferiti (fk_id_utente, fk_id_ristorante) VALUES (?, ?) ON CONFLICT DO NOTHING";
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
        String sql = "DELETE FROM preferiti WHERE fk_id_utente = ? AND fk_id_ristorante = ?";
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
        String sql = "SELECT r.* FROM ristorantitheknife r INNER JOIN preferiti p ON r.id_ristorante = p.fk_id_ristorante WHERE p.fk_id_utente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs, conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}