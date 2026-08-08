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
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

/**
 * Classe che rappresenta l'utente ristoratore, estensione della classe {@link Utente}.
 * <p>
 * Fornisce metodi per:
 * <ul>
 *     <li>Aggiungere un ristorante e associarlo al ristoratore</li>
 *     <li>Rispondere alle recensioni</li>
 *     <li>Scaricare la lista dei ristoranti gestiti</li>
 *     <li>Visualizzare le recensioni relative ai propri ristoranti</li>
 * </ul>
 * </p>
 *
 * @author Gianluca
 * @author Simone
 */
public class Ristoratore extends Utente {

    /**
     * Classe interna di supporto per mantenere l’associazione tra utente e ristorante.
     */
    private static class GestioneRistorante {
        Integer fkIdUtente;
        Integer fkIdRistorante;

        /**
         * Costruttore della classe di gestione.
         * @param fkIdUtente ID dell’utente ristoratore.
         * @param fkIdRistorante ID del ristorante associato.
         */
        GestioneRistorante(Integer fkIdUtente, Integer fkIdRistorante) {
            this.fkIdUtente = fkIdUtente;
            this.fkIdRistorante = fkIdRistorante;
        }
    }

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
     * Aggiunge un nuovo ristorante al sistema e lo associa al ristoratore corrente.
     * <p>
     * Il metodo legge la lista esistente di ristoranti, aggiunge quello nuovo e
     * salva la lista aggiornata, prevenendo sovrascritture dei dati.
     * Inoltre aggiorna il file di gestione {@code gestioneRistoranti.json}
     * per collegare il ristorante al ristoratore.
     * </p>
     * @param rist L'oggetto {@link Ristorante} da aggiungere.
     * @return {@code true} se l'aggiunta ha avuto successo, {@code false} altrimenti.
     */
    public boolean AggiungiRistorante(Ristorante rist) {
        String filePath = "data/ristoranti.json";

        ArrayList<Ristorante> listaRistoranti = Gestione.Deserializer.fromJsonFile(
                filePath, Ristorante.class, new Ristorante.RistoranteDeserializer());
        if (listaRistoranti == null) {
            listaRistoranti = new ArrayList<>();
        }

        for (Ristorante r : listaRistoranti) {
            if (r.nome.equalsIgnoreCase(rist.nome) && r.indirizzo.equalsIgnoreCase(rist.indirizzo)) {
                return false; // Ristorante duplicato
            }
        }

        int nextId = listaRistoranti.stream().mapToInt(r -> r.idRistorante).max().orElse(0) + 1;
        rist.idRistorante = nextId;

        listaRistoranti.add(rist);

        Type listType = new TypeToken<ArrayList<Ristorante>>(){}.getType();
        Gestione.Serializer.toJsonFile(filePath, listaRistoranti, listType);

        try {
            String gestioneFilePath = "data/gestioneRistoranti.json";
            Type gestioneListType = new TypeToken<ArrayList<GestioneRistorante>>(){}.getType();

            ArrayList<GestioneRistorante> gestioneList =
                    Gestione.Deserializer.fromJsonFile(gestioneFilePath, gestioneListType);
            if (gestioneList == null) {
                gestioneList = new ArrayList<>();
            }

            gestioneList.add(new GestioneRistorante(this.idUtente, rist.idRistorante));
            Gestione.Serializer.toJsonFile(gestioneFilePath, gestioneList, gestioneListType);

        } catch (Exception e) {
            System.err.println("Errore durante l'aggiornamento di gestioneRistoranti.json: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Invia una risposta a una recensione.
     * <p>
     * Il metodo legge tutte le recensioni, assegna un nuovo ID alla risposta,
     * la associa all’utente corrente e salva la lista aggiornata.
     * </p>
     * @param r La {@link Recensione} che rappresenta la risposta.
     * @return {@code true} se la risposta è stata salvata con successo, {@code false} altrimenti.
     */
    public boolean RispondiRecensione(Recensione r) {
        String filePath = "data/recensioni.json";
        try {
            ArrayList<Recensione> tutteLeRecensioni = Gestione.Deserializer.fromJsonFile(
                    filePath, Recensione.class, new Recensione.RecensioneDeserializer());
            if (tutteLeRecensioni == null) {
                tutteLeRecensioni = new ArrayList<>();
            }
            int nextId = tutteLeRecensioni.stream().mapToInt(rev -> rev.idRecensione).max().orElse(0) + 1;
            r.idRecensione = nextId;
            r.fkIdUtente = this.idUtente;
            tutteLeRecensioni.add(r);
            Type listType = new TypeToken<ArrayList<Recensione>>(){}.getType();
            Gestione.Serializer.toJsonFile(filePath, tutteLeRecensioni, listType);
        } catch (Exception e) {
            System.err.println("Errore durante la scrittura della risposta: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Scarica la lista degli ID dei ristoranti posseduti dal ristoratore.
     * <p>
     * Il metodo legge il file {@code gestioneRistoranti.json} e filtra i ristoranti
     * associati all’utente corrente.
     * </p>
     * @return Lista di ID dei ristoranti posseduti.
     */
    public ArrayList<Integer> scaricaRistorantiPosseduti() {
        String filePath = "data/gestioneRistoranti.json";
        ArrayList<Integer> idRistoranti = new ArrayList<>();
        try {
            JsonArray jsonArray = JsonParser.parseReader(new FileReader(filePath)).getAsJsonArray();
            for (JsonElement el : jsonArray) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.get("fkIdUtente").getAsInt() == this.idUtente) {
                    idRistoranti.add(obj.get("fkIdRistorante").getAsInt());
                }
            }
        } catch (Exception e) {
            System.err.println("Errore lettura gestioneRistoranti.json: " + e.getMessage());
        }
        return idRistoranti;
    }

    /**
     * Visualizza tutte le recensioni relative ai ristoranti gestiti dal ristoratore.
     * <p>
     * Il metodo legge il file {@code recensioni.json}, filtra le recensioni per i ristoranti
     * posseduti dall’utente e le restituisce sotto forma di lista.
     * </p>
     * @param listaRistoranti Lista di {@link Ristorante} per cui visualizzare le recensioni.
     * @return Lista di recensioni dei ristoranti gestiti.
     */
    @Override
    public ArrayList<Recensione> visualizzaRecensioni(ArrayList<Ristorante> listaRistoranti) {
        String filePath = "data/recensioni.json";
        ArrayList<Integer> listaRistorantiPosseduti = scaricaRistorantiPosseduti();
        ArrayList<Recensione> lista = new ArrayList<>();
        try {
            JsonArray jsonArray = JsonParser.parseReader(new FileReader(filePath)).getAsJsonArray();
            Integer idRisoranteJson;
            Integer idUtenteJson;
            int votoJson;
            String commentoJson;
            LocalDate dataJsonJson;
            Integer idRecensioneJson;
            Integer idRecensionePadreJson;
            for (JsonElement el : jsonArray) {
                JsonObject obj = el.getAsJsonObject();
                idRisoranteJson = obj.get("fkIdRistorante").getAsInt();
                idRecensionePadreJson = obj.get("idRecensionePadre").getAsInt();
                if(!listaRistorantiPosseduti.contains(idRisoranteJson) && idRecensionePadreJson != -1)
                    continue;
                idUtenteJson = obj.get("fkIdUtente").getAsInt();
                votoJson = obj.get("voto").getAsInt();
                commentoJson = obj.get("commento").getAsString();
                dataJsonJson = LocalDate.parse(obj.get("data").getAsString());
                idRecensioneJson = obj.get("idRecensione").getAsInt();
                Recensione rec = new Recensione(idRisoranteJson, idUtenteJson, votoJson,
                        commentoJson, dataJsonJson, idRecensioneJson, idRecensionePadreJson);
                lista.add(rec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
