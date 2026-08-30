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

import theknife.CriterioRicerca;
import theknife.Ristorante;
import theknife.TipoCucina;

import java.util.ArrayList;
import java.util.Map;

/**
 * Interfaccia Data Access Object (DAO) per la gestione dei ristoranti.
 * Definisce i metodi per l'accesso e la persistenza dei dati relativi ai ristoranti su database.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public interface RistoranteDAO {
    /**
     * Recupera tutti i ristoranti presenti nel database, ordinati per nome.
     *
     * @return una lista di oggetti {@link Ristorante}
     */
    ArrayList<Ristorante> getAllRistoranti();

    /**
     * Recupera un ristorante specifico tramite il suo identificativo univoco.
     *
     * @param id l'ID del ristorante
     * @return l'oggetto {@link Ristorante} trovato, null altrimenti
     */
    Ristorante getRistoranteById(int id);

    /**
     * Esegue una ricerca avanzata di ristoranti applicando filtri opzionali.
     *
     * @param citta filtro per città (case-insensitive)
     * @param lat latitudine per ricerca geografica (raggio 20km)
     * @param lon longitudine per ricerca geografica (raggio 20km)
     * @param nome filtro parziale sul nome del ristorante
     * @param tipoCucina filtro per tipologia di cucina
     * @param pMin prezzo minimo
     * @param pMax prezzo massimo
     * @param delivery se true, filtra solo ristoranti con consegna
     * @param online se true, filtra solo ristoranti con prenotazione online
     * @param ratingMin valutazione media minima richiesta
     * @return lista dei ristoranti che soddisfano i criteri
     */
    ArrayList<Ristorante> cercaAvanzata(String citta, Double lat, Double lon, String nome, String tipoCucina, Float pMin, Float pMax, boolean delivery, boolean online, Double ratingMin);

    /**
     * Aggiunge un nuovo ristorante al database e lo associa a un ristoratore.
     *
     * @param r l'oggetto {@link Ristorante} da aggiungere
     * @param idRistoratore l'ID dell'utente ristoratore che lo gestirà
     * @return true se l'operazione è riuscita, false altrimenti
     */
    boolean aggiungiRistorante(Ristorante r, int idRistoratore);

    /**
     * Recupera i ristoranti gestiti da un determinato ristoratore.
     *
     * @param idRistoratore l'ID del ristoratore
     * @return una lista di {@link Ristorante} gestiti
     */
    ArrayList<Ristorante> getRistorantiGestiti(int idRistoratore);

    /**
     * Aggiunge un ristorante alla lista dei preferiti di un utente.
     *
     * @param idUtente l'ID dell'utente cliente
     * @param idRistorante l'ID del ristorante da aggiungere
     * @return true se aggiunto con successo, false altrimenti
     */
    boolean aggiungiPreferito(int idUtente, int idRistorante);

    /**
     * Rimuove un ristorante dalla lista dei preferiti di un utente.
     *
     * @param idUtente l'ID dell'utente cliente
     * @param idRistorante l'ID del ristorante da rimuovere
     * @return true se rimosso con successo, false altrimenti
     */
    boolean rimuoviPreferito(int idUtente, int idRistorante);

    /**
     * Recupera la lista dei ristoranti preferiti di un utente.
     *
     * @param idUtente l'ID dell'utente cliente
     * @return una lista di {@link Ristorante} preferiti
     */
    ArrayList<Ristorante> getPreferitiUtente(int idUtente);
}