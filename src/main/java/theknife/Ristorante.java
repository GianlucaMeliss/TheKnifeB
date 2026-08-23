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

    // <editor-fold desc="Attributi">
    /** Nome del ristorante. */
    public String nome;

    /** Indirizzo fisico del ristorante. */
    public String indirizzo;

    /** Città in cui si trova il ristorante. */
    public String citta;

    /** Nazione in cui si trova il ristorante. */
    public String nazione;

    /** Prezzo medio indicativo del ristorante. */
    public Float prezzo;

    /** Flag che indica se il ristorante offre servizio di consegna. */
    public boolean consegna;

    /** Flag che indica se è possibile prenotare online. */
    public boolean pren_online;

    /** Elenco dei tipi di cucina offerti. */
    public ArrayList<TipoCucina> tipoCucina;

    /** Identificatore univoco del ristorante. */
    public int idRistorante;
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
        this.consegna = false;
        this.pren_online = false;
        this.tipoCucina = TipoCucina;

        if (!isCittaValida(citta) || !isCittaValida(nazione)) {
            if (citta == null || citta.isBlank()) citta = "Sconosciuta";
            if (nazione == null || nazione.isBlank()) nazione = "Sconosciuta";
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
     * @param citta Città o nazione da validare
     * @return true se il nome è valido, false altrimenti
     */
    public static boolean isCittaValida(String citta) {
        if (citta == null) return false;
        return citta.matches("^[\\p{L}\\s'\\-/\\.]+$");
    }

    /**
     * Verifica se il prezzo è valido.
     * @param prezzo Prezzo da validare
     * @return true se valido, false altrimenti
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
        return nome+" in "+citta+"("+nazione+")\tprezzo:"+prezzo+"€\n"+"Offre:\n-Prenotazione online=>"
                +(pren_online?"sì":"no")+"\n-Delivery=>"+(consegna?"sì":"no")+"\n\n";
    }
    // </editor-fold>
}
