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
package theknife;

import java.io.Serializable;
import java.util.ArrayList;




/**
 * Classe che rappresenta un ristorante.
 * Contiene informazioni anagrafiche e funzionali come nome, indirizzo, città, nazione,
 * tipi di cucina, possibilità di consegna o prenotazione online, prezzo medio e ID univoco.
 * Implementa {@link Serializable} per la trasmissione RMI.
 *
 * @author Gianluca Melis
 * @author Davide Redemagni
 */
public class Ristorante implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * Costruttore compatibile senza coordinate (imposta coordinate a 0.0).
     */
    public Ristorante(String nome, String indirizzo, String citta, String nazione, Float prezzo, ArrayList<TipoCucina> tipoCucina) {
        this(nome, indirizzo, citta, nazione, 0.0, 0.0, prezzo, tipoCucina);
    }

    public static boolean isCittaValida(String citta) {
        return citta != null && citta.matches("^[\\p{L}\\s'\\-/\\.]+$");
    }

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