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

import theknife.client.RmiClientManager;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.concurrent.Task;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Classe principale dell'applicazione TheKnife.
 * <p>
 * Gestisce la finestra principale (Stage), la navigazione tra le diverse viste (Scene),
 * e lo stato globale dell'applicazione, come l'utente attualmente loggato.
 * Funge da controllore centrale che coordina le interazioni tra le varie schermate.
 * </p>
 * @author Simone Zamberletti
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Utente currentUser = null;

    private MenuItem menuLogin;
    private MenuItem menuLogout;
    private Menu menuFile;

    /**
     * Enum per tracciare la schermata di provenienza quando si apre la vista di dettaglio di un ristorante.
     * <p>Serve a gestire correttamente il comportamento del pulsante "Indietro".</p>
     */
    public enum ViewOrigin { SEARCH, FAVORITES }
    private ViewOrigin lastRestaurantListOrigin = ViewOrigin.SEARCH;

    private StartMenuView startMenuView;
    private LoginView loginView;
    private RegistrationFormView registrationFormView;
    private GuestSearchView sharedGuestSearchView;
    private RestaurantDetailView restaurantDetailView;
    private LoggedInClientView loggedInClientView;
    private LoggedInRestaurateurView loggedInRestaurateurView;

    /**
     * Metodo di avvio principale dell'applicazione JavaFX.
     * @param primaryStage Lo {@code Stage} principale fornito dal framework.
     * */

    @Override
    public void start(Stage primaryStage) {
        // Tenta la connessione al server RMI all'avvio
        boolean connected = RmiClientManager.getInstance().connect();

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("TheKnife");
        initRootLayout();

        if (!connected) {
            // Se la connessione fallisce, avvisa l'utente ma permetti l'apertura (per sola consultazione se possibile)
            showError("Attenzione: Impossibile connettersi al server remoto.\nVerifica la connessione e riprova.");
        }

        showStartMenu();
        updateMenuVisibility();
    }

    /**
     * Mostra una notifica temporanea (toast) che scompare automaticamente.
     * <p>
     * Crea una nuova finestra senza bordi che contiene un messaggio e la visualizza
     * per 3 secondi prima di chiuderla.
     * </p>
     * @param message Il messaggio da visualizzare nella notifica.
     */

    public void showTemporaryInfo(String message) {
        Stage notificationStage = new Stage();
        notificationStage.initOwner(primaryStage);
        notificationStage.initStyle(StageStyle.TRANSPARENT);

        Label label = new Label(message);
        label.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 6;");

        StackPane root = new StackPane(label);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        notificationStage.setScene(scene);
        notificationStage.show();

        notificationStage.setX(primaryStage.getX() + (primaryStage.getWidth() - notificationStage.getWidth()) / 2);
        notificationStage.setY(primaryStage.getY() + primaryStage.getHeight() - notificationStage.getHeight() - 100);

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> notificationStage.close());
        delay.play();
    }

    /**
     * Inizializza il layout di base dell'applicazione.
     * <p>
     * Imposta la {@code MenuBar} in alto e prepara la scena principale con il foglio di stile.
     * </p>
     */
    private void initRootLayout() {
        rootLayout = new BorderPane();
        MenuBar menuBar = createMenuBar();
        rootLayout.setTop(menuBar);
        Scene scene = new Scene(rootLayout, 1100, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Costruisce e configura la {@code MenuBar} principale.
     * @return La {@code MenuBar} configurata con tutte le sue voci.
     */

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuFile = new Menu("Menu");
        MenuItem menuStart = new MenuItem("Home");
        menuLogin = new MenuItem("Login");
        menuLogout = new MenuItem("Logout");
        MenuItem menuRegister = new MenuItem("Registrazione");
        MenuItem menuGuest = new MenuItem("Accesso Guest");
        MenuItem menuExit = new MenuItem("Esci");
        menuStart.setOnAction(e -> showStartMenu());
        menuLogin.setOnAction(e -> showLoginView());
        menuLogout.setOnAction(e -> logout());
        menuRegister.setOnAction(e -> showRegistrationForm());
        menuGuest.setOnAction(e -> showGuestSearch(null));
        menuExit.setOnAction(e -> primaryStage.close());
        menuFile.getItems().addAll(menuStart, new SeparatorMenuItem(), menuRegister, new SeparatorMenuItem(), menuGuest, new SeparatorMenuItem(), menuExit);
        menuBar.getMenus().add(menuFile);
        return menuBar;
    }

    /**
     * Aggiorna dinamicamente la visibilità delle voci Login e Logout nel menu.
     * <p>
     * Aggiunge o rimuove le voci dal menu a seconda che un utente sia loggato o meno,
     * garantendo che venga mostrata solo l'opzione pertinente.
     * </p>
     */
    private void updateMenuVisibility() {
        boolean isLoggedIn = (currentUser != null);
        menuFile.getItems().remove(menuLogin);
        menuFile.getItems().remove(menuLogout);
        if (isLoggedIn) {
            menuFile.getItems().add(2, menuLogout);
        } else {
            menuFile.getItems().add(2, menuLogin);
        }
    }

    /**
     * Assicura che esista una sola istanza della {@code GuestSearchView}.
     * <p>
     * Questo metodo implementa un pattern Singleton per la vista di ricerca,
     * permettendo di conservare lo stato dei filtri durante la navigazione.
     * </p>
     */
    private void ensureGuestSearchViewExists() {
        if (sharedGuestSearchView == null) {
            sharedGuestSearchView = new GuestSearchView(this);
        }
    }

    /**
     * Gestisce il processo di login di un utente.
     * @param user L'oggetto {@link Utente} che ha superato l'autenticazione.
     */
    public void loginAs(Utente user) {
        this.currentUser = user;
        ensureGuestSearchViewExists();
        sharedGuestSearchView.resetView();

        // --- INIZIO NUOVA AGGIUNTA ---
        // Prende il domicilio dall'utente appena loggato e fa partire in automatico la ricerca
        if (user != null && user.getDomicilio() != null) {
            sharedGuestSearchView.setLocation(user.getDomicilio());
        }
        // --- FINE NUOVA AGGIUNTA ---

        if (user.ruolo == Ruolo.CLIENTE) {
            showLoggedInClientView();
        } else if (user.ruolo == Ruolo.RISTORATORE) {
            showLoggedInRestaurateurView();
        }
        updateMenuVisibility();
    }

    /**
     * Gestisce il processo di logout dell'utente.
     */
    public void logout() {
        this.currentUser = null;
        loggedInClientView = null;
        loggedInRestaurateurView = null;
        showInfo("Logout effettuato con successo.");
        showStartMenu();
        updateMenuVisibility();
    }

    /**
     * Gestisce la navigazione "indietro" dalla vista di dettaglio di un ristorante.
     * <p>
     * Utilizza la variabile {@code lastRestaurantListOrigin} per determinare se
     * tornare alla schermata di ricerca o alla dashboard dei preferiti.
     * </p>
     */
    public void goBackToPreviousView() {
        switch (lastRestaurantListOrigin) {
            case FAVORITES:
                if (currentUser != null && currentUser.ruolo == Ruolo.CLIENTE) {
                    showLoggedInClientView();
                }
                break;
            case SEARCH:
            default:
                if (currentUser != null && currentUser.ruolo == Ruolo.CLIENTE) {
                    showLoggedInClientView();
                    loggedInClientView.selectSearchTab();
                } else if (currentUser != null && currentUser.ruolo == Ruolo.RISTORATORE) {
                    showLoggedInRestaurateurView();
                    loggedInRestaurateurView.selectSearchTab();
                } else {
                    showGuestSearch(null);
                }
                break;
        }
    }

    /**
     * Aggiunge un ristorante ai preferiti dell'utente corrente.
     * @param ristorante Il {@link Ristorante} da aggiungere.
     */
    public void addRestaurantToFavorites(Ristorante ristorante) {
        if (currentUser instanceof UtenteRegistrato) {
            ((UtenteRegistrato) currentUser).AggiungiPreferiti(ristorante);
        } else {
            showError("Devi essere loggato come cliente per aggiungere preferiti.");
        }
    }

    /**
     * Rimuove un ristorante dai preferiti e aggiorna la dashboard.
     * @param ristorante Il {@link Ristorante} da rimuovere.
     */
    public void removeRestaurantFromFavorites(Ristorante ristorante) {
        if (currentUser instanceof UtenteRegistrato) {
            if (((UtenteRegistrato) currentUser).RimuoviPreferito(ristorante)) {
                showTemporaryInfo("'" + ristorante.nome + "' rimosso dai preferiti.");
                if (loggedInClientView != null) {
                    loggedInClientView.refreshDashboardFavorites();
                }
            } else {
                showError("Errore durante la rimozione del preferito.");
            }
        }
    }

    /**
     * Verifica se un ristorante è tra i preferiti dell'utente corrente.
     * @param ristorante Il {@link Ristorante} da verificare.
     * @return {@code true} se è un preferito, {@code false} altrimenti.
     */
    public boolean isCurrentRestaurantFavorite(Ristorante ristorante) {
        if (currentUser instanceof UtenteRegistrato) {
            HashMap<Integer, ArrayList<Integer>> preferiti = ((UtenteRegistrato) currentUser).VisualizzaPreferiti();
            return ((UtenteRegistrato) currentUser).VerificaPreferiti(currentUser.idUtente, ristorante.idRistorante, preferiti);
        }
        return false;
    }

    /**
     * Restituisce il ruolo dell'utente attualmente loggato.
     * @return Il {@link Ruolo} dell'utente, o {@code null} se nessuno è loggato.
     */
    public Ruolo getCurrentUserRole() {
        return currentUser != null ? currentUser.ruolo : null;
    }

    /**
     * Salva una nuova recensione in background e esegue un'azione al completamento.
     * @param newReview La nuova {@link Recensione} da salvare.
     * @param onSuccessCallback Un'azione {@code Runnable} da eseguire dopo il salvataggio.
     */
    public void saveNewReview(Recensione newReview, Runnable onSuccessCallback) {
        if (currentUser instanceof UtenteRegistrato) {
            newReview.fkIdUtente = currentUser.idUtente;
            Task<Boolean> saveTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return ((UtenteRegistrato) currentUser).aggiungiRecensione(newReview);
                }
            };
            saveTask.setOnSucceeded(e -> {
                if (saveTask.getValue()) {
                    showTemporaryInfo("Recensione aggiunta con successo!");
                    if (onSuccessCallback != null) {
                        onSuccessCallback.run();
                    }
                } else {
                    showError("Errore nel salvataggio della recensione.");
                }
            });
            saveTask.setOnFailed(e -> showError("Errore critico: " + saveTask.getException().getMessage()));
            new Thread(saveTask).start();
        }
    }

    /**
     * Mostra la vista iniziale (menu di avvio).
     */
    public void showStartMenu() {
        if (startMenuView == null) startMenuView = new StartMenuView(this);
        if(sharedGuestSearchView != null) {
            sharedGuestSearchView.resetView();
        }
        rootLayout.setCenter(startMenuView.getView());
    }

    /**
     * Mostra la vista di login.
     */
    public void showLoginView() {
        if (loginView == null) loginView = new LoginView(this);
        loginView.clearFieldsAndReset();
        rootLayout.setCenter(loginView.getView());
    }

    /**
     * Mostra la vista per la registrazione di un nuovo utente.
     */
    public void showRegistrationForm() {
        if (registrationFormView == null) registrationFormView = new RegistrationFormView(this);
        rootLayout.setCenter(registrationFormView.getView());
    }

    /**
     * Mostra la vista di ricerca per un utente non registrato (guest).
     * @param predefLocation Una località da pre-impostare nel campo di ricerca.
     */
    public void showGuestSearch(String predefLocation) {
        this.currentUser = null;
        updateMenuVisibility();
        ensureGuestSearchViewExists();
        if (predefLocation != null) {
            sharedGuestSearchView.setLocation(predefLocation);
        }
        rootLayout.setCenter(sharedGuestSearchView.getView());
    }

    /**
     * Mostra la vista di dettaglio per un ristorante specifico.
     * @param ristorante Il {@link Ristorante} da visualizzare.
     * @param origin La vista di provenienza, per gestire il tasto "Indietro".
     */
    public void showRestaurantDetail(Ristorante ristorante, ViewOrigin origin) {
        this.lastRestaurantListOrigin = origin;
        if (restaurantDetailView == null) restaurantDetailView = new RestaurantDetailView(this);
        Ruolo ruoloAttuale = getCurrentUserRole();
        restaurantDetailView.setRestaurant(ristorante, ruoloAttuale);
        rootLayout.setCenter(restaurantDetailView.getView());
    }

    /**
     * Mostra la dashboard per un utente cliente loggato.
     */
    public void showLoggedInClientView() {
        ensureGuestSearchViewExists();
        if (loggedInClientView == null) {
            loggedInClientView = new LoggedInClientView(this, sharedGuestSearchView);
        }
        if (currentUser instanceof UtenteRegistrato) {
            loggedInClientView.setUser((UtenteRegistrato) currentUser);
        }
        rootLayout.setCenter(loggedInClientView.getView());
    }

    /**
     * Mostra la dashboard per un utente ristoratore loggato.
     */
    public void showLoggedInRestaurateurView() {
        ensureGuestSearchViewExists();
        if (loggedInRestaurateurView == null) {
            loggedInRestaurateurView = new LoggedInRestaurateurView(this, sharedGuestSearchView);
        }
        if (currentUser instanceof Ristoratore) {
            loggedInRestaurateurView.setUser((Ristoratore) currentUser);
        }
        rootLayout.setCenter(loggedInRestaurateurView.getView());
    }

    /**
     * Mostra un dialogo di errore modale.
     * @param msg Il messaggio di errore da visualizzare.
     */
    public void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Mostra un dialogo informativo modale.
     * @param msg Il messaggio informativo da visualizzare.
     */
    public void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Metodo main dell'applicazione.
     * @param args Argomenti passati da riga di comando.
     */
    public static void main(String[] args) {
        launch(args);
    }
}