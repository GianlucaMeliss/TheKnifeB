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

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.*;

/**
 * Classe che rappresenta un utente registrato (cliente), estensione della classe {@link Utente}.
 * Permette la gestione di recensioni: aggiunta, modifica, eliminazione e visualizzazione.
 * @author Gianluca Melis
 * @author Alessandro Melnyk
 * @author Davide Redemagni
 * @author Simone Zamberletti
 */
public class UtenteRegistrato extends Utente {

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
     * Aggiunge una recensione all'elenco presente nel file JSON.
     * @param r Recensione da aggiungere.
     * @return true se l'operazione va a buon fine, false altrimenti.
     */
    public boolean aggiungiRecensione(Recensione r){
        String filePath = "data/recensioni.json";

        try {
            // 1. Carica tutte le recensioni dal file
            JsonArray recensioniArray = JsonParser.parseReader(new FileReader(filePath)).getAsJsonArray();

            // 2. Trova l'ID massimo per assegnare un nuovo ID univoco
            int maxId = 0;
            for (JsonElement el : recensioniArray) {
                JsonObject obj = el.getAsJsonObject();
                int currentId = obj.get("idRecensione").getAsInt();
                if (currentId > maxId) maxId = currentId;
            }

            int nuovoId = maxId + 1;

            // 3. Imposta id della risposta
            r.idRecensione = nuovoId;

            // 4. Converti la risposta in JsonObject
            JsonObject rispostaJson = new JsonObject();
            rispostaJson.addProperty("idRecensione", r.idRecensione);
            rispostaJson.addProperty("fkIdRistorante", r.fkIdRistorante);
            rispostaJson.addProperty("fkIdUtente", r.fkIdUtente);
            rispostaJson.addProperty("voto", r.voto);
            rispostaJson.addProperty("commento", r.commento);
            rispostaJson.addProperty("data", r.data.toString());
            rispostaJson.addProperty("idRecensionePadre", r.idRecensionePadre);

            // 5. Aggiungi la risposta all'array
            recensioniArray.add(rispostaJson);

            // 6. Scrivi l'array aggiornato nel file con pretty printing
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonAggiornato = gson.toJson(recensioniArray);

            Files.writeString(Paths.get(filePath), jsonAggiornato, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

            return true;

        } catch (IOException e) {
            //System.err.println("Errore nel rispondere alla recensione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifica una recensione esistente nel file JSON.
     * @param idRecensione ID della recensione da modificare.
     * @param r Nuova versione della recensione.
     * @return true se la recensione è stata modificata, false se non trovata o errore.
     */
    public boolean modificaRecensione(Integer idRecensione, Recensione r) {
        String filePath = "data/recensioni.json";

        try {
            // 1. Carica tutte le recensioni dal file
            JsonArray recensioniArray = JsonParser.parseReader(new FileReader(filePath)).getAsJsonArray();

            boolean trovata = false;
            int i = 0;

            while (i < recensioniArray.size() && !trovata) {
                // 2. Scorri tutte le recensioni per trovare quella da modificare
                JsonObject obj = recensioniArray.get(i).getAsJsonObject();
                int currentId = obj.get("idRecensione").getAsInt();

                if (currentId == idRecensione) {
                    // 3. Costruisci il nuovo oggetto recensione aggiornato
                    JsonObject nuovaRecensione = new JsonObject();
                    nuovaRecensione.addProperty("idRecensione", idRecensione);
                    nuovaRecensione.addProperty("fkIdRistorante", r.fkIdRistorante);
                    nuovaRecensione.addProperty("fkIdUtente", r.fkIdUtente);
                    nuovaRecensione.addProperty("voto", r.voto);
                    nuovaRecensione.addProperty("commento", r.commento);
                    nuovaRecensione.addProperty("data", r.data.toString());
                    nuovaRecensione.addProperty("idRecensionePadre", r.idRecensionePadre);

                    // 4. Sostituisci la vecchia recensione con quella nuova
                    recensioniArray.set(i, nuovaRecensione);
                    trovata = true;
                    break;
                }
                i++;
            }

            if (!trovata) return false; // non trovata

            // 5. Scrivi il file aggiornato
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonAggiornato = gson.toJson(recensioniArray);

            Files.writeString(Paths.get(filePath), jsonAggiornato, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Elimina una recensione e la sua eventuale risposta.
     * <p>
     * La logica è stata aggiornata per rimuovere dal file JSON sia la recensione
     * con l'ID specificato, sia qualsiasi altra recensione che la abbia come "padre",
     * garantendo la coerenza dei dati.
     * </p>
     * @param idRecensione L'ID della recensione da eliminare.
     * @return {@code true} se almeno un elemento è stato rimosso, {@code false} altrimenti.
     */
    public boolean eliminaRecensione(Integer idRecensione) {
        String filePath = "data/recensioni.json";
        try {
            ArrayList<Recensione> tutteLeRecensioni = Gestione.Deserializer.fromJsonFile(filePath, Recensione.class, new Recensione.RecensioneDeserializer());
            if (tutteLeRecensioni == null) return false;

            // Rimuove sia la recensione che ha questo ID, sia qualsiasi recensione
            // che lo abbia come idRecensionePadre (cioè la sua risposta).
            boolean removed = tutteLeRecensioni.removeIf(r -> r.idRecensione.equals(idRecensione) || r.idRecensionePadre.equals(idRecensione));

            if (removed) {
                Type listType = new TypeToken<ArrayList<Recensione>>(){}.getType();
                Gestione.Serializer.toJsonFile(filePath, tutteLeRecensioni, listType);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Errore durante l'eliminazione della recensione: " + e.getMessage());
        }
        return false;
    }
    /**
     * Aggiunge un ristorante all'elenco dei preferiti dell'utente.
     * <p>
     * L'operazione legge la mappa dei preferiti, aggiunge l'ID del ristorante alla lista
     * dell'utente corrente (se non già presente), riordina e salva nuovamente il file.
     * </p>
     * @param r Il {@link Ristorante} da aggiungere ai preferiti.
     * @return {@code true} se il ristorante è stato aggiunto con successo, {@code false} se era già presente.
     */
    public boolean AggiungiPreferiti(Ristorante r) {
        HashMap<Integer, ArrayList<Integer>> preferiti = DeserializePreferiti();
        ArrayList<Integer> lista = preferiti.getOrDefault(this.idUtente, new ArrayList<>());
        if (!lista.contains(r.idRistorante)) {
            lista.add(r.idRistorante);
            preferiti.put(this.idUtente, lista);
            preferiti = OrdinaPreferiti(preferiti);
            SerializePreferiti(preferiti);
            return true;
        }
        return false;
    }

    /**
     * Rimuove un ristorante dall'elenco dei preferiti dell'utente.
     * @param r Il {@link Ristorante} da rimuovere.
     * @return {@code true} se il ristorante è stato rimosso, {@code false} se non era nella lista o si è verificato un errore.
     */
    public boolean RimuoviPreferito(Ristorante r) {
        HashMap<Integer, ArrayList<Integer>> preferiti = DeserializePreferiti();
        ArrayList<Integer> lista = preferiti.get(this.idUtente);
        if (lista != null && lista.contains(r.idRistorante)) {
            lista.remove(Integer.valueOf(r.idRistorante));
            if (lista.isEmpty()) {
                preferiti.remove(this.idUtente);
            } else {
                preferiti.put(this.idUtente, lista);
            }
            SerializePreferiti(preferiti);
            return true;
        }
        return false;
    }

    /**
     * Recupera la mappa completa di tutti i preferiti, con chiave l'ID utente e valore la lista degli ID dei ristoranti preferiti.
     * @return Una {@code HashMap<Integer, ArrayList<Integer>>} che rappresenta i preferiti di tutti gli utenti.
     */
    public HashMap<Integer, ArrayList<Integer>> VisualizzaPreferiti() {
        return DeserializePreferiti();
    }

    /**
     * Controlla se un dato ristorante è tra i preferiti di un utente specifico.
     * @param idUtente L'ID dell'utente da controllare.
     * @param idRistorante L'ID del ristorante da cercare.
     * @param preferiti La mappa dei preferiti in cui effettuare la ricerca.
     * @return {@code true} se il ristorante è tra i preferiti dell'utente, {@code false} altrimenti.
     */
    boolean VerificaPreferiti(int idUtente, int idRistorante, HashMap<Integer, ArrayList<Integer>> preferiti) {
        ArrayList<Integer> lista = preferiti.get(idUtente);
        return lista != null && lista.contains(idRistorante);
    }

    /**
     * Ordina la mappa dei preferiti.
     * <p>
     * L'ordinamento viene eseguito sia sulle chiavi della mappa (ID utente) sia sugli elementi
     * delle liste di valori (ID ristorante).
     * </p>
     * @param preferiti La mappa {@code HashMap} dei preferiti non ordinata.
     * @return Una {@code LinkedHashMap} ordinata per chiave e con le liste interne ordinate.
     */
    HashMap<Integer, ArrayList<Integer>> OrdinaPreferiti(HashMap<Integer, ArrayList<Integer>> preferiti) {
        List<Map.Entry<Integer, ArrayList<Integer>>> entries = new ArrayList<>(preferiti.entrySet());
        entries.sort(Map.Entry.comparingByKey());
        LinkedHashMap<Integer, ArrayList<Integer>> sorted = new LinkedHashMap<>();
        for (Map.Entry<Integer, ArrayList<Integer>> entry : entries) {
            ArrayList<Integer> ristoranti = new ArrayList<>(entry.getValue());
            Collections.sort(ristoranti);
            sorted.put(entry.getKey(), ristoranti);
        }
        return sorted;
    }

    /**
     * Serializza la mappa dei preferiti in un file JSON.
     * @param preferiti La mappa {@code HashMap} dei preferiti da salvare.
     */
    public void SerializePreferiti(HashMap<Integer, ArrayList<Integer>> preferiti) {
        String filePath = "data/preferiti.json";
        Type type = new TypeToken<HashMap<Integer, ArrayList<Integer>>>(){}.getType();
        Gestione.Serializer.toJsonFile(filePath, preferiti, type);
    }

    /**
     * Deserializza la mappa dei preferiti da un file JSON.
     * @return Una {@code HashMap} contenente i preferiti. Se il file non esiste o si verifica un errore,
     * restituisce una nuova {@code HashMap} vuota.
     */
    public HashMap<Integer, ArrayList<Integer>> DeserializePreferiti() {
        String filePath = "data/preferiti.json";
        Type type = new TypeToken<HashMap<Integer, ArrayList<Integer>>>(){}.getType();
        HashMap<Integer, ArrayList<Integer>> preferiti = Gestione.Deserializer.fromJsonFile(filePath, type);
        if (preferiti == null) {
            return new HashMap<>();
        }
        return preferiti;
    }

    /**
     * Restituisce l'identificatore univoco (ID) dell'utente registrato.
     * @return L'{@code Integer} che rappresenta l'ID dell'utente.
     */
    Integer getIdUtente() {
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