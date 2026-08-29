package theknife.dao;

import theknife.Utente;

public interface UtenteDAO {
    Utente eseguiLogin(String username, String passwordCifrata);
    boolean registraUtente(Utente utente);
}