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
package theknife;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * Classe che rappresenta una recensione su un ristorante.
 * Una recensione può essere una recensione principale (scritta da un utente)
 * o una risposta (scritta da un ristoratore, con riferimento a una recensione padre).
 * Contiene informazioni sull'autore, sul ristorante recensito, sul voto assegnato,
 * sul commento e sulla data.
 *
 * @author Gianluca Melis
 * @author Davide Redemagni
 * @author Simone Zamberletti
 */
public class Recensione {
    /** Identificatore univoco della recensione. */
    Integer idRecensione;

    /** ID del ristorante recensito. */
    Integer fkIdRistorante = -1;

    /** ID dell’utente che ha scritto la recensione. */
    Integer fkIdUtente;

    /**
     * ID della recensione a cui si sta rispondendo, se presente.
     * Se -1, indica che è una recensione principale scritta da un cliente.
     */
    Integer idRecensionePadre = -1;

    /** Voto assegnato al ristorante (da 1 a 5 tipicamente). */
    int voto = -1;

    /** Commento testuale lasciato dall’utente. */
    String commento;

    /** Data in cui la recensione è stata scritta. */
    LocalDate data;

    /** NUOVA RIGA: Aggiungiamo un campo temporaneo per memorizzare l'username dell'autore per la UI. 
     * 'transient' dice a Gson di ignorarlo durante il salvataggio su file. */
    transient String authorUsername;

    /**
     * Costruttore completo della recensione.
     * @param fkIdRistorante ID del ristorante recensito
     * @param fkIdUtente ID dell’utente che ha scritto la recensione
     * @param voto Voto assegnato
     * @param commento Commento testuale
     * @param data Data della recensione
     * @param idRecensione ID della recensione
     * @param idRecensionePadre ID della recensione padre (se è una risposta), -1 altrimenti
     */
    public Recensione(Integer fkIdRistorante, Integer fkIdUtente, int voto, String commento, LocalDate data,
                      Integer idRecensione, Integer idRecensionePadre ) {
        this.fkIdRistorante = fkIdRistorante;
        this.fkIdUtente = fkIdUtente;
        this.voto = voto;
        this.commento = commento;
        this.data = data;
        this.idRecensione = idRecensione;
        this.idRecensionePadre = idRecensionePadre;
    }

    /**
     * Costruttore per recensioni principali (senza risposta).
     * @param fkIdRistorante ID del ristorante recensito
     * @param fkIdUtente ID dell’utente
     * @param voto Voto assegnato
     * @param commento Commento testuale
     * @param data Data della recensione
     */
    public Recensione(Integer fkIdRistorante, Integer fkIdUtente, int voto, String commento, LocalDate data) {
        this(fkIdRistorante,fkIdUtente,voto,commento,data,null,-1);
    }

    /**
     * Costruttore per recensioni risposta con solo commento e ID della recensione padre.
     * @param idRecensione ID della recensione a cui si sta rispondendo
     * @param fkIdRistorante ID del ristorante
     * @param commento Commento della risposta
     */
    public Recensione(Integer idRecensione, Integer fkIdRistorante,String commento) {
        this(fkIdRistorante,-1,-1,commento,LocalDate.now(),-1,idRecensione);
    }

    /**
     * Restituisce una rappresentazione testuale semplificata della recensione.
     * @return stringa con commento, utente e ID recensione
     */
    public String toString() {
        // --- INIZIO BLOCCO MODIFICATO ---
        if (voto == -1) {
            return "Risposta: \"" + commento + "\"";
        }

        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stars.append(i < voto ? '★' : '☆');
        }

        String author = (authorUsername != null && !authorUsername.isEmpty()) ? " (" + authorUsername + ")" : "";
        return stars.toString() + author + ": \"" + commento + "\"";
        // --- FINE BLOCCO MODIFICATO ---
    }


    /**
     * Deserializzatore personalizzato per la classe {@link Recensione},
     * utilizzato per convertire un oggetto JSON in un'istanza di Recensione.
     */
    public static class RecensioneDeserializer implements JsonDeserializer<Recensione> {

        /**
         * Effettua il parsing di un oggetto JSON per costruire una {@link Recensione}.
         * @param json Oggetto JSON da deserializzare
         * @param typeOfT Tipo della destinazione
         * @param context Contesto di deserializzazione
         * @return Oggetto {@link Recensione} costruito a partire dal JSON
         * @throws JsonParseException in caso di errore di parsing
         */
        @Override
        public Recensione deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            Integer idRecensione = obj.get("idRecensione").getAsInt();
            Integer fkIdRistorante = obj.has("fkIdRistorante") ? obj.get("fkIdRistorante").getAsInt() : -1;
            Integer fkIdUtente = obj.get("fkIdUtente").getAsInt();
            Integer idRecensionePadre = obj.has("idRecensionePadre") ? obj.get("idRecensionePadre").getAsInt() : -1;
            int voto = obj.get("voto").getAsInt();
            String commento = obj.get("commento").getAsString();
            LocalDate data = LocalDate.parse(obj.get("data").getAsString());

            return new Recensione(fkIdRistorante, fkIdUtente, voto, commento, data, idRecensione, idRecensionePadre);
        }
    }
}