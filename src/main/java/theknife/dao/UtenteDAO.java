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

import theknife.Utente;

/**
 * Interfaccia Data Access Object (DAO) per la gestione degli utenti.
 * Definisce le operazioni di login e registrazione interfacciandosi con il database.
 *
 * @author Alessandro Melnyk
 * @author Gianluca Melis
 * @author Simone Zamberletti
 * @author Davide Redemagni
 */
public interface UtenteDAO {
    /**
     * Esegue il login di un utente verificando le credenziali sul database.
     *
     * @param username lo username dell'utente
     * @param passwordCifrata la password cifrata dell'utente
     * @return l'oggetto {@link Utente} (Cliente o Ristoratore) se le credenziali sono corrette, null altrimenti
     */
    Utente eseguiLogin(String username, String passwordCifrata);

    /**
     * Registra un nuovo utente nel database.
     *
     * @param utente l'oggetto {@link Utente} contenente i dati da registrare
     * @return true se la registrazione è andata a buon fine, false altrimenti
     */
    boolean registraUtente(Utente utente);
}