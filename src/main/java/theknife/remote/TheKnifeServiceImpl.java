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
package theknife.remote;

import theknife.*;
import theknife.dao.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class TheKnifeServiceImpl extends UnicastRemoteObject implements TheKnifeService {
    private static final long serialVersionUID = 1L;

    private final UtenteDAO utenteDAO;
    private final RistoranteDAO ristoranteDAO;
    private final RecensioneDAO recensioneDAO;

    public TheKnifeServiceImpl() throws RemoteException {
        super();
        this.utenteDAO = new UtenteDAOImpl();
        this.ristoranteDAO = new RistoranteDAOImpl();
        this.recensioneDAO = new RecensioneDAOImpl();
    }

    @Override
    public Utente login(String username, String password) throws RemoteException {
        return utenteDAO.eseguiLogin(username, password);
    }

    @Override
    public boolean registraUtente(Utente utente) throws RemoteException {
        return utenteDAO.registraUtente(utente);
    }

    @Override
    public ArrayList<Ristorante> getAllRistoranti() throws RemoteException {
        return ristoranteDAO.getAllRistoranti();
    }

    @Override
    public ArrayList<Ristorante> cercaRistorantiAvanzata(String citta, String nome, String tipoCucina,
                                                         Float prezzoMin, Float prezzoMax,
                                                         boolean delivery, boolean online,
                                                         Double ratingMin) throws RemoteException {
        return ristoranteDAO.cercaAvanzata(citta, nome, tipoCucina, prezzoMin, prezzoMax, delivery, online, ratingMin);
    }

    @Override
    public Ristorante getRistoranteById(int id) throws RemoteException {
        return ristoranteDAO.getRistoranteById(id);
    }

    @Override
    public boolean aggiungiRecensione(Recensione recensione) throws RemoteException {
        return recensioneDAO.aggiungiRecensione(recensione);
    }

    @Override
    public boolean modificaRecensione(int idRecensione, Recensione recensione) throws RemoteException {
        return recensioneDAO.modificaRecensione(idRecensione, recensione);
    }

    @Override
    public boolean eliminaRecensione(int idRecensione) throws RemoteException {
        return recensioneDAO.eliminaRecensione(idRecensione);
    }

    @Override
    public ArrayList<Recensione> getRecensioniByRistorante(int idRistorante) throws RemoteException {
        return recensioneDAO.getRecensioniByRistorante(idRistorante);
    }

    @Override
    public ArrayList<Recensione> getRecensioniByUtente(int idUtente) throws RemoteException {
        return recensioneDAO.getRecensioniByUtente(idUtente);
    }

    @Override
    public double[] getStatisticheRistorante(int idRistorante) throws RemoteException {
        return recensioneDAO.getStatisticheRistorante(idRistorante);
    }

    @Override
    public boolean aggiungiPreferito(int idUtente, int idRistorante) throws RemoteException {
        return ristoranteDAO.aggiungiPreferito(idUtente, idRistorante);
    }

    @Override
    public boolean rimuoviPreferito(int idUtente, int idRistorante) throws RemoteException {
        return ristoranteDAO.rimuoviPreferito(idUtente, idRistorante);
    }

    @Override
    public ArrayList<Ristorante> getPreferitiUtente(int idUtente) throws RemoteException {
        return ristoranteDAO.getPreferitiUtente(idUtente);
    }

    @Override
    public ArrayList<Ristorante> getRistorantiGestiti(int idRistoratore) throws RemoteException {
        return ristoranteDAO.getRistorantiGestiti(idRistoratore);
    }

    @Override
    public boolean aggiungiRistorante(Ristorante ristorante, int idRistoratore) throws RemoteException {
        return ristoranteDAO.aggiungiRistorante(ristorante, idRistoratore);
    }
}