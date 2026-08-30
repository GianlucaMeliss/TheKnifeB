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

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.time.LocalDate;

/**
 * Rappresenta un utente non registrato dell'applicazione.
 * Definisce le operazioni disponibili per gli utenti che non hanno effettuato l'accesso,
 * come la ricerca, la visualizzazione di recensioni e la registrazione di nuovi account tramite il server remoto.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public class UtenteNonRegistrato extends OperazioniUtente{
    /**
     * La città associata all'utente non registrato.
     * <p>
     * Questo attributo può essere utilizzato per contestualizzare la ricerca
     * o le operazioni dell'utente.
     */
    String citta;

    /**
     * Costruisce un'istanza di UtenteNonRegistrato.
     * @param Citta La città da associare alla sessione dell'utente.
     */
    public UtenteNonRegistrato(String Citta) {
        this.citta = Citta;
    }


    /**
     * Gestisce il processo di registrazione di un nuovo utente tramite il servizio remoto.
     * Crea un oggetto {@link Utente} (Cliente o Ristoratore) e lo invia al server
     * per la persistenza su database SQL.
     * @return <code>true</code> se la registrazione è avvenuta con successo, <code>false</code> altrimenti.
     */
    public static boolean Registrazione(
                                 String ruoloInput,
                                 String nome,
                                 String cognome,
                                 String username,
                                 String password,
                                 String dataNascitaStr,
                                 String domicilio
    ) {
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) {
                System.err.println("Servizio RMI non disponibile.");
                return false;
            }

            Ruolo ruolo = Ruolo.valueOf(ruoloInput.toUpperCase());
            String passwordCifrata = Gestione.CifraturaUtils.cripta(password);
            LocalDate dataNascita = LocalDate.parse(dataNascitaStr);

            Utente u;
            if (ruolo == Ruolo.CLIENTE) {
                u = new UtenteRegistrato(nome, cognome, username, passwordCifrata, dataNascita, domicilio, ruolo);
            } else {
                u = new Ristoratore(nome, cognome, username, passwordCifrata, dataNascita, domicilio, ruolo);
            }

            return service.registraUtente(u);

        } catch (Exception e) {
            System.err.println("Errore nei dati inseriti o di comunicazione: " + e.getMessage());
        }
        return false;
    }

    /**
     * Recupera le recensioni associate a una lista specifica di ristoranti tramite il server.
     * @param listaRistoranti La lista di oggetti <code>Ristorante</code> per cui si desidera visualizzare le recensioni.
     * @return Un <code>ArrayList</code> di oggetti <code>Recensione</code>.
     */
    public ArrayList<Recensione> visualizzaRecensioni(ArrayList<Ristorante> listaRistoranti){
        ArrayList<Recensione> recensioniRisultanti= new ArrayList<>();
        try {
            TheKnifeService service = RmiClientManager.getInstance().getService();
            if (service == null) return recensioniRisultanti;

            for(Ristorante ristorante : listaRistoranti) {
                recensioniRisultanti.addAll(service.getRecensioniByRistorante(ristorante.idRistorante));
            }
        } catch (Exception e) {
            System.err.println("Errore recupero recensioni: " + e.getMessage());
        }
        return recensioniRisultanti;
    }

}