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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Gestisce la vista del menu di avvio dell'applicazione.
 * <p>
 * Questa è la schermata iniziale mostrata a tutti gli utenti (anche non loggati).
 * Permette di accedere alla ricerca dei ristoranti, di effettuare il login
 * o di aprire il modulo di registrazione.
 * </p>
 * @author Simone Zamberletti
 */
public class StartMenuView {

    private final MainApp mainApp;
    private final BorderPane view;
    private TextField locationField;

    /**
     * Costruttore della vista StartMenuView.
     * @param mainApp Il riferimento all'applicazione principale.
     */
    public StartMenuView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.view = new BorderPane();
        createUI();
    }

    /**
     * Inizializza l'interfaccia utente.
     */
    private void createUI() {
        VBox container = new VBox(25);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.CENTER);

        Label title = new Label("Benvenuto in TheKnife");
        title.getStyleClass().add("label-title");

        Button btnLogin = new Button("Login");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.getStyleClass().add("button-primary");
        btnLogin.setOnAction(e -> mainApp.showLoginView());

        Button btnRegister = new Button("Registrazione");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.getStyleClass().add("button-primary");
        btnRegister.setOnAction(e -> mainApp.showRegistrationForm());

        Label guestLabel = new Label("Accesso come ospite (guest):");
        guestLabel.getStyleClass().add("label-subtitle");

        locationField = new TextField();
        locationField.setPromptText("Inserisci la tua località (obbligatorio)");
        locationField.setMaxWidth(300);

        Button btnGuest = new Button("Entra come Guest");
        btnGuest.setMaxWidth(Double.MAX_VALUE);
        btnGuest.getStyleClass().add("button-primary");
        btnGuest.setOnAction(e -> handleGuestAccess());

        VBox guestBox = new VBox(10, guestLabel, locationField, btnGuest);
        guestBox.setAlignment(Pos.CENTER);
        guestBox.setMaxWidth(320);

        container.getChildren().addAll(title, btnLogin, btnRegister, new Separator(), guestBox);

        view.setCenter(container);
    }

    /**
     * Gestisce l'evento di click sul pulsante "Entra come Guest".
     * <p>
     * Recupera la località inserita, verifica che non sia vuota e, in caso positivo,
     * instrada l'utente alla schermata di ricerca pre-impostando la località scelta.
     * </p>
     */
    private void handleGuestAccess() {
        String location = locationField.getText().trim();
        if (location.isEmpty()) {
            mainApp.showError("Il campo località è obbligatorio per l'accesso come ospite.");
            return;
        }
        mainApp.showGuestSearch(location);
    }

    /**
     * Restituisce il nodo radice di questa vista per l'inserimento nel layout principale.
     * @return Il {@code BorderPane} che contiene l'intera interfaccia di questa vista.
     */
    public BorderPane getView() { return view; } 
}