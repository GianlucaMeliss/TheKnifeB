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
package theknife.remote;

import theknife.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interfaccia remota per il servizio TheKnife tramite RMI.
 * Fornisce i metodi per la gestione di utenti, ristoranti e recensioni con persistenza su database SQL.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public interface TheKnifeService extends Remote {

    // --- Gestione Utenti ---

    /**
     * Effettua il login di un utente.
     * @param username lo username dell'utente
     * @param password la password (cifrata o in chiaro a seconda della logica di business)
     * @return l'oggetto Utente se il login ha successo, null altrimenti
     * @throws RemoteException in caso di errori di comunicazione
     */
    Utente login(String username, String password) throws RemoteException;

    /**
     * Registra un nuovo utente nel sistema.
     * @param utente l'oggetto utente da registrare
     * @return true se la registrazione ha successo, false altrimenti
     * @throws RemoteException in caso di errori di comunicazione
     */
    boolean registraUtente(Utente utente) throws RemoteException;

    // --- Gestione Ristoranti ---

    /**
     * Recupera la lista di tutti i ristoranti nel sistema.
     * @return lista di tutti i ristoranti registrati
     * @throws RemoteException in caso di errori di comunicazione
     */
    ArrayList<Ristorante> getAllRistoranti() throws RemoteException;

    /**
     * Ricerca avanzata di ristoranti tramite filtri multipli eseguita sul server.
     * @param citta filtro città
     * @param lat latitudine
     * @param lon longitudine
     * @param nome filtro nome
     * @param tipoCucina filtro tipo cucina
     * @param prezzoMin prezzo minimo
     * @param prezzoMax prezzo massimo
     * @param delivery filtro consegna
     * @param online filtro prenotazione online
     * @param ratingMin rating minimo
     * @return lista dei ristoranti che soddisfano i criteri
     * @throws RemoteException in caso di errori di comunicazione
     */
    ArrayList<Ristorante> cercaRistorantiAvanzata(
            String citta, Double lat, Double lon, String nome, String tipoCucina,
            Float prezzoMin, Float prezzoMax,
            boolean delivery, boolean online,
            Double ratingMin
    ) throws RemoteException;

    /**
     * Recupera i dettagli di un singolo ristorante tramite ID.
     * @param id l'ID del ristorante
     * @return l'oggetto Ristorante corrispondente
     * @throws RemoteException in caso di errori di comunicazione
     */
    Ristorante getRistoranteById(int id) throws RemoteException;

    // --- Gestione Recensioni ---

    /**
     * Aggiunge una nuova recensione o risposta.
     * @param recensione l'oggetto recensione da salvare
     * @return true se l'operazione ha successo
     * @throws RemoteException in caso di errori di comunicazione
     */
    boolean aggiungiRecensione(Recensione recensione) throws RemoteException;

    /**
     * Modifica una recensione esistente.
     * @param idRecensione l'ID della recensione da modificare
     * @param recensione i nuovi dati della recensione
     * @return true se l'operazione ha successo
     * @throws RemoteException in caso di errori di comunicazione
     */
    boolean modificaRecensione(int idRecensione, Recensione recensione) throws RemoteException;

    /**
     * Elimina una recensione (e le eventuali risposte associate).
     * @param idRecensione l'ID della recensione da eliminare
     * @return true se l'operazione ha successo
     * @throws RemoteException in caso di errori di comunicazione
     */
    boolean eliminaRecensione(int idRecensione) throws RemoteException;

    /**
     * Recupera tutte le recensioni associate a un ristorante.
     * @param idRistorante ID del ristorante
     * @return lista di recensioni associate
     * @throws RemoteException in caso di errori di comunicazione
     */
    ArrayList<Recensione> getRecensioniByRistorante(int idRistorante) throws RemoteException;

    /**
     * Recupera tutte le recensioni scritte da un determinato utente.
     * @param idUtente ID dell'utente
     * @return lista di recensioni scritte dall'utente
     * @throws RemoteException in caso di errori di comunicazione
     */
    ArrayList<Recensione> getRecensioniByUtente(int idUtente) throws RemoteException;

    /**
     * Recupera la media delle valutazioni e il numero di recensioni per un ristorante.
     * @param idRistorante ID del ristorante
     * @return array [media, conteggio]
     * @throws RemoteException in caso di errori di comunicazione
     */
    double[] getStatisticheRistorante(int idRistorante) throws RemoteException;

    // --- Gestione Preferiti (Clienti) ---

    /**
     * Aggiunge un ristorante ai preferiti di un utente.
     * @param idUtente l'ID dell'utente cliente
     * @param idRistorante l'ID del ristorante
     * @return true se l'operazione ha successo
     * @throws RemoteException in caso di errori di comunicazione
     */
    boolean aggiungiPreferito(int idUtente, int idRistorante) throws RemoteException;

    /**
     * Rimuove un ristorante dai preferiti di un utente.
     * @param idUtente l'ID dell'utente cliente
     * @param idRistorante l'ID del ristorante
     * @return true se l'operazione ha successo
     * @throws RemoteException in caso di errori di comunicazione
     */
    boolean rimuoviPreferito(int idUtente, int idRistorante) throws RemoteException;

    /**
     * Recupera la lista dei ristoranti preferiti di un utente.
     * @param idUtente l'ID dell'utente cliente
     * @return la lista dei ristoranti preferiti
     * @throws RemoteException in caso di errori di comunicazione
     */
    ArrayList<Ristorante> getPreferitiUtente(int idUtente) throws RemoteException;

    // --- Gestione Ristoratori ---

    /**
     * Recupera la lista dei ristoranti gestiti da un determinato ristoratore.
     * @param idRistoratore l'ID dell'utente ristoratore
     * @return la lista dei ristoranti
     * @throws RemoteException in caso di errori di comunicazione
     */
    ArrayList<Ristorante> getRistorantiGestiti(int idRistoratore) throws RemoteException;

    /**
     * Aggiunge un nuovo ristorante associandolo a un ristoratore.
     * @param ristorante l'oggetto ristorante da aggiungere
     * @param idRistoratore l'ID del ristoratore proprietario
     * @return true se l'operazione ha successo
     * @throws RemoteException in caso di errori di comunicazione
     */
    boolean aggiungiRistorante(Ristorante ristorante, int idRistoratore) throws RemoteException;
}
