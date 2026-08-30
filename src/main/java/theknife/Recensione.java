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

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Classe che rappresenta una recensione su un ristorante.
 * Una recensione può essere una recensione principale (scritta da un utente)
 * o una risposta (scritta da un ristoratore, con riferimento a una recensione padre).
 * Contiene informazioni sull'autore, sul ristorante recensito, sul voto assegnato,
 * sul commento e sulla data.
 * Implementa {@link Serializable} per la trasmissione RMI.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public class Recensione implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Identificatore univoco della recensione. */
    public Integer idRecensione;

    /** ID del ristorante recensito. */
    public Integer fkIdRistorante = -1;

    /** ID dell’utente che ha scritto la recensione. */
    public Integer fkIdUtente;

    /**
     * ID della recensione a cui si sta rispondendo, se presente.
     * Se -1, indica che è una recensione principale scritta da un cliente.
     */
    public Integer idRecensionePadre = -1;

    /** Voto assegnato al ristorante (da 1 a 5 tipicamente). */
    public int voto = -1;

    /** Commento testuale lasciato dall’utente. */
    public String commento;

    /** Data in cui la recensione è stata scritta. */
    public LocalDate data;

    /** Username dell'autore per la UI. */
    public String authorUsername;

    /** Nome del ristorante per la UI. */
    public String restaurantName;

    /**
     * Costruttore completo della recensione.
     * @param fkIdRistorante ID del ristorante recensito
     * @param fkIdUtente ID dell'utente autore
     * @param voto Voto assegnato (1-5), -1 se risposta
     * @param commento Testo della recensione
     * @param data Data di inserimento
     * @param idRecensione ID univoco dal database
     * @param idRecensionePadre ID della recensione a cui si risponde, -1 se principale
     */
    public Recensione(Integer fkIdRistorante, Integer fkIdUtente, int voto, String commento, LocalDate data,
                      Integer idRecensione, Integer idRecensionePadre ) {
        this.fkIdRistorante = fkIdRistorante;
        this.fkIdUtente = fkIdUtente;
        this.voto = voto;
        this.commento = commento;
        this.data = data;
        this.idRecensione = idRecensione;
        this.idRecensionePadre = idRecensionePadre;
    }

    /**
     * Costruttore per recensioni principali (senza risposta).
     * @param fkIdRistorante ID del ristorante recensito
     * @param fkIdUtente ID dell'utente autore
     * @param voto Voto assegnato (1-5)
     * @param commento Testo della recensione
     * @param data Data di inserimento
     */
    public Recensione(Integer fkIdRistorante, Integer fkIdUtente, int voto, String commento, LocalDate data) {
        this(fkIdRistorante,fkIdUtente,voto,commento,data,null,-1);
    }

    /**
     * Costruttore per recensioni risposta con solo commento e ID della recensione padre.
     * @param idRecensione ID della recensione padre
     * @param fkIdRistorante ID del ristorante
     * @param commento Testo della risposta
     */
    public Recensione(Integer idRecensione, Integer fkIdRistorante,String commento) {
        this(fkIdRistorante,-1,-1,commento,LocalDate.now(),-1,idRecensione);
    }

    /**
     * Restituisce una rappresentazione testuale semplificata della recensione.
     * @return stringa con commento, utente e ID recensione
     */
    @Override
    public String toString() {
        if (voto == -1) {
            return "Risposta: \"" + commento + "\"";
        }

        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stars.append(i < voto ? '★' : '☆');
        }

        String author = (authorUsername != null && !authorUsername.isEmpty()) ? " (" + authorUsername + ")" : "";
        return stars.toString() + author + ": \"" + commento + "\"";
    }
}
