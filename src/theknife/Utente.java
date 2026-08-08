package theknife;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Classe astratta che rappresenta un utente del sistema (cliente o ristoratore).
 * Contiene attributi comuni a tutti gli utenti registrati e metodi per la gestione della registrazione.
 * Estende {@link OperazioniUtente}.
 *
 * @author Davide Redemagni
 */
public abstract class Utente extends OperazioniUtente{
    // <editor-fold desc="Attributi">
    /** Nome dell'utente. */
    String nome;

    /** Cognome dell'utente. */
    String cognome;

    /** Username univoco dell'utente. */
    String username;

    /** Password dell'utente. Deve rispettare criteri di complessità. */
    String password;

    /** Data di nascita dell'utente. */
    LocalDate dataNasc;

    /** Indirizzo di domicilio dell'utente. */
    String domicilio;

    /** Ruolo dell'utente (CLIENTE o RISTORATORE). */
    Ruolo ruolo;

    /** Identificatore univoco dell'utente. */
    Integer idUtente;
    // </editor-fold>

    // <editor-fold desc="Costruttori">
    /**
     * Costruttore completo per la deserializzazione da file JSON.
     *
     * @param Nome Nome dell'utente
     * @param Cognome Cognome dell'utente
     * @param Username Username
     * @param Password Password
     * @param DataNasc Data di nascita
     * @param Domicilio Indirizzo di domicilio
     * @param Ruolo Ruolo dell'utente
     * @param IdUtente Identificativo univoco
     */
    public Utente(String Nome, String Cognome, String Username, String Password, LocalDate DataNasc, String Domicilio, Ruolo Ruolo, int IdUtente) {
        //Costruttore per deserializzare da file json
        this(Nome, Cognome, Username, Password, DataNasc, Domicilio, Ruolo);
        this.idUtente = IdUtente;
    }
    /**
     * Costruttore principale con validazione, senza ID.
     *
     * @param Nome Nome dell'utente
     * @param Cognome Cognome dell'utente
     * @param Username Username
     * @param Password Password
     * @param DataNasc Data di nascita
     * @param Domicilio Indirizzo di domicilio
     * @param Ruolo Ruolo dell'utente
     */
    public Utente(String Nome, String Cognome, String Username, String Password, LocalDate DataNasc, String Domicilio, Ruolo Ruolo) {
        this.nome = Nome;
        this.cognome = Cognome;
        this.username = Username;
        this.password = Password;
        this.dataNasc = DataNasc;
        this.domicilio = Domicilio;
        this.ruolo = Ruolo;
        if (!isNomeValido(nome) || !isNomeValido(cognome)) {
                throw new IllegalArgumentException("Nome e cognome devono contenere solo lettere.");
        }
        if (!isPasswordValida(password)) {
            throw new IllegalArgumentException("Password non valida: deve contenere almeno 6 caratteri, una minuscola, una maiuscola, un numero e un simbolo.");
        }
        if (Username == null) {
            throw new IllegalArgumentException("Username non valido.");
        }
        if (Ruolo == null) {
            throw new IllegalArgumentException("Ruolo non valido.");
        }
        if (Domicilio == null) {
            throw new IllegalArgumentException("Domicilio non valido.");
        }
    }
    /**
     * Costruttore per inserimento rapido (senza data di nascita).
     *
     * @param Nome Nome dell'utente
     * @param Cognome Cognome dell'utente
     * @param Username Username
     * @param Password Password
     * @param Domicilio Indirizzo di domicilio
     * @param Ruolo Ruolo dell'utente
     */
    public Utente(String Nome, String Cognome, String Username, String Password, String Domicilio, Ruolo Ruolo) { //costruttore per inserimento
        this(Nome, Cognome, Username, Password, null, Domicilio, Ruolo);                                 //senza data di nascita
    }
    // </editor-fold>

    // <editor-fold desc="Metodi">

    /**
     * Verifica se un nome è valido (solo lettere e spazi).
     * @param nome Nome da validare
     * @return true se valido, false altrimenti
     */
    static boolean isNomeValido(String nome) {
        if (nome == null) return false;
        return nome.matches("[a-zA-Z\\sàèéìòùÀÈÉÌÒÙ]+"); //solo lettere e spazi
    }

    /**
     * Verifica se una password è valida.
     * Deve contenere almeno una maiuscola, una minuscola, un numero, un simbolo e minimo 6 caratteri.
     * @param password Password da validare
     * @return true se valida, false altrimenti
     */
    static boolean isPasswordValida(String password) {
        if (password == null) return false;
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{6,}$"); //almeno una minuscola, una maiuscola, un numero e un simbolo e 6 caratteri
    }

    /**
     * Aggiunge un utente al file <code>utenti.json</code>.
     * @param utente Utente da aggiungere
     * @return true se l'operazione va a buon fine, false in caso di errore
     */
    public boolean aggiungiUtente(Utente utente) {
        // 1. Legge l'elenco esistente
        ArrayList<Utente> utenti = Gestione.Deserializer.fromJsonFile(
                "data/utenti.json",
                Utente.class,
                new Utente.UtenteDeserializer()
        );

        // 3. Calcola nuovo ID
        utente.idUtente = utenti.getLast().idUtente + 1;

        // 4. Serializza il nuovo utente
        String nuovoUtenteJson = utente.toJson();

        try {
            // 5. Legge e modifica il contenuto JSON
            Path path = Paths.get("data/utenti.json");
            String content = Files.readString(path).trim();

            // Rimuove la chiusura dell'array
            if (content.endsWith("]")) {
                content = content.substring(0, content.lastIndexOf("]")).trim();
            }

            // Aggiunge virgola se necessario
            if (!content.endsWith("[")) {
                content += ",";
            }

            // Aggiunge nuovo JSON e chiude l'array
            content += "\n" + nuovoUtenteJson + "\n]";

            // Scrive nel file
            Files.writeString(path, content, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;

        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file JSON: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Serializza l'oggetto utente in formato JSON.
     * @return Stringa JSON rappresentante l'utente
     */
    public String toJson() {
        return "  {\n"
                + "    \"idUtente\": " + idUtente + ",\n"
                + "    \"nome\": \"" + nome + "\",\n"
                + "    \"cognome\": \"" + cognome + "\",\n"
                + "    \"username\": \"" + username + "\",\n"
                + "    \"password\": \"" + password + "\",\n"
                + "    \"data\": \"" + dataNasc + "\",\n"
                + "    \"domicilio\": \"" + domicilio + "\",\n"
                + "    \"ruolo\": \"" + ruolo + "\"\n"
                + "  }";
    }

    /**
     * Restituisce una rappresentazione leggibile dell'utente.
     * @return Stringa formattata con i dati dell'utente
     */
    @Override
    public String toString() {
        return "Utente:\n" +
                "  ID: " + (idUtente != null ? idUtente : "N/D") + "\n" +
                "  Nome: " + nome + "\n" +
                "  Cognome: " + cognome + "\n" +
                "  Username: " + username + "\n" +
                "  Password: " + password + "\n" +
                "  Data di nascita: " + (dataNasc != null ? dataNasc : "Non specificata") + "\n" +
                "  Domicilio: " + domicilio + "\n" +
                "  Ruolo: " + (ruolo != null ? ruolo : "Non assegnato") + "\n";
    }
    // </editor-fold>
    /**
     * Deserializzatore personalizzato per {@link Utente}.
     * Riconosce il ruolo e crea un oggetto {@link UtenteRegistrato} o {@link Ristoratore}.
     */
    public static class UtenteDeserializer implements JsonDeserializer<Utente> {

        /**
         * Deserializza un oggetto JSON in una sottoclasse di {@link Utente}.
         * @param json Oggetto JSON da deserializzare
         * @param typeOfT Tipo target
         * @param context Contesto di deserializzazione
         * @return Oggetto {@link Utente} specifico (cliente o ristoratore)
         * @throws JsonParseException in caso di errore di parsing
         */
        @Override
        public Utente deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            String nome = obj.has("nome") && !obj.get("nome").isJsonNull() ? obj.get("nome").getAsString() : null;
            String cognome = obj.has("cognome") && !obj.get("cognome").isJsonNull() ? obj.get("cognome").getAsString() : null;
            String username = obj.has("username") && !obj.get("username").isJsonNull() ? obj.get("username").getAsString() : null;
            String password = obj.has("password") && !obj.get("password").isJsonNull() ? obj.get("password").getAsString() : null;
            String domicilio = obj.has("domicilio") && !obj.get("domicilio").isJsonNull() ? obj.get("domicilio").getAsString() : null;

            LocalDate dataNasc = null;
            if (obj.has("data") && !obj.get("data").isJsonNull()) {
                try {
                    dataNasc = LocalDate.parse(obj.get("data").getAsString());
                } catch (Exception e) {
                    System.err.println("Data non valida: " + obj.get("data"));
                }
            }

            Ruolo ruolo = null;
            if (obj.has("ruolo") && !obj.get("ruolo").isJsonNull()) {
                try {
                    ruolo = Ruolo.valueOf(obj.get("ruolo").getAsString());
                } catch (IllegalArgumentException e) {
                    System.err.println("Ruolo non valido: " + obj.get("ruolo").getAsString());
                }
            }

            Integer idUtente = obj.has("idUtente") && !obj.get("idUtente").isJsonNull()
                    ? obj.get("idUtente").getAsInt()
                    : null;

            Utente utente;
            switch (ruolo) {
                case Ruolo.CLIENTE: utente= new UtenteRegistrato(nome, cognome, username, password, dataNasc, domicilio, ruolo);
                    break;
                case Ruolo.RISTORATORE: utente= new Ristoratore(nome, cognome, username, password, dataNasc, domicilio, ruolo);
                    break;
                default:
                    throw new IllegalArgumentException("Ruolo non valido.");
            }
            utente.idUtente = idUtente;
            return utente;
        }
    }

}