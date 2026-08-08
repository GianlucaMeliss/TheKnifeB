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
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;

/**
 * Gestisce la vista di login dell'applicazione.
 * <p>
 * Questa schermata fornisce all'utente i campi per inserire username e password
 * e gestisce il processo di autenticazione in modo asincrono per non bloccare
 * l'interfaccia utente.
 * </p>
 * @author Simone Zamberletti
 */
public class LoginView {
    private final MainApp mainApp;
    private final BorderPane view;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    /**
     * Costruttore della vista di login.
     * @param mainApp Il riferimento all'applicazione principale, necessario per la navigazione.
     */
    public LoginView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.view = new BorderPane();
        createUI();
    }

    /**
     * Resetta la vista al suo stato iniziale.
     * <p>
     * Svuota i campi di testo e ripristina lo stato del pulsante di login.
     * Viene chiamato prima di mostrare la vista per garantire una schermata pulita,
     * specialmente dopo un logout.
     * </p>
     */
    public void clearFieldsAndReset() {
        usernameField.clear();
        passwordField.clear();
        loginButton.setDisable(false);
        loginButton.setText("Accedi");
    }

    /**
     * Costruisce e assembla i componenti grafici che compongono la vista di login.
     */
    private void createUI() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(40));
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(400);

        Label title = new Label("Accedi a TheKnife");
        title.getStyleClass().add("label-title");

        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);
        formGrid.setHgap(10);
        formGrid.setAlignment(Pos.CENTER);

        usernameField = new TextField();
        usernameField.setPromptText("Inserisci il tuo username");
        formGrid.add(new Label("Username:"), 0, 0);
        formGrid.add(usernameField, 1, 0);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        formGrid.add(new Label("Password:"), 0, 1);
        formGrid.add(passwordField, 1, 1);

        loginButton = new Button("Accedi");
        loginButton.getStyleClass().add("button-primary");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> handleLogin());

        Button cancelButton = new Button("Torna alla Home");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setOnAction(e -> mainApp.showStartMenu());

        container.getChildren().addAll(title, formGrid, loginButton, cancelButton);
        view.setCenter(container);
    }

    /**
     * Gestisce l'evento di click sul pulsante di login.
     * <p>
     * Esegue una validazione iniziale dei campi e poi avvia un {@code Task} in background
     * per verificare le credenziali dell'utente senza bloccare l'interfaccia.
     * </p>
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mainApp.showError("Username e Password sono obbligatori.");
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("Accesso in corso...");

        Task<Utente> loginTask = new Task<>() {
            @Override
            protected Utente call() throws Exception {
                // Eseguito su un thread separato per non bloccare la UI
                ArrayList<Utente> utenti = Gestione.Deserializer.fromJsonFile(
                        "data/utenti.json",
                        Utente.class,
                        new Utente.UtenteDeserializer()
                );

                // Scansiona la lista utenti alla ricerca di una corrispondenza
                for (Utente utente : utenti) {
                    if (utente.username.equals(username)) {
                        // Se l'utente viene trovato, decifra la password e la confronta
                        String decryptedPassword = Gestione.CifraturaUtils.decripta(utente.password);
                        if (decryptedPassword.equals(password)) {
                            return utente; // Successo: restituisce l'oggetto Utente
                        }
                    }
                }
                return null; // Fallimento: utente non trovato o password errata
            }
        };

        // Gestisce il caso in cui il Task termina con successo
        loginTask.setOnSucceeded(e -> {
            Utente loggedInUser = loginTask.getValue();
            if (loggedInUser != null) {
                // Comunica all'app principale che il login è avvenuto con successo
                mainApp.loginAs(loggedInUser);
            } else {
                mainApp.showError("Credenziali non valide. Riprova.");
                // Se il login fallisce, ripristina il pulsante
                loginButton.setDisable(false);
                loginButton.setText("Accedi");
            }
        });

        // Gestisce il caso in cui il Task fallisce a causa di un'eccezione
        loginTask.setOnFailed(e -> {
            mainApp.showError("Errore critico durante il login: " + loginTask.getException().getMessage());
            loginButton.setDisable(false);
            loginButton.setText("Accedi");
        });

        new Thread(loginTask).start();
    }

    /**
     * Restituisce il nodo radice di questa vista per l'inserimento nel layout principale.
     * @return Il {@code BorderPane} che contiene l'intera interfaccia di questa vista.
     */
    public BorderPane getView() { return view; }
}