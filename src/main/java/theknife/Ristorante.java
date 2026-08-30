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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe che rappresenta un ristorante.
 * Contiene tutte le informazioni relative a un esercizio di ristorazione,
 * tra cui il nome, la posizione (città e nazione), i tipi di cucina offerti,
 * la fascia di prezzo e i servizi disponibili (delivery, prenotazione online).
 * Implementa {@link Serializable} per la trasmissione RMI.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public class Ristorante implements Serializable {
    private static final long serialVersionUID = 1L;

    // <editor-fold desc="Attributi">
    public int idRistorante;
    public String nome;
    public String indirizzo;
    public String citta;
    public String nazione;
    public Double latitudine;
    public Double longitudine;
    public Float prezzo;
    public boolean consegna;
    public boolean pren_online;
    public ArrayList<TipoCucina> tipoCucina;


    /**
     * Costruttore completo per la creazione di un ristorante con coordinate geografiche.
     * 
     * @param nome Nome del ristorante
     * @param indirizzo Indirizzo fisico
     * @param citta Città di ubicazione
     * @param nazione Nazione di ubicazione
     * @param latitudine Latitudine per ricerca geografica
     * @param longitudine Longitudine per ricerca geografica
     * @param prezzo Fascia di prezzo medio
     * @param tipoCucina Lista dei tipi di cucina offerti
     */
    public Ristorante(String nome, String indirizzo, String citta, String nazione,
                      Double latitudine, Double longitudine, Float prezzo, ArrayList<TipoCucina> tipoCucina) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome non valido.");
        if (indirizzo == null || indirizzo.isBlank()) throw new IllegalArgumentException("Indirizzo non valido.");
        if (!isPrezzoValido(prezzo)) throw new IllegalArgumentException("Prezzo non valido.");
        if (tipoCucina == null) throw new IllegalArgumentException("Tipo cucina non valido.");

        this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = (citta != null && !citta.isBlank()) ? citta : "Sconosciuta";
        this.nazione = (nazione != null && !nazione.isBlank()) ? nazione : "Sconosciuta";
        this.latitudine = latitudine != null ? latitudine : 0.0;
        this.longitudine = longitudine != null ? longitudine : 0.0;
        this.prezzo = prezzo;
        this.consegna = false;
        this.pren_online = false;
        this.tipoCucina = tipoCucina;
    }

    /**
     * Costruttore semplificato senza coordinate geografiche (impostate a 0.0).
     * 
     * @param nome Nome del ristorante
     * @param indirizzo Indirizzo fisico
     * @param citta Città di ubicazione
     * @param nazione Nazione di ubicazione
     * @param prezzo Fascia di prezzo medio
     * @param tipoCucina Lista dei tipi di cucina offerti
     */
    public Ristorante(String nome, String indirizzo, String citta, String nazione, Float prezzo, ArrayList<TipoCucina> tipoCucina) {
        this(nome, indirizzo, citta, nazione, 0.0, 0.0, prezzo, tipoCucina);
    }

    /**
     * Verifica se il nome della città è valido (formato testo).
     * @param citta nome città da validare
     * @return true se valido
     */
    public static boolean isCittaValida(String citta) {
        return citta != null && citta.matches("^[\\p{L}\\s'\\-/\\.]+$");
    }

    /**
     * Verifica se il prezzo indicato è valido (non negativo).
     * @param prezzo valore del prezzo da validare
     * @return true se valido
     */
    public static boolean isPrezzoValido(Float prezzo) {
        return prezzo != null && prezzo >= 0;
    }

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

    @Override
    public String toString() {
        return nome + " in " + citta + " (" + nazione + ") - Prezzo: " + prezzo + "€\n"
                + "Coordinate: [" + latitudine + ", " + longitudine + "]\n"
                + "Delivery: " + (consegna ? "Sì" : "No") + " | Prenotazione Online: " + (pren_online ? "Sì" : "No") + "\n";
    }
}