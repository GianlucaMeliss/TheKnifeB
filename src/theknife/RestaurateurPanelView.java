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

    import theknife.client.RmiClientManager;
import theknife.remote.TheKnifeService;
import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.concurrent.Task;
    import javafx.geometry.Insets;
    import javafx.geometry.Pos;
    import javafx.scene.Node;
    import javafx.scene.control.*;
    import javafx.scene.layout.*;
    import javafx.scene.text.Font;
    import javafx.scene.text.FontWeight;
    import java.text.DecimalFormat;
    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Optional;
    import java.util.stream.Collectors;

    /**
     * Gestisce la vista del pannello di controllo per un utente ristoratore.
     * <p>
     * Questa schermata è strutturata con uno {@code SplitPane} che divide la vista in due:
     * a sinistra la lista dei ristoranti di proprietà del ristoratore, a destra
     * le recensioni relative al ristorante selezionato e gli strumenti per rispondervi.
     * </p>
     * @author Simone Zamberletti
     */
    public class RestaurateurPanelView {

        private final MainApp mainApp;
        private final BorderPane view;
        private Ristoratore currentUser;

        private ObservableList<Ristorante> myRestaurants;
        private ListView<Ristorante> restaurantsListView;
        private ListView<Recensione> reviewsListView;
        private TextArea replyTextArea;
        private Button btnReply;

        /**
         * Costruttore della vista del pannello ristoratore.
         * @param mainApp Il riferimento all'applicazione principale.
         */
        public RestaurateurPanelView(MainApp mainApp) {
            this.mainApp = mainApp;
            this.view = new BorderPane();
            createUI();
        }

        /**
         * Imposta l'utente ristoratore per cui visualizzare il pannello e avvia il caricamento dei suoi dati.
         * @param user L'oggetto {@link Ristoratore} che ha effettuato l'accesso.
         */
        public void setUser(Ristoratore user) {
            this.currentUser = user;
            loadRealData();
        }

        /**
         * Crea il pannello sinistro dello {@code SplitPane}.
         * @return Un {@code VBox} contenente la lista dei ristoranti del ristoratore.
         */
        private VBox createLeftPane() {
            VBox leftPane = new VBox(10);
            leftPane.setPadding(new Insets(10));
            Label lblRestaurants = new Label("I Tuoi Ristoranti");
            lblRestaurants.getStyleClass().add("label-subtitle");
            restaurantsListView = new ListView<>();
            restaurantsListView.setPlaceholder(new Label("Non hai ancora registrato nessun ristorante."));

            // --- NUOVA VISUALIZZAZIONE GRAFICA (IDENTICA AGLI ALTRI PANNELLI) ---
            restaurantsListView.setCellFactory(param -> new ListCell<Ristorante>() {
                private VBox content = new VBox(5);
                private Label nameLabel = new Label();
                private Label detailsLabel = new Label();
                private Label ratingLabel = new Label();
                private Label servicesLabel = new Label();

                {
                    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
                    content.getChildren().addAll(nameLabel, ratingLabel, detailsLabel, servicesLabel);
                    content.setPadding(new Insets(5, 10, 5, 10));
                }

                @Override
                protected void updateItem(Ristorante item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        nameLabel.setText(item.nome + " (" + item.citta + ")");

                        String cuisine = item.tipoCucina.toString().replace("[", "").replace("]", "");
                        detailsLabel.setText(cuisine);

                    try {
                        TheKnifeService service = RmiClientManager.getInstance().getService();
                        if (service != null) {
                            double[] stats = service.getStatisticheRistorante(item.idRistorante);
                            double avgRating = stats[0];
                            long reviewCount = (long) stats[1];
                            DecimalFormat df = new DecimalFormat("#.0");
                            ratingLabel.setText("Valutazione: " + df.format(avgRating) + "/5 (" + reviewCount + " recensioni)");
                        } else {
                            ratingLabel.setText("Valutazione: N/D");
                        }
                    } catch (Exception e) {
                        ratingLabel.setText("Valutazione: Errore");
                    }

                        String price = String.format("Prezzo Medio: %.2f€", item.prezzo);
                        String delivery = item.consegna ? "✓ Delivery" : "✗ Delivery";
                        String reservation = item.pren_online ? "✓ Prenotazione Online" : "✗ Prenotazione Online";
                        servicesLabel.setText(price + "  |  " + delivery + "  |  " + reservation);

                        setGraphic(content);
                    }
                }
            });

            Button btnAddRestaurant = new Button("Aggiungi Ristorante");
            btnAddRestaurant.getStyleClass().add("button-primary");
            btnAddRestaurant.setOnAction(e -> showAddRestaurantDialog());
            leftPane.getChildren().addAll(lblRestaurants, restaurantsListView, btnAddRestaurant);
            return leftPane;
        }

        //<editor-fold desc="Codice non modificato">
        private void loadRealData() {
            Task<ObservableList<Ristorante>> loadTask = new Task<>() {
                @Override
                protected ObservableList<Ristorante> call() throws Exception {
                    if (currentUser == null) return FXCollections.emptyObservableList();
                    
                    // Recupera direttamente la lista dei ristoranti gestiti dal server
                    ArrayList<Ristorante> ownedRestaurants = currentUser.getRistorantiGestiti();
                    return FXCollections.observableArrayList(ownedRestaurants);
                }
            };
            loadTask.setOnSucceeded(e -> {
                myRestaurants = loadTask.getValue();
                restaurantsListView.setItems(myRestaurants);
            });
            loadTask.setOnFailed(e -> mainApp.showError("Errore nel caricamento dei ristoranti dal server."));
            new Thread(loadTask).start();
        }
        private void createUI() {
            Label title = new Label("Pannello Gestione Ristoranti");
            title.getStyleClass().add("label-title");
            title.setPadding(new Insets(20));
            VBox container = new VBox(10);
            container.setPadding(new Insets(10));
            container.getChildren().add(title);
            SplitPane splitPane = new SplitPane();
            splitPane.setDividerPositions(0.45);
            VBox leftPane = createLeftPane();
            VBox rightPane = createRightPane();
            splitPane.getItems().addAll(leftPane, rightPane);
            container.getChildren().add(splitPane);
            view.setCenter(container);
            restaurantsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                loadReviewsForRestaurant(newVal);
            });
            reviewsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                boolean hasReply = false;
                if (newVal != null && reviewsListView.getItems() != null) {
                    hasReply = reviewsListView.getItems().stream()
                            .anyMatch(r -> newVal.idRecensione.equals(r.idRecensionePadre));
                }
                btnReply.setDisable(newVal == null || hasReply || newVal.voto == -1);
                replyTextArea.setDisable(newVal == null || hasReply || newVal.voto == -1);
                replyTextArea.setPromptText(hasReply ? "Risposta già inviata." : "Scrivi qui la tua risposta...");
            });
        }
        private void loadReviewsForRestaurant(Ristorante restaurant) {
            if (restaurant == null) {
                reviewsListView.setItems(FXCollections.emptyObservableList());
                return;
            }
            Task<ObservableList<Recensione>> reviewsTask = new Task<>() {
                @Override
                protected ObservableList<Recensione> call() throws Exception {
                    TheKnifeService service = RmiClientManager.getInstance().getService();
                    if (service == null) throw new Exception("Servizio RMI non disponibile.");
                    
                    ArrayList<Recensione> restaurantReviews = service.getRecensioniByRistorante(restaurant.idRistorante);
                    return FXCollections.observableArrayList(restaurantReviews);
                }
            };
            reviewsTask.setOnSucceeded(e -> {
                reviewsListView.setItems(reviewsTask.getValue());
                replyTextArea.clear();
                btnReply.setDisable(true);
            });
            reviewsTask.setOnFailed(e -> mainApp.showError("Errore nel caricamento delle recensioni dal server."));
            new Thread(reviewsTask).start();
        }
        private void sendReply() {
            Recensione selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
            String replyText = replyTextArea.getText().trim();
            if (selectedReview == null) {
                mainApp.showError("Seleziona una recensione a cui rispondere.");
                return;
            }
            if (replyText.isEmpty()) {
                mainApp.showError("Il testo della risposta non può essere vuoto.");
                return;
            }
            Recensione reply = new Recensione(selectedReview.idRecensione, selectedReview.fkIdRistorante, replyText);
            Task<Boolean> replyTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return currentUser.RispondiRecensione(reply);
                }
            };
            replyTask.setOnSucceeded(e -> {
                if (replyTask.getValue()) {
                    mainApp.showInfo("Risposta inviata con successo!");
                    loadReviewsForRestaurant(restaurantsListView.getSelectionModel().getSelectedItem());
                } else {
                    mainApp.showError("Errore nel salvataggio della risposta.");
                }
            });
            replyTask.setOnFailed(e -> mainApp.showError("Errore critico durante l'invio della risposta."));
            new Thread(replyTask).start();
        }
        private void showAddRestaurantDialog() {
            AddRestaurantDialog dialog = new AddRestaurantDialog();
            Optional<Ristorante> result = dialog.showAndWait();
            result.ifPresent(newRestaurant -> {
                Task<Boolean> addTask = new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return currentUser.AggiungiRistorante(newRestaurant);
                    }
                };
                addTask.setOnSucceeded(e -> {
                    if(addTask.getValue()) {
                        mainApp.showInfo("Ristorante '" + newRestaurant.nome + "' aggiunto con successo.");
                        myRestaurants.add(newRestaurant);
                    } else {
                        mainApp.showError("Un ristorante con lo stesso nome e indirizzo esiste già.");
                    }
                });
                addTask.setOnFailed(e -> mainApp.showError("Errore critico durante l'aggiunta del ristorante."));
                new Thread(addTask).start();
            });
        }
        private VBox createRightPane() {
            VBox rightPane = new VBox(10);
            rightPane.setPadding(new Insets(10));
            Label lblReviews = new Label("Recensioni Ricevute");
            lblReviews.getStyleClass().add("label-subtitle");
            reviewsListView = new ListView<>();
            reviewsListView.setPlaceholder(new Label("Seleziona un ristorante per vedere le recensioni"));
            Label lblReply = new Label("Rispondi alla recensione selezionata:");
            replyTextArea = new TextArea();
            replyTextArea.setPrefRowCount(4);
            btnReply = new Button("Invia Risposta");
            btnReply.getStyleClass().add("button-primary");
            btnReply.setOnAction(e -> sendReply());
            btnReply.setDisable(true);
            rightPane.getChildren().addAll(lblReviews, reviewsListView, lblReply, replyTextArea, btnReply);
            return rightPane;
        }
        public BorderPane getView() {
            return view;
        }
        private static class AddRestaurantDialog extends Dialog<Ristorante> {
            private TextField nameField, addressField, cityField, nationField, priceField;
            private CheckBox deliveryCheck, reservationCheck;
            private ListView<TipoCucina> cuisineList;
            public AddRestaurantDialog() {
                setTitle("Aggiungi Nuovo Ristorante");
                setHeaderText("Compila i campi per registrare il tuo ristorante.");
                getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                GridPane grid = createGrid();
                getDialogPane().setContent(grid);
                Node okButton = getDialogPane().lookupButton(ButtonType.OK);
                okButton.setDisable(true);
                nameField.textProperty().addListener((obs, oldV, newV) -> validate(okButton));
                priceField.textProperty().addListener((obs, oldV, newV) -> validate(okButton));
                cuisineList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> validate(okButton));
                setResultConverter(button -> {
                    if (button == ButtonType.OK) {
                        try {
                            float price = Float.parseFloat(priceField.getText().trim().replace(',', '.'));
                            ArrayList<TipoCucina> selectedCuisines = new ArrayList<>(cuisineList.getSelectionModel().getSelectedItems());
                            Ristorante newRest = new Ristorante(
                                    nameField.getText().trim(),
                                    addressField.getText().trim(),
                                    cityField.getText().trim(),
                                    nationField.getText().trim(),
                                    price,
                                    selectedCuisines
                            );
                            newRest.consegna = deliveryCheck.isSelected();
                            newRest.pren_online = reservationCheck.isSelected();
                            return newRest;
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    }
                    return null;
                });
            }
            private void validate(Node okButton) {
                boolean isPriceValid;
                try {
                    Float.parseFloat(priceField.getText().trim().replace(',', '.'));
                    isPriceValid = true;
                } catch (NumberFormatException e) {
                    isPriceValid = false;
                }
                boolean allValid = !nameField.getText().trim().isEmpty() &&
                        isPriceValid &&
                        !cuisineList.getSelectionModel().getSelectedItems().isEmpty();
                okButton.setDisable(!allValid);
            }
            private GridPane createGrid() {
                GridPane grid = new GridPane();
                grid.setVgap(10); grid.setHgap(10); grid.setPadding(new Insets(20));
                nameField = new TextField(); nameField.setPromptText("Es. La Trattoria");
                addressField = new TextField(); addressField.setPromptText("Es. Via Roma 1");
                cityField = new TextField(); cityField.setPromptText("Es. Como");
                nationField = new TextField(); nationField.setPromptText("Es. Italia");
                priceField = new TextField(); priceField.setPromptText("Es. 35.50");
                deliveryCheck = new CheckBox("Offre Delivery");
                reservationCheck = new CheckBox("Offre Prenotazione Online");
                cuisineList = new ListView<>(FXCollections.observableArrayList(TipoCucina.values()));
                cuisineList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                cuisineList.setPrefHeight(100);
                grid.add(new Label("Nome*:"), 0, 0); grid.add(nameField, 1, 0);
                grid.add(new Label("Indirizzo*:"), 0, 1); grid.add(addressField, 1, 1);
                grid.add(new Label("Città*:"), 0, 2); grid.add(cityField, 1, 2);
                grid.add(new Label("Nazione*:"), 0, 3); grid.add(nationField, 1, 3);
                grid.add(new Label("Prezzo Medio (€)*:"), 0, 4); grid.add(priceField, 1, 4);
                grid.add(new Label("Tipologie Cucina*:"), 0, 5); grid.add(cuisineList, 1, 5);
                grid.add(new Label("Servizi:"), 0, 6); grid.add(new HBox(15, deliveryCheck, reservationCheck), 1, 6);
                return grid;
            }
        }
    }