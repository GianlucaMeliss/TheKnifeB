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

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gestisce la vista di dettaglio di un singolo ristorante,
 * mostrando le informazioni principali (nome, cucina, città, prezzo, valutazione media, ecc.)
 * e le recensioni degli utenti con eventuali risposte del ristoratore.
 *
 * <p>Permette inoltre all'utente cliente di:
 * <ul>
 *     <li>Aggiungere o rimuovere il ristorante dai preferiti</li>
 *     <li>Scrivere una nuova recensione</li>
 * </ul>
 * </p>
 *
 * @author Gianluca Melis
 * @author Alessandro Melnyk
 * @author Davide Redemagni
 * @author Simone Zamberletti
 */
public class RestaurantDetailView {

    /** Riferimento all'applicazione principale */
    private final MainApp mainApp;

    /** Layout principale della vista */
    private final BorderPane view;

    /** Ristorante attualmente visualizzato */
    private Ristorante currentRestaurant;

    /** Etichetta con il nome del ristorante */
    private Label restaurantNameLabel;

    /** Pulsante per aggiungere/rimuovere il ristorante dai preferiti */
    private Button btnToggleFavorite;

    /** Pulsante per scrivere una nuova recensione */
    private Button btnWriteReview;

    /** Etichette per le varie informazioni del ristorante */
    private Label restaurantCuisineLabel, cityLabel, priceLabel, avgRatingLabel, deliveryLabel, reservationLabel;

    /** Lista delle recensioni e relative risposte */
    private ListView<ReviewWrapper> reviewsListView;

    /**
     * Classe interna di supporto che raggruppa una recensione
     * con la sua eventuale risposta del ristoratore.
     */
    private static class ReviewWrapper {
        /** Recensione principale scritta da un utente */
        Recensione review;
        /** Eventuale risposta del ristoratore (può essere null) */
        Recensione reply;

        /**
         * Costruisce un wrapper che associa recensione e risposta.
         * @param review recensione principale
         * @param reply risposta del ristoratore (può essere null)
         */
        ReviewWrapper(Recensione review, Recensione reply) {
            this.review = review;
            this.reply = reply;
        }
    }

    /**
     * Costruttore della vista di dettaglio.
     * @param mainApp riferimento all'applicazione principale
     */
    public RestaurantDetailView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.view = new BorderPane();
        createUI();
    }

    /**
     * Costruisce e dispone i componenti grafici della vista,
     * compresa la barra superiore, le informazioni del ristorante e la lista recensioni.
     */
    private void createUI() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Barra superiore con pulsante indietro, nome ristorante, scrittura recensione e preferiti
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("⬅ Indietro");
        backButton.setOnAction(e -> mainApp.goBackToPreviousView());

        restaurantNameLabel = new Label("Nome Ristorante");
        restaurantNameLabel.getStyleClass().add("label-title");

        btnToggleFavorite = new Button();
        btnToggleFavorite.setOnAction(e -> handleToggleFavorite());

        btnWriteReview = new Button("✍️ Scrivi una Recensione");
        btnWriteReview.getStyleClass().add("button-primary");
        btnWriteReview.setOnAction(e -> handleWriteReview());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(backButton, restaurantNameLabel, spacer, btnWriteReview, btnToggleFavorite);

        // Griglia con informazioni sul ristorante
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(10); grid.setPadding(new Insets(20));
        restaurantCuisineLabel = new Label(); cityLabel = new Label(); priceLabel = new Label();
        avgRatingLabel = new Label(); deliveryLabel = new Label(); reservationLabel = new Label();
        grid.add(new Label("Tipologia Cucina:"), 0, 0); grid.add(restaurantCuisineLabel, 1, 0);
        grid.add(new Label("Città:"), 0, 1); grid.add(cityLabel, 1, 1);
        grid.add(new Label("Prezzo Medio:"), 0, 2); grid.add(priceLabel, 1, 2);
        grid.add(new Label("Valutazione Media:"), 0, 3); grid.add(avgRatingLabel, 1, 3);
        grid.add(new Label("Delivery:"), 0, 4); grid.add(deliveryLabel, 1, 4);
        grid.add(new Label("Prenotazione Online:"), 0, 5); grid.add(reservationLabel, 1, 5);

        // Titolo recensioni
        Label reviewsTitle = new Label("Recensioni");
        reviewsTitle.getStyleClass().add("label-subtitle");

        // Lista delle recensioni e risposte
        reviewsListView = new ListView<>();
        reviewsListView.setPlaceholder(new Label("Nessuna recensione per questo ristorante."));

        // Imposta una cella personalizzata per mostrare recensione e risposta
        reviewsListView.setCellFactory(param -> new ListCell<ReviewWrapper>() {
            private VBox content = new VBox(5);
            private Label reviewLabel = new Label();
            private Label replyLabel = new Label();
            {
                replyLabel.setStyle("-fx-font-style: italic;");
                replyLabel.setPadding(new Insets(0, 0, 0, 20));
                content.getChildren().setAll(reviewLabel, replyLabel);
            }

            @Override
            protected void updateItem(ReviewWrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    reviewLabel.setText(item.review.toString());
                    if (item.reply != null) {
                        replyLabel.setText(item.reply.toString());
                        replyLabel.setVisible(true);
                        replyLabel.setManaged(true);
                    } else {
                        replyLabel.setVisible(false);
                        replyLabel.setManaged(false);
                    }
                    setGraphic(content);
                }
            }
        });

        container.getChildren().addAll(topBar, grid, reviewsTitle, reviewsListView);
        view.setCenter(container);
    }

    /**
     * Popola la vista con i dati di un ristorante specifico,
     * aggiornando etichette e lista recensioni.
     *
     * @param ristorante il ristorante da visualizzare
     * @param ruolo ruolo dell'utente corrente (serve per decidere se mostrare pulsanti cliente)
     */
    public void setRestaurant(Ristorante ristorante, Ruolo ruolo) {
        this.currentRestaurant = ristorante;
        if (ristorante == null) return;

        boolean isClient = (ruolo == Ruolo.CLIENTE);
        btnToggleFavorite.setVisible(isClient);
        btnToggleFavorite.setManaged(isClient);
        btnWriteReview.setVisible(isClient);
        btnWriteReview.setManaged(isClient);

        if (isClient) {
            boolean isAlreadyFavorite = mainApp.isCurrentRestaurantFavorite(this.currentRestaurant);
            updateFavoriteButtonState(isAlreadyFavorite);
        }

        // Carica recensioni dal sistema
        ArrayList<Recensione> allReviews = new UtenteNonRegistrato("").caricaRecensioni();
        ArrayList<Utente> allUsers = Gestione.Deserializer.fromJsonFile("data/utenti.json", Utente.class, new Utente.UtenteDeserializer());

        // Filtra recensioni del ristorante
        List<Recensione> restaurantReviewsAndReplies = allReviews.stream()
                .filter(r -> r.fkIdRistorante.equals(ristorante.idRistorante))
                .collect(Collectors.toList());

        // Separa recensioni principali da risposte
        List<Recensione> mainReviews = restaurantReviewsAndReplies.stream()
                .filter(r -> r.voto != -1)
                .collect(Collectors.toList());
        List<Recensione> replies = restaurantReviewsAndReplies.stream()
                .filter(r -> r.voto == -1)
                .collect(Collectors.toList());

        // Associa recensione ↔ risposta
        List<ReviewWrapper> reviewWrappers = new ArrayList<>();
        for (Recensione review : mainReviews) {
            String authorName = allUsers.stream()
                    .filter(u -> u.idUtente.equals(review.fkIdUtente))
                    .findFirst().map(u -> u.username).orElse("Anonimo");
            review.authorUsername = authorName;

            Recensione reply = replies.stream()
                    .filter(r -> r.idRecensionePadre.equals(review.idRecensione))
                    .findFirst().orElse(null);

            reviewWrappers.add(new ReviewWrapper(review, reply));
        }

        // Calcola valutazione media
        double avg = mainReviews.stream().mapToInt(r -> r.voto).average().orElse(0.0);
        DecimalFormat df = new DecimalFormat("#.0");
        String avgRating = df.format(avg);
        String formattedPrice = String.format("%.2f €", ristorante.prezzo);

        // Aggiorna etichette
        restaurantNameLabel.setText(ristorante.nome);
        restaurantCuisineLabel.setText(ristorante.tipoCucina.toString().replace("[", "").replace("]", ""));
        cityLabel.setText(ristorante.citta);
        priceLabel.setText(formattedPrice);
        avgRatingLabel.setText(avgRating + "/5 (" + mainReviews.size() + " recensioni)");
        deliveryLabel.setText(ristorante.consegna ? "Sì" : "No");
        reservationLabel.setText(ristorante.pren_online ? "Sì" : "No");

        // Popola la lista
        reviewsListView.setItems(FXCollections.observableArrayList(reviewWrappers));
    }

    /**
     * Apre la finestra di dialogo per scrivere una nuova recensione
     * e salva il risultato se confermato.
     */
    private void handleWriteReview() {
        if(currentRestaurant == null) return;
        ArrayList<Ristorante> preselected = new ArrayList<>();
        preselected.add(currentRestaurant);
        ReviewDialog dialog = new ReviewDialog(preselected, null);
        Optional<Recensione> result = dialog.showAndWait();
        result.ifPresent(newReview -> {
            Runnable refreshAction = () -> setRestaurant(currentRestaurant, mainApp.getCurrentUserRole());
            mainApp.saveNewReview(newReview, refreshAction);
        });
    }

    /**
     * Gestisce il click sul pulsante "preferiti",
     * aggiungendo o rimuovendo il ristorante dall'elenco.
     */
    private void handleToggleFavorite() {
        if (currentRestaurant == null) return;
        boolean isCurrentlyFavorite = mainApp.isCurrentRestaurantFavorite(currentRestaurant);
        if (isCurrentlyFavorite) {
            mainApp.removeRestaurantFromFavorites(currentRestaurant);
        } else {
            mainApp.addRestaurantToFavorites(currentRestaurant);
        }
        updateFavoriteButtonState(!isCurrentlyFavorite);
    }

    /**
     * Aggiorna lo stato del pulsante "preferiti"
     * mostrando il testo corretto e lo stile grafico.
     *
     * @param isFavorite true se il ristorante è già nei preferiti
     */
    private void updateFavoriteButtonState(boolean isFavorite) {
        if (isFavorite) {
            btnToggleFavorite.setText("💔 Rimuovi dai Preferiti");
            btnToggleFavorite.getStyleClass().remove("button-primary");
        } else {
            btnToggleFavorite.setText("❤️ Aggiungi ai Preferiti");
            btnToggleFavorite.getStyleClass().add("button-primary");
        }
        btnToggleFavorite.setDisable(false);
    }

    /**
     * Restituisce il contenitore principale della vista.
     * @return il {@link BorderPane} della vista
     */
    public BorderPane getView() { return view; }
}
