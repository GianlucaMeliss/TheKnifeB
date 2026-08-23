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

import theknife.client.RmiClientManager;
import theknife.remote.TheKnifeService;

import java.time.LocalDate;
import java.util.*;

/**
 * Classe che rappresenta l'utente ristoratore, estensione della classe {@link Utente}.
 * Permette la gestione di ristoranti e risposte alle recensioni tramite il server remoto (RMI).
 * @author Gianluca
 * @author Simone
 */
public class Ristoratore extends Utente {
    private static final long serialVersionUID = 1L;

    /**
     * Costruttore completo con ID.
     * @param Nome Nome del ristoratore.
     * @param Cognome Cognome del ristoratore.
     * @param Username Username scelto.
     * @param Password Password scelta.
     * @param DataNasc Data di nascita.
     * @param Domicilio Domicilio.
     * @param Ruolo Ruolo dell’utente.
     * @param IdUtente ID univoco dell’utente.
     */
    public Ristoratore(String Nome, String Cognome, String Username, String Password,
                       LocalDate DataNasc, String Domicilio, Ruolo Ruolo, int IdUtente) {
        super(Nome, Cognome, Username, Password, DataNasc, Domicilio, Ruolo, IdUtente);
    }

    /**
     * Costruttore senza ID (viene generato automaticamente).
     * @param Nome Nome del ristoratore.
     * @param Cognome Cognome del ristoratore.
     * @param Username Username scelto.
     * @param Password Password scelta.
     * @param DataNasc Data di nascita.
     * @param Domicilio Domicilio.
     * @param Ruolo Ruolo dell’utente.
     */
    public Ristoratore(String Nome, String Cognome, String Username, String Password,
                       LocalDate DataNasc, String Domicilio, Ruolo Ruolo) {
        super(Nome, Cognome, Username, Password, DataNasc, Domicilio, Ruolo);
    }

    /**
     * Aggiunge un nuovo ristorante tramite il server remoto.
     * @param rist L'oggetto {@link Ristorante} da aggiungere.
     * @return {@code true} se l'aggiunta ha avuto successo, {@code false} altrimenti.
     */
    public boolean AggiungiRistorante(Ristorante rist) {
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return false;
            return service.aggiungiRistorante(rist, this.idUtente);
        } catch (Exception e) {
            System.err.println("Errore RMI aggiunta ristorante: " + e.getMessage());
            return false;
        }
    }

    /**
     * Invia una risposta a una recensione tramite il server remoto.
     * @param r La {@link Recensione} che rappresenta la risposta.
     * @return {@code true} se la risposta è stata salvata con successo, {@code false} altrimenti.
     */
    public boolean RispondiRecensione(Recensione r) {
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return false;
            r.fkIdUtente = this.idUtente;
            return service.aggiungiRecensione(r);
        } catch (Exception e) {
            System.err.println("Errore RMI risposta recensione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera la lista dei ristoranti gestiti dal ristoratore dal server.
     */
    public ArrayList<Ristorante> getRistorantiGestiti() {
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return new ArrayList<>();
            return service.getRistorantiGestiti(this.idUtente);
        } catch (Exception e) {
            System.err.println("Errore RMI recupero ristoranti gestiti: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Scarica la lista degli ID dei ristoranti posseduti (per compatibilità).
     * @return Lista di ID dei ristoranti posseduti.
     */
    public ArrayList<Integer> scaricaRistorantiPosseduti() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (Ristorante r : getRistorantiGestiti()) {
            ids.add(r.idRistorante);
        }
        return ids;
    }

    /**
     * Visualizza tutte le recensioni relative ai ristoranti gestiti.
     * @param listaRistoranti Lista di {@link Ristorante} per cui visualizzare le recensioni.
     * @return Lista di recensioni.
     */
    @Override
    public ArrayList<Recensione> visualizzaRecensioni(ArrayList<Ristorante> listaRistoranti) {
        ArrayList<Recensione> allRecs = new ArrayList<>();
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return allRecs;
            for (Ristorante r : listaRistoranti) {
                allRecs.addAll(service.getRecensioniByRistorante(r.idRistorante));
            }
        } catch (Exception e) {
            System.err.println("Errore RMI visualizzazione recensioni ristoratore: " + e.getMessage());
        }
        return allRecs;
    }
}
