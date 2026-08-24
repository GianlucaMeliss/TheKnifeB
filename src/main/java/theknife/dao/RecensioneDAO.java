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

package theknife.dao;



import theknife.Recensione;
import java.util.ArrayList;

public interface RecensioneDAO {
    boolean aggiungiRecensione(Recensione r);
    boolean modificaRecensione(int idRecensione, Recensione r);
    boolean eliminaRecensione(int idRecensione);
    ArrayList<Recensione> getRecensioniByRistorante(int idRistorante);
    ArrayList<Recensione> getRecensioniByUtente(int idUtente);
    double[] getStatisticheRistorante(int idRistorante);
}