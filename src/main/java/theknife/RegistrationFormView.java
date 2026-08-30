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
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;

/**
 * Gestisce la vista del modulo di registrazione per i nuovi utenti.
 * <p>
 * Questa schermata presenta un form per raccogliere i dati necessari alla creazione
 * di un nuovo account, come nome, cognome, username, password e ruolo.
 * La logica di salvataggio viene eseguita in background per non bloccare l'interfaccia.
 * </p>
 * @author Simone Zamberletti
 */
public class RegistrationFormView {
    private final MainApp mainApp;
    private final BorderPane view;
    private TextField nameField, surnameField, usernameField, locationField;
    private PasswordField passwordField;
    private DatePicker birthDatePicker;
    private ComboBox<String> roleCombo;
    private Button btnRegister;

    /**
     * Costruttore della vista di registrazione.
     * @param mainApp Il riferimento all'applicazione principale, necessario per la navigazione.
     */
    public RegistrationFormView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.view = new BorderPane();
        createUI();
    }

    /**
     * Costruisce e assembla l'interfaccia utente (UI) principale di questa vista.
     */
    private void createUI() {
        Label title = new Label("Registrazione Utente");
        title.getStyleClass().add("label-title");

        GridPane formGrid = createFormGrid();

        HBox buttonsBox = new HBox(20);
        buttonsBox.setPadding(new Insets(20, 0, 0, 0));
        buttonsBox.setAlignment(Pos.CENTER);

        btnRegister = new Button("Registrati");
        btnRegister.getStyleClass().add("button-primary");
        btnRegister.setOnAction(e -> handleRegister());

        Button btnCancel = new Button("Annulla");
        btnCancel.setOnAction(e -> mainApp.showStartMenu());

        buttonsBox.getChildren().addAll(btnRegister, btnCancel);

        VBox container = new VBox();
        container.setAlignment(Pos.TOP_CENTER);
        container.getChildren().addAll(title, formGrid, buttonsBox);
        container.setPadding(new Insets(20));
        view.setCenter(container);
    }

    /**
     * Crea e configura il {@code GridPane} che contiene tutti i campi del modulo di registrazione.
     * @return Il {@code GridPane} popolato con i componenti del form.
     */
    private GridPane createFormGrid() {
        GridPane formGrid = new GridPane();
        formGrid.setPadding(new Insets(20));
        formGrid.setHgap(15);
        formGrid.setVgap(12);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setMaxWidth(450);

        int row = 0;
        nameField = new TextField(); nameField.setPromptText("Inserisci il nome");
        formGrid.add(new Label("Nome*:"), 0, row); formGrid.add(nameField, 1, row++);
        surnameField = new TextField(); surnameField.setPromptText("Inserisci il cognome");
        formGrid.add(new Label("Cognome*:"), 0, row); formGrid.add(surnameField, 1, row++);
        usernameField = new TextField(); usernameField.setPromptText("Scegli uno username");
        formGrid.add(new Label("Username*:"), 0, row); formGrid.add(usernameField, 1, row++);
        passwordField = new PasswordField(); passwordField.setPromptText("Inserisci la password");
        formGrid.add(new Label("Password*:"), 0, row); formGrid.add(passwordField, 1, row++);
        locationField = new TextField(); locationField.setPromptText("Inserisci il luogo di domicilio");
        formGrid.add(new Label("Luogo domicilio*:"), 0, row); formGrid.add(locationField, 1, row++);
        birthDatePicker = new DatePicker();
        formGrid.add(new Label("Data di nascita:"), 0, row); formGrid.add(birthDatePicker, 1, row++);

        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(Ruolo.CLIENTE.toString(), Ruolo.RISTORATORE.toString());
        roleCombo.getSelectionModel().selectFirst();
        formGrid.add(new Label("Ruolo*:"), 0, row); formGrid.add(roleCombo, 1, row++);
        return formGrid;
    }

    /**
     * Gestisce l'evento di click sul pulsante "Registrati".
     * <p>
     * Esegue la validazione dei dati inseriti e, se validi, avvia un {@code Task}
     * in background che delega alla logica del backend il compito di creare e salvare
     * il nuovo utente nel file JSON.
     * </p>
     */
    /**
     * Gestisce l'evento di click sul pulsante "Registrati".
     * <p>
     * Esegue la validazione dei dati inseriti e, se validi, avvia un {@code Task}
     * in background che delega alla logica del backend il compito di creare e salvare
     * il nuovo utente nel file JSON.
     * </p>
     */
    private void handleRegister() {
        // Validazione preliminare per campi vuoti
        if (nameField.getText().trim().isEmpty() || surnameField.getText().trim().isEmpty() ||
                usernameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty() ||
                locationField.getText().trim().isEmpty() || roleCombo.getValue() == null) {
            mainApp.showError("Compila tutti i campi obbligatori (*)");
            return;
        }

        LocalDate birthDate = birthDatePicker.getValue();
        if (birthDate == null) {
            mainApp.showError("Seleziona una data di nascita.");
            return;
        }

        btnRegister.setDisable(true);
        btnRegister.setText("Registrazione in corso...");

        // Utilizza un Task per eseguire l'operazione di salvataggio su un thread separato
        Task<Boolean> registrationTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Chiama il metodo statico del backend.
                // Questa chiamata si occupa di tutte le operazioni complesse:
                // validazione avanzata, cifratura password, controllo duplicati e scrittura su file.
                return UtenteNonRegistrato.Registrazione(
                        roleCombo.getValue(),
                        nameField.getText(),
                        surnameField.getText(),
                        usernameField.getText(),
                        passwordField.getText(),
                        birthDate.toString(),
                        locationField.getText()
                );
            }
        };

        // Gestisce il risultato del Task una volta completato con successo
        registrationTask.setOnSucceeded(e -> {
            boolean success = registrationTask.getValue();
            if(success) {
                mainApp.showInfo("Registrazione completata con successo! Ora puoi effettuare il login.");
                svuotaCampi(); // <--- AGGIUNTO QUI: Pulisce il form dopo il successo
                mainApp.showLoginView();
            } else {
                mainApp.showError("Registrazione fallita. L'username potrebbe essere già in uso. Riprova.");
                btnRegister.setDisable(false);
                btnRegister.setText("Registrati");
            }
        });

        // Gestisce un eventuale fallimento del Task (es. errore di I/O)
        registrationTask.setOnFailed(e -> {
            mainApp.showError("Errore critico durante la registrazione: " + registrationTask.getException().getMessage());
            btnRegister.setDisable(false);
            btnRegister.setText("Registrati");
        });

        new Thread(registrationTask).start();
    }

    /**
     * Restituisce il nodo radice di questa vista per l'inserimento nel layout principale.
     * @return Il {@code BorderPane} che contiene l'intera interfaccia di questa vista.
     */
    public BorderPane getView() { return view; }
    /**
     * Svuota tutti i campi del form e ripristina il bottone.
     */
    public void svuotaCampi() {
        nameField.clear();
        surnameField.clear();
        usernameField.clear();
        passwordField.clear();
        locationField.clear();
        birthDatePicker.setValue(null);
        roleCombo.getSelectionModel().selectFirst();
        btnRegister.setDisable(false);
        btnRegister.setText("Registrati");
    }
}