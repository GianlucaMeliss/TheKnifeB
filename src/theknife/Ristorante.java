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
import java.util.ArrayList;

/**
 * Classe che rappresenta un ristorante.
 * Contiene informazioni anagrafiche e funzionali come nome, indirizzo, città, nazione,
 * tipi di cucina, possibilità di consegna o prenotazione online, prezzo medio e ID univoco.
 * Supporta anche la serializzazione e deserializzazione da/verso JSON.
 * @author Gianluca Melis
 * @author Davide Redemagni
 */
public class Ristorante {
    // <editor-fold desc="Attributi">
    /** Nome del ristorante. */
    String nome;

    /** Indirizzo fisico del ristorante. */
    String indirizzo;

    /** Città in cui si trova il ristorante. */
    String citta;

    /** Nazione in cui si trova il ristorante. */
    String nazione;

    /** Prezzo medio indicativo del ristorante. */
    Float prezzo;

    /** Flag che indica se il ristorante offre servizio di consegna. */
    boolean consegna;

    /** Flag che indica se è possibile prenotare online. */
    boolean pren_online;

    /** Elenco dei tipi di cucina offerti. */
    ArrayList<TipoCucina> tipoCucina;

    /** Identificatore univoco del ristorante. */
    int idRistorante;
    // </editor-fold>

    // <editor-fold desc="Costruttori">
    /**
     * Costruttore principale per creare un nuovo ristorante.
     * @param Nome Nome del ristorante
     * @param Indirizzo Indirizzo del ristorante
     * @param Citta Città in cui si trova il ristorante
     * @param Nazione Nazione in cui si trova il ristorante
     * @param Prezzo Prezzo medio
     * @param TipoCucina Elenco dei tipi di cucina offerti
     * @throws IllegalArgumentException se i dati inseriti non rispettano le regole di validazione
     */
    public Ristorante(String Nome, String Indirizzo, String Citta, String Nazione, Float Prezzo, ArrayList<TipoCucina> TipoCucina ) {
        this.nome = Nome;
        this.indirizzo = Indirizzo;
        this.citta = Citta;
        this.nazione = Nazione;
        this.prezzo = Prezzo;
        this.consegna = false;          //imprementare metodo nella creazione
        this.pren_online = false;       //imprementare metodo nella creazione
        this.tipoCucina = TipoCucina;

        if (!isCittaValida(citta) || !isCittaValida(nazione)) {
            //System.out.println("Città o nazione non valida: " + citta + ", " + nazione);
            if (citta == null || citta.isBlank()) citta = "Sconosciuta";
            if (nazione == null || nazione.isBlank()) nazione = "Sconosciuta";
            //throw new IllegalArgumentException("Città e nazione non valide");
        }
        if (!isPrezzoValido(prezzo)) {
            throw new IllegalArgumentException("Prezzo non valido.");
        }
        if (Nome == null) {
            throw new IllegalArgumentException("Nome non valido.");
        }
        if (Indirizzo == null) {
            throw new IllegalArgumentException("Indirizzo non valido.");
        }
        if (TipoCucina == null) {
            throw new IllegalArgumentException("Tipo cucina non valido.");
        }
    }
    // </editor-fold>

    // <editor-fold desc="Metodi">
    /**
     * Verifica se il nome della città o nazione è valido.
     * Accetta lettere Unicode, spazi, apostrofi, trattini e punti.
     * @param citta Città o nazione da validare
     * @return true se il nome è valido, false altrimenti
     */
    static boolean isCittaValida(String citta) {
        if (citta == null) return false;
        return citta.matches("^[\\p{L}\\s'\\-/\\.]+$"); //qualsiasi lettera Unicode (latina, accentata, ecc.)(\\p{L}) spazi e trattini
    }

    /**
     * Verifica se il prezzo è valido (deve essere maggiore o uguale a zero).
     * @param prezzo Prezzo da validare
     * @return true se valido, false altrimenti
     */
    static boolean isPrezzoValido(Float prezzo) {
        return prezzo >= 0;
    }

    /**
     * Verifica l'uguaglianza tra due ristoranti confrontando nome, indirizzo, città e nazione (case insensitive).
     * @param obj Oggetto da confrontare
     * @return true se i ristoranti sono uguali, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Ristorante other = (Ristorante) obj;

        return nome.equalsIgnoreCase(other.nome)
                && indirizzo.equalsIgnoreCase(other.indirizzo)
                && citta.equalsIgnoreCase(other.citta)
                && nazione.equalsIgnoreCase(other.nazione);
    }

    /**
     * Restituisce una rappresentazione testuale del ristorante,
     * includendo città, nazione, prezzo e servizi disponibili.
     * @return stringa descrittiva del ristorante
     */
    @Override
    public String toString() {
        return nome+" in "+citta+"("+nazione+")\tprezzo:"+prezzo+"€\n"+"Offre:\n-Prenotazione online=>"
                +(pren_online?"sì":"no")+"\n-Delivery=>"+(consegna?"sì":"no")+"\n\n";
    }
    // </editor-fold>

    /**
     * Serializza l'oggetto {@code Ristorante} in una stringa JSON.
     * @return rappresentazione JSON del ristorante
     */
    public String toJson() {
        StringBuilder cucineBuilder = new StringBuilder();

        for (int i = 0; i < tipoCucina.size(); i++) {
            cucineBuilder.append(tipoCucina.get(i).toString());
            if (i < tipoCucina.size() - 1) {
                cucineBuilder.append(", ");
            }
        }

        String json =
                "  {\n"
                        + "    \"nome\": \"" + nome + "\",\n"
                        + "    \"indirizzo\": \"" + indirizzo + "\",\n"
                        + "    \"prezzo\": " + prezzo + ",\n"
                        + "    \"tipoCucina\": \"" + cucineBuilder.toString() + "\",\n"
                        + "    \"consegna\": " + consegna + ",\n"
                        + "    \"pren_online\": " + pren_online + ",\n"
                        + "    \"citta\": \"" + citta + "\",\n"
                        + "    \"nazione\": \"" + nazione + "\",\n"
                        + "    \"idRistorante\": " + idRistorante + "\n"
                        + "  }";

        return json;
    }


    /**
     * Deserializzatore personalizzato per {@link Ristorante},
     * utilizzato per convertire un oggetto JSON in un'istanza di {@code Ristorante}.
     */
    public static class RistoranteDeserializer implements JsonDeserializer<Ristorante> {
        /**
         * Converte un JSON in un oggetto {@link Ristorante}.
         * @param json JSON da convertire
         * @param typeOfT Tipo dell'oggetto atteso
         * @param context Contesto di deserializzazione
         * @return oggetto {@code Ristorante} deserializzato
         * @throws JsonParseException se il formato del JSON è errato
         */
        @Override
        public Ristorante deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            // Estrazione dei campi dal JSON
            String nome = obj.has("nome") && !obj.get("nome").isJsonNull() ? obj.get("nome").getAsString() : "Sconosciuto";
            String indirizzo = obj.has("indirizzo") && !obj.get("indirizzo").isJsonNull() ? obj.get("indirizzo").getAsString() : "Sconosciuto";
            String citta = obj.has("citta") && !obj.get("citta").isJsonNull() ? obj.get("citta").getAsString() : "Sconosciuta";
            String nazione = obj.has("nazione") && !obj.get("nazione").isJsonNull() ? obj.get("nazione").getAsString() : "Sconosciuta";
            float prezzo = obj.has("prezzo") && !obj.get("prezzo").isJsonNull() ? obj.get("prezzo").getAsFloat() : 0.0f;
            boolean consegna = obj.has("consegna") && !obj.get("consegna").isJsonNull() ? obj.get("consegna").getAsBoolean() : false;
            boolean prenOnline = obj.has("pren_online") && !obj.get("pren_online").isJsonNull() ? obj.get("pren_online").getAsBoolean() : false;
            int idRistorante = obj.has("idRistorante") && !obj.get("idRistorante").isJsonNull() ? obj.get("idRistorante").getAsInt() : -1;

            // Conversione tipoCucina da stringa a ArrayList<TipoCucina>
            String tipoStr = obj.get("tipoCucina").getAsString();
            String[] tipi = tipoStr.split(",\\s*");
            ArrayList<TipoCucina> tipoCucina = new ArrayList<>();
            for (String tipo : tipi) {
                TipoCucina tipoEnum = TipoCucina.fromString(tipo);
                if (tipoEnum != null) {
                    tipoCucina.add(tipoEnum);
                } else {
                    System.err.println("Tipo cucina non riconosciuto: " + tipo);
                }
            }

            // Crea l’oggetto Ristorante
            Ristorante r = new Ristorante(nome, indirizzo, citta, nazione, prezzo, tipoCucina);
            r.consegna = consegna;
            r.pren_online = prenOnline;
            r.idRistorante = idRistorante;

            return r;
        }
    }
}