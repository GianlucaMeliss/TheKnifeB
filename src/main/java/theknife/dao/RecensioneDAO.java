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
import java.util.ArrayList;

/**
 * Interfaccia Data Access Object (DAO) per la gestione delle recensioni.
 * Gestisce la persistenza e il recupero dei commenti e delle valutazioni su database.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public interface RecensioneDAO {
    /**
     * Aggiunge una nuova recensione o una risposta nel database.
     *
     * @param r l'oggetto {@link Recensione} da salvare
     * @return true se l'inserimento è riuscito, false altrimenti
     */
    boolean aggiungiRecensione(Recensione r);

    /**
     * Modifica il contenuto o il voto di una recensione esistente.
     *
     * @param idRecensione l'ID della recensione da modificare
     * @param r l'oggetto {@link Recensione} con i nuovi dati
     * @return true se la modifica è riuscita, false altrimenti
     */
    boolean modificaRecensione(int idRecensione, Recensione r);

    /**
     * Elimina una recensione e le sue eventuali risposte dal database.
     *
     * @param idRecensione l'ID della recensione principale da eliminare
     * @return true se l'eliminazione è riuscita, false altrimenti
     */
    boolean eliminaRecensione(int idRecensione);

    /**
     * Recupera tutte le recensioni associate a un ristorante.
     *
     * @param idRistorante l'ID del ristorante
     * @return una lista di {@link Recensione}
     */
    ArrayList<Recensione> getRecensioniByRistorante(int idRistorante);

    /**
     * Recupera tutte le recensioni scritte da un utente specifico.
     *
     * @param idUtente l'ID dell'utente
     * @return una lista di {@link Recensione}
     */
    ArrayList<Recensione> getRecensioniByUtente(int idUtente);

    /**
     * Calcola la media dei voti e il numero totale di recensioni per un ristorante.
     *
     * @param idRistorante l'ID del ristorante
     * @return un array di double: [media_voti, totale_recensioni]
     */
    double[] getStatisticheRistorante(int idRistorante);
}