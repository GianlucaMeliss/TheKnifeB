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
package theknife;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Classe astratta che rappresenta un utente del sistema (cliente o ristoratore).
 * Contiene attributi comuni a tutti gli utenti registrati e metodi per la gestione della registrazione.
 * Estende {@link OperazioniUtente}.
 * Implementa {@link Serializable} per la trasmissione RMI.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public abstract class Utente extends OperazioniUtente implements Serializable {
    private static final long serialVersionUID = 1L;

    // <editor-fold desc="Attributi">
    /** Nome dell'utente. */
    protected String nome;

    /** Cognome dell'utente. */
    protected String cognome;

    /** Username univoco dell'utente. */
    protected String username;

    /** Password dell'utente. Deve rispettare criteri di complessità. */
    protected String password;

    /** Data di nascita dell'utente. */
    protected LocalDate dataNasc;

    /** Indirizzo di domicilio dell'utente. */
    protected String domicilio;

    /** Ruolo dell'utente (CLIENTE o RISTORATORE). */
    protected Ruolo ruolo;

    /** Identificatore univoco dell'utente. */
    protected Integer idUtente;
    // </editor-fold>

    // <editor-fold desc="Costruttori">
    /**
     * Costruttore completo con ID.
     * @param Nome Nome dell'utente
     * @param Cognome Cognome dell'utente
     * @param Username Username per il login
     * @param Password Password (cifrata)
     * @param DataNasc Data di nascita
     * @param Domicilio Indirizzo dell'utente
     * @param Ruolo Ruolo (CLIENTE o RISTORATORE)
     * @param IdUtente ID univoco dal database
     */
    public Utente(String Nome, String Cognome, String Username, String Password, LocalDate DataNasc, String Domicilio, Ruolo Ruolo, int IdUtente) {
        this(Nome, Cognome, Username, Password, DataNasc, Domicilio, Ruolo);
        this.idUtente = IdUtente;
    }
    /**
     * Costruttore principale con validazione, senza ID.
     * @param Nome Nome dell'utente
     * @param Cognome Cognome dell'utente
     * @param Username Username per il login
     * @param Password Password (cifrata)
     * @param DataNasc Data di nascita
     * @param Domicilio Indirizzo dell'utente
     * @param Ruolo Ruolo (CLIENTE o RISTORATORE)
     * @throws IllegalArgumentException se i parametri non rispettano i criteri di validazione
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
     * @param Nome Nome dell'utente
     * @param Cognome Cognome dell'utente
     * @param Username Username per il login
     * @param Password Password (cifrata)
     * @param Domicilio Indirizzo dell'utente
     * @param Ruolo Ruolo (CLIENTE o RISTORATORE)
     */
    public Utente(String Nome, String Cognome, String Username, String Password, String Domicilio, Ruolo Ruolo) {
        this(Nome, Cognome, Username, Password, null, Domicilio, Ruolo);
    }
    // </editor-fold>

    // <editor-fold desc="Metodi">

    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public LocalDate getDataNasc() { return dataNasc; }
    public String getDomicilio() { return domicilio; }
    public Ruolo getRuolo() { return ruolo; }
    public Integer getIdUtente() { return idUtente; }
    public void setIdUtente(Integer idUtente) { this.idUtente = idUtente; }

    /**
     * Verifica se un nome è valido (solo lettere e spazi).
     * @param nome Nome da validare
     * @return true se valido, false altrimenti
     */
    public static boolean isNomeValido(String nome) {
        if (nome == null) return false;
        return nome.matches("[a-zA-Z\\sàèéìòùÀÈÉÌÒÙ]+"); //solo lettere e spazi
    }

    /**
     * Verifica se una password è valida.
     * Deve contenere almeno una maiuscola, una minuscola, un numero, un simbolo e minimo 6 caratteri.
     * @param password Password da validare
     * @return true se valida, false altrimenti
     */
    public static boolean isPasswordValida(String password) {
        if (password == null) return false;
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{6,}$"); //almeno una minuscola, una maiuscola, un numero e un simbolo e 6 caratteri
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
}
