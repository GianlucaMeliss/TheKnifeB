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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * Gestisce la vista principale per un utente cliente dopo aver effettuato l'accesso.
 * <p>
 * Questa classe agisce come un contenitore che utilizza un {@code TabPane} per organizzare
 * le diverse sezioni a disposizione del cliente: la dashboard personale ({@link ClientDashboardView})
 * e la schermata di ricerca ristoranti ({@link GuestSearchView}).
 * </p>
 * @author Simone Zamberletti
 */
public class LoggedInClientView {

    private final BorderPane view;
    private final ClientDashboardView clientDashboard;
    private final TabPane tabPane;
    private final Tab searchTab;

    /**
     * Costruttore della vista per il cliente loggato.
     * <p>
     * Assembla i due pannelli principali (Dashboard e Ricerca) all'interno di un TabPane.
     * Riceve un'istanza condivisa di {@code GuestSearchView} per mantenere lo stato della ricerca.
     * </p>
     * @param mainApp Il riferimento all'applicazione principale per la comunicazione.
     * @param searchView L'istanza condivisa della vista di ricerca.
     */
    public LoggedInClientView(MainApp mainApp, GuestSearchView searchView) {
        this.view = new BorderPane();
        this.clientDashboard = new ClientDashboardView(mainApp);
        this.tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab dashboardTab = new Tab("La Mia Dashboard");
        dashboardTab.setContent(clientDashboard.getView());

        this.searchTab = new Tab("Cerca Ristoranti");
        searchTab.setContent(searchView.getView());

        tabPane.getTabs().addAll(dashboardTab, searchTab);
        view.setCenter(tabPane);
    }

    /**
     * Metodo "ponte" che inoltra la richiesta di aggiornamento dei preferiti alla dashboard.
     * <p>
     * Viene chiamato da {@code MainApp} quando lo stato dei preferiti cambia in un'altra
     * parte dell'applicazione, per garantire la coerenza dei dati.
     * </p>
     */
    public void refreshDashboardFavorites() {
        clientDashboard.refreshFavorites();
    }

    /**
     * Inoltra l'oggetto utente loggato alla dashboard per il caricamento dei dati.
     * @param user L'oggetto {@link UtenteRegistrato} che ha effettuato l'accesso.
     */
    public void setUser(UtenteRegistrato user) {
        clientDashboard.setUser(user);
    }

    /**
     * Seleziona programmaticamente la scheda (Tab) di ricerca.
     * <p>
     * Utile per la logica di navigazione, ad esempio quando il pulsante "Indietro"
     * deve riportare l'utente alla schermata di ricerca.
     * </p>
     */
    public void selectSearchTab() {
        tabPane.getSelectionModel().select(searchTab);
    }

    /**
     * Restituisce il nodo radice di questa vista per l'inserimento nel layout principale.
     * @return Il {@code BorderPane} che contiene l'intera interfaccia di questa vista.
     */
    public BorderPane getView() {
        return view;
    }
}