package theknife.dao;

import theknife.Utente;

public interface UtenteDAO {
    // Metodo per il login
    Utente eseguiLogin(String username, String passwordCifrata);

    // Metodo per registrare un utente
    boolean registraUtente(Utente utente);
}