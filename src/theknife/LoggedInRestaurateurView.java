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
 * Gestisce la vista principale per un utente ristoratore dopo aver effettuato l'accesso.
 * <p>
 * Similmente alla controparte cliente, questa classe agisce come un contenitore che
 * utilizza un {@code TabPane} per organizzare le diverse sezioni a disposizione del ristoratore:
 * il pannello di gestione ({@link RestaurateurPanelView}) e la schermata di ricerca
 * ristoranti ({@link GuestSearchView}).
 * </p>
 * @author Simone Zamberletti
 */
public class LoggedInRestaurateurView {

    private final BorderPane view;
    private final RestaurateurPanelView restaurateurPanel;
    private final TabPane tabPane;
    private final Tab searchTab;

    /**
     * Costruttore della vista per il ristoratore loggato.
     * <p>
     * Assembla i due pannelli principali (Pannello Gestione e Ricerca) all'interno di un TabPane.
     * Riceve un'istanza condivisa di {@code GuestSearchView} per mantenere lo stato della ricerca
     * coerente attraverso tutta l'applicazione.
     * </p>
     * @param mainApp Il riferimento all'applicazione principale per la comunicazione.
     * @param searchView L'istanza condivisa della vista di ricerca.
     */
    public LoggedInRestaurateurView(MainApp mainApp, GuestSearchView searchView) {
        this.view = new BorderPane();

        this.restaurateurPanel = new RestaurateurPanelView(mainApp);

        this.tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab panelTab = new Tab("Pannello Gestione");
        panelTab.setContent(restaurateurPanel.getView());

        this.searchTab = new Tab("Cerca Ristoranti");
        searchTab.setContent(searchView.getView());

        tabPane.getTabs().addAll(panelTab, searchTab);
        view.setCenter(tabPane);
    }

    /**
     * Inoltra l'oggetto utente loggato al pannello di gestione per il caricamento dei dati specifici.
     * @param user L'oggetto {@link Ristoratore} che ha effettuato l'accesso.
     */
    public void setUser(Ristoratore user) {
        restaurateurPanel.setUser(user);
    }

    /**
     * Seleziona programmaticamente la scheda (Tab) di ricerca.
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