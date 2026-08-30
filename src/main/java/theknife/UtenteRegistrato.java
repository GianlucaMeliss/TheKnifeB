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

import theknife.client.RmiClientManager;
import theknife.remote.TheKnifeService;

import java.time.LocalDate;
import java.util.*;

/**
 * Classe che rappresenta un utente registrato (cliente), estensione della classe {@link Utente}.
 * Permette la gestione di recensioni e preferiti tramite il server remoto (RMI).
 * @author Gianluca Melis
 * @author Alessandro Melnyk
 * @author Davide Redemagni
 * @author Simone Zamberletti
 */
public class UtenteRegistrato extends Utente {
    private static final long serialVersionUID = 1L;

    /**
     * Costruttore con ID utente esplicito.
     * @param Nome Nome dell'utente.
     * @param Cognome Cognome dell'utente.
     * @param Username Username dell'utente.
     * @param Password Password dell'utente.
     * @param DataNasc Data di nascita.
     * @param Domicilio Domicilio dell'utente.
     * @param Ruolo Ruolo assegnato all'utente.
     * @param IdUtente Identificatore univoco dell'utente.
     */
    public  UtenteRegistrato(String Nome, String Cognome, String Username, String Password, LocalDate DataNasc, String Domicilio, Ruolo Ruolo, Integer IdUtente) {
        super(Nome, Cognome,  Username, Password, DataNasc, Domicilio, Ruolo, IdUtente);
    }

    /**
     * Costruttore con tutti i parametri compresa la data di nascita.
     * @param Nome Nome dell'utente.
     * @param Cognome Cognome dell'utente.
     * @param Username Username dell'utente.
     * @param Password Password dell'utente.
     * @param DataNasc Data di nascita.
     * @param Domicilio Domicilio dell'utente.
     * @param Ruolo Ruolo assegnato all'utente.
     */
    public  UtenteRegistrato(String Nome, String Cognome, String Username, String Password, LocalDate DataNasc, String Domicilio, Ruolo Ruolo) {
        super(Nome, Cognome,  Username, Password, DataNasc, Domicilio, Ruolo);
    }

    /**
     * Costruttore senza data di nascita.
     * @param Nome Nome dell'utente.
     * @param Cognome Cognome dell'utente.
     * @param Username Username dell'utente.
     * @param Password Password dell'utente.
     * @param Domicilio Domicilio dell'utente.
     * @param Ruolo Ruolo assegnato all'utente.
     */
    public UtenteRegistrato(String Nome, String Cognome, String Username, String Password, String Domicilio, Ruolo Ruolo) {
        super(Nome, Cognome,  Username, Password, Domicilio, Ruolo);
    }

    /**
     * Aggiunge una recensione tramite il server remoto.
     * @param r Recensione da aggiungere.
     * @return true se l'operazione va a buon fine, false altrimenti.
     */
    public boolean aggiungiRecensione(Recensione r){
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return false;
            return service.aggiungiRecensione(r);
        } catch (Exception e) {
            System.err.println("Errore RMI aggiunta recensione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifica una recensione esistente tramite il server remoto.
     * @param idRecensione ID della recensione da modificare.
     * @param r Nuova versione della recensione.
     * @return true se la recensione è stata modificata.
     */
    public boolean modificaRecensione(Integer idRecensione, Recensione r) {
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return false;
            return service.modificaRecensione(idRecensione, r);
        } catch (Exception e) {
            System.err.println("Errore RMI modifica recensione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una recensione tramite il server remoto.
     * @param idRecensione L'ID della recensione da eliminare.
     * @return {@code true} se l'operazione ha avuto successo.
     */
    public boolean eliminaRecensione(Integer idRecensione) {
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return false;
            return service.eliminaRecensione(idRecensione);
        } catch (Exception e) {
            System.err.println("Errore RMI eliminazione recensione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Aggiunge un ristorante ai preferiti tramite il server remoto.
     * 
     * @param r il ristorante da aggiungere ai preferiti
     * @return true se l'operazione ha successo
     */
    public boolean AggiungiPreferiti(Ristorante r) {
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return false;
            return service.aggiungiPreferito(this.idUtente, r.idRistorante);
        } catch (Exception e) {
            System.err.println("Errore RMI aggiunta preferito: " + e.getMessage());
            return false;
        }
    }

    /**
     * Rimuove un ristorante dai preferiti tramite il server remoto.
     * 
     * @param r il ristorante da rimuovere dai preferiti
     * @return true se l'operazione ha successo
     */
    public boolean RimuoviPreferito(Ristorante r) {
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return false;
            return service.rimuoviPreferito(this.idUtente, r.idRistorante);
        } catch (Exception e) {
            System.err.println("Errore RMI rimozione preferito: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera la mappa completa dei preferiti (simulata tramite chiamata remota per compatibilità).
     * 
     * @return una mappa con l'ID utente e la lista degli ID dei ristoranti preferiti
     */
    public HashMap<Integer, ArrayList<Integer>> VisualizzaPreferiti() {
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return map;

            ArrayList<Ristorante> preferiti = service.getPreferitiUtente(this.idUtente);
            ArrayList<Integer> ids = new ArrayList<>();
            for (Ristorante r : preferiti) {
                ids.add(r.idRistorante);
            }
            map.put(this.idUtente, ids);
        } catch (Exception e) {
            System.err.println("Errore RMI visualizzazione preferiti: " + e.getMessage());
        }
        return map;
    }

    /**
     * Controlla se un dato ristorante è tra i preferiti.
     * 
     * @param idUtente ID dell'utente da controllare
     * @param idRistorante ID del ristorante da verificare
     * @param preferiti mappa dei preferiti caricata
     * @return true se il ristorante è presente tra i preferiti dell'utente
     */
    public boolean VerificaPreferiti(int idUtente, int idRistorante, HashMap<Integer, ArrayList<Integer>> preferiti) {
        ArrayList<Integer> lista = preferiti.get(idUtente);
        return lista != null && lista.contains(idRistorante);
    }


    /**
     * Restituisce l'identificatore univoco (ID) dell'utente registrato.
     * @return L'{@code Integer} che rappresenta l'ID dell'utente.
     */
    public Integer getIdUtente() {
        return idUtente;
    }

    /**
     * Metodo ereditato per visualizzare le recensioni, non ancora implementato.
     * @param listaRistoranti Lista di ristoranti filtrati di cui visualizzare le recensioni.
     * @return Sempre {@code null}, poiché la funzionalità non è stata implementata.
     */
    @Override
    public ArrayList<Recensione> visualizzaRecensioni(ArrayList<Ristorante> listaRistoranti){
        return null;
    }
}