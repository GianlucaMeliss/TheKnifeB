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

import java.util.ArrayList;
import java.util.Map;

/**
 * Classe astratta che definisce le operazioni base disponibili per un utente.
 * <p>
 * Fornisce un contratto per le operazioni comuni come la ricerca di ristoranti e la visualizzazione delle recensioni,
 * che possono essere implementate in modo diverso a seconda del tipo di utente.
 * </p>
 * @author Alessandro Melnyk
 */
public abstract class OperazioniUtente {

    /**
     * Metodo astratto per visualizzare le recensioni dei ristoranti.
     * <p>
     * Le classi concrete devono implementare questo metodo per definire come
     * recuperare e presentare le recensioni.
     * </p>
     * @param listaRistoranti La lista di ristoranti di cui visualizzare le recensioni.
     * @return Un {@code ArrayList} di oggetti {@link Recensione}.
     */
    public abstract ArrayList<Recensione> visualizzaRecensioni(ArrayList<Ristorante> listaRistoranti);

    /**
     * Cerca i ristoranti in base a una serie di criteri specificati.
     * <p>
     * Il metodo itera su una lista di ristoranti e restituisce solo quelli che
     * soddisfano tutti i criteri di ricerca forniti. La ricerca per nome, città e nazione non è case-sensitive.
     * </p>
     * @param listaRistoranti La lista completa di ristoranti da cui effettuare la ricerca.
     * @param criteri Una {@code Map} dove la chiave è un {@link CriterioRicerca} e il valore è la stringa da cercare.
     * @return Un {@code ArrayList} di {@link Ristorante} che corrispondono ai criteri di ricerca.
     */
    public final ArrayList<Ristorante> cercaRistorante(ArrayList<Ristorante> listaRistoranti, Map<CriterioRicerca, String> criteri) {
        ArrayList<Ristorante> risultati = new ArrayList<>();

        for (Ristorante ristorante : listaRistoranti) {
            boolean corrisponde = true;

            for (Map.Entry<CriterioRicerca, String> entry : criteri.entrySet()) {
                CriterioRicerca criterio = entry.getKey();
                String valore = entry.getValue();

                switch (criterio) {
                    case CITTA:
                        if (!ristorante.citta.equalsIgnoreCase(valore)) {
                            corrisponde = false;
                        }
                        break;
                    case NAZIONE:
                        if (!ristorante.nazione.equalsIgnoreCase(valore)) {
                            corrisponde = false;
                        }
                        break;
                    case PREZZO:
                        if (!ristorante.prezzo.equals(Float.parseFloat(valore))) {
                            corrisponde = false;
                        }
                        break;
                    case TIPO_CUCINA:
                        try {
                            TipoCucina tipo = TipoCucina.valueOf(valore.toUpperCase());
                            if (!ristorante.tipoCucina.contains(tipo)) {
                                corrisponde = false;
                            }
                        } catch (IllegalArgumentException e) {
                            corrisponde = false;
                        }
                        break;
                    case NOME:
                        if (!ristorante.nome.equalsIgnoreCase(valore)) {
                            corrisponde = false;
                        }
                        break;
                }

                if (!corrisponde) break;
            }
            if (corrisponde) {
                risultati.add(ristorante);
            }
        }
        return risultati;
    }
}