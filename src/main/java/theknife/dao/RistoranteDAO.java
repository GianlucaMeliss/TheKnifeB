
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

import theknife.Ristorante;
import java.util.ArrayList;

public interface RistoranteDAO {
    ArrayList<Ristorante> getAllRistoranti();
    Ristorante getRistoranteById(int id);
    ArrayList<Ristorante> cercaAvanzata(String citta, String nome, String tipoCucina, Float pMin, Float pMax, boolean delivery, boolean online, Double ratingMin);
    boolean aggiungiRistorante(Ristorante r, int idRistoratore);
    ArrayList<Ristorante> getRistorantiGestiti(int idRistoratore);
    boolean aggiungiPreferito(int idUtente, int idRistorante);
    boolean rimuoviPreferito(int idUtente, int idRistorante);
    ArrayList<Ristorante> getPreferitiUtente(int idUtente);
}