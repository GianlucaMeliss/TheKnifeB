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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

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
 * Questa classe definisce le operazioni disponibili per gli utenti che non hanno
 * ancora effettuato l'accesso o la registrazione, come la possibilità di creare un
 * nuovo account e di visualizzare le recensioni dei ristoranti.
 *
 * @author Melnyk Alessandro
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
     * Gestisce il processo di registrazione di un nuovo utente.
     * Il metodo acquisisce i dati dell'utente tramite console, richiedendo di scegliere
     * un ruolo (Cliente o Ristoratore) e di inserire le informazioni personali.
     * Effettua una verifica per assicurarsi che l'username non sia già in uso per il ruolo
     * scelto prima di salvare il nuovo utente.
     * @return <code>true</code> se la registrazione è avvenuta con successo, <code>false</code> altrimenti.
     * Il metodo ritorna <code>false</code> se il ruolo inserito non è valido, il formato della data
     * non è corretto, l'username è già esistente o si verifica un altro errore
     * durante l'inserimento dei dati.
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
            Ruolo ruolo = Ruolo.valueOf(ruoloInput.toUpperCase());
            password = Gestione.CifraturaUtils.cripta(password);
            LocalDate dataNascita = LocalDate.parse(dataNascitaStr);

            if (!VerificaUtente(username, ruolo)) {
                Utente u;
                if (ruolo == Ruolo.CLIENTE) {
                    u = new UtenteRegistrato(nome, cognome, username, password, dataNascita, domicilio, ruolo);
                } else {
                    u = new Ristoratore(nome, cognome, username, password, dataNascita, domicilio, ruolo);
                }

                u.aggiungiUtente(u);
                return true;
            } else {
                System.out.println("Utente già esistente con questo username per il ruolo scelto.");
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ruolo non valido.");
        } catch (DateTimeParseException e) {
            System.out.println("Formato della data di nascita non corretto.");
        } catch (Exception e) {
            System.out.println("Errore nei dati inseriti: " + e.getMessage());
        }
        return false;
    }

    /**
     * Recupera le recensioni associate a una lista specifica di ristoranti.
     * Il metodo carica tutte le recensioni dal file di persistenza e le filtra
     * per restituire solo quelle pertinenti ai ristoranti forniti.
     * @param listaRistoranti La lista di oggetti <code>Ristorante</code> per cui si desidera visualizzare le recensioni.
     * @return Un <code>ArrayList</code> di oggetti <code>Recensione</code> che corrispondono ai ristoranti nella lista data.
     */
    public ArrayList<Recensione> visualizzaRecensioni(ArrayList<Ristorante> listaRistoranti){
        String filePath="data/recensioni.json";
        ArrayList<Recensione> listaRecensioni= new ArrayList<>();
        ArrayList<Recensione> recensioniRisultanti= new ArrayList<>();
        listaRecensioni= caricaRecensioni();
        for(Ristorante ristorante : listaRistoranti)
        {
            for(Recensione recensione: listaRecensioni){
                if(ristorante.idRistorante==recensione.fkIdRistorante) recensioniRisultanti.add(recensione);
            }
        }
        return recensioniRisultanti;
    }

    /**
     * Carica la lista completa delle recensioni dal file JSON di persistenza.
     * Utilizza un deserializzatore custom per interpretare correttamente i dati
     * dal file <code>recensioni.json</code>.
     * @return Un <code>ArrayList</code> contenente tutte le recensioni presenti nel sistema.
     */
    public ArrayList<Recensione> caricaRecensioni() {
        String filePath = "data/recensioni.json";
        Type listType = new TypeToken<ArrayList<Recensione>>(){}.getType();
        JsonDeserializer<Recensione> adapter = new Recensione.RecensioneDeserializer();

        return Gestione.Deserializer.fromJsonFile(filePath, Recensione.class, adapter);
    }

    /**
     * Verifica l'esistenza di un utente con un dato nickname e ruolo.
     * Controlla nel file <code>utenti.json</code> se esiste già un utente
     * registrato con la stessa combinazione di username e ruolo.
     * @param nickname Il nickname (username) da verificare.
     * @param r Il ruolo (<code>CLIENTE</code> o <code>RISTORATORE</code>) associato al nickname.
     * @return <code>true</code> se un utente con quel nickname e ruolo esiste già, <code>false</code> altrimenti.
     */
    static boolean VerificaUtente(String nickname,Ruolo r){
        ArrayList<Utente> listaUtenti = new ArrayList<>();
        listaUtenti=Gestione.Deserializer.fromJsonFile(
                "data/utenti.json",
                Utente.class,
                new Utente.UtenteDeserializer());
        for(Utente u: listaUtenti){
            if(u.username.equals(nickname) && u.ruolo.equals(r))return true;
        }
        return false;
    }
}