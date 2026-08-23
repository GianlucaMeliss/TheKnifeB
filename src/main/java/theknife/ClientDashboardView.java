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
import theknife.remote.TheKnifeService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gestisce la vista della dashboard per un utente cliente loggato.
 * <p>
 * Questa schermata è composta da due pannelli a schede (Tab): uno per visualizzare
 * i ristoranti preferiti dell'utente e uno per visualizzare e gestire le proprie recensioni.
 * Carica i dati in modo asincrono per mantenere l'interfaccia reattiva.
 * </p>
 * @author Simone Zamberletti
 */
public class ClientDashboardView {

    private final MainApp mainApp;
    private final BorderPane view;
    private UtenteRegistrato currentUser;
    private ObservableList<Ristorante> favoriteRestaurants;
    private ObservableList<Recensione> userReviews;
    private ListView<Ristorante> favoritesListView;
    private TreeView<Object> reviewsTreeView;
    private ProgressIndicator loadingIndicator;

    /**
     * Classe interna statica utilizzata come contenitore di dati.
     * <p>
     * Il suo scopo è permettere al Task in background di restituire risultati multipli
     * (in questo caso, due liste diverse) in un unico oggetto.
     * </p>
     */
    private static class DashboardData {
        final ObservableList<Ristorante> favorites;
        final ObservableList<Recensione> reviews;
        DashboardData(ObservableList<Ristorante> f, ObservableList<Recensione> r) { this.favorites = f; this.reviews = r; }
    }

    /**
     * Costruttore della vista ClientDashboardView.
     * @param mainApp Il riferimento all'applicazione principale, necessario per la navigazione e la comunicazione.
     */
    public ClientDashboardView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.view = new BorderPane();
        createUI();
    }

    /**
     * Imposta l'utente per cui visualizzare la dashboard e avvia il caricamento dei dati.
     * <p>
     * Questo metodo è il punto di ingresso per popolare la vista con le informazioni
     * corrette dopo che un utente ha effettuato il login.
     * </p>
     * @param user L'oggetto {@link UtenteRegistrato} che ha effettuato l'accesso.
     */
    public void setUser(UtenteRegistrato user) {
        this.currentUser = user;
        loadRealData();
    }

    /**
     * Ricarica in modo asincrono la sola lista dei ristoranti preferiti.
     * <p>
     * Viene invocato da {@code MainApp} quando lo stato dei preferiti viene modificato
     * da un'altra vista (es. {@code RestaurantDetailView}), per garantire la coerenza dei dati.
     * </p>
     */
    public void refreshFavorites() {
        if (currentUser == null) return;
        Task<ObservableList<Ristorante>> refreshTask = new Task<>() {
            @Override
            protected ObservableList<Ristorante> call() throws Exception {
                TheKnifeService service = RmiClientManager.getInstance().getService();
                if (service == null) throw new Exception("Servizio RMI non disponibile.");

                ArrayList<Ristorante> preferiti = service.getPreferitiUtente(currentUser.idUtente);
                return FXCollections.observableArrayList(preferiti);
            }
        };
        refreshTask.setOnSucceeded(e -> {
            favoriteRestaurants = refreshTask.getValue();
            favoritesListView.setItems(favoriteRestaurants);
        });
        refreshTask.setOnFailed(e -> mainApp.showError("Errore durante l'aggiornamento dei preferiti: " + refreshTask.getException().getMessage()));
        new Thread(refreshTask).start();
    }

    /**
     * Costruisce l'interfaccia utente (UI) principale di questa vista.
     * <p>
     * Assembla il titolo, il contenitore a schede (TabPane) e l'indicatore di caricamento.
     * </p>
     */
    private void createUI() {
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        Label title = new Label("Dashboard Cliente");
        title.getStyleClass().add("label-title");
        title.setPadding(new Insets(20));
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab favoritesTab = new Tab("Ristoranti Preferiti", createFavoritesPane());
        Tab reviewsTab = new Tab("Le mie Recensioni", createReviewsPane());
        tabPane.getTabs().addAll(favoritesTab, reviewsTab);
        VBox container = new VBox(10, title, tabPane);
        container.setPadding(new Insets(10));
        StackPane stackPane = new StackPane(container, loadingIndicator);
        view.setCenter(stackPane);
    }

    /**
     * Costruisce il pannello (pane) per la scheda "Ristoranti Preferiti".
     * @return Un oggetto {@code VBox} contenente l'interfaccia della scheda dei preferiti.
     */
    private VBox createFavoritesPane() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        favoritesListView = new ListView<>();
        favoritesListView.setPlaceholder(new Label("Non hai ancora aggiunto ristoranti ai preferiti."));
        favoritesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mainApp.showRestaurantDetail(newVal, MainApp.ViewOrigin.FAVORITES);
                Platform.runLater(() -> favoritesListView.getSelectionModel().clearSelection());
            }
        });
        favoritesListView.setCellFactory(param -> new ListCell<Ristorante>() {
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
        box.getChildren().addAll(new Label("I tuoi ristoranti preferiti:"), favoritesListView);
        return box;
    }

    /**
     * Carica i dati dell'utente (preferiti e recensioni) in un Task in background.
     * <p>
     * Questa operazione viene eseguita su un thread separato per non bloccare l'interfaccia
     * utente durante la lettura e l'elaborazione dei file JSON.
     * </p>
     */
    private void loadRealData() {
        loadingIndicator.setVisible(true);
        Task<DashboardData> loadDataTask = new Task<>() {
            @Override
            protected DashboardData call() throws Exception {
                TheKnifeService service = RmiClientManager.getInstance().getService();
                if (service == null) throw new Exception("Servizio RMI non disponibile.");

                // Carica i preferiti dal server
                ArrayList<Ristorante> favList = service.getPreferitiUtente(currentUser.idUtente);
                ObservableList<Ristorante> favs = FXCollections.observableArrayList(favList);

                // Carica le recensioni dell'utente dal server
                ArrayList<Recensione> myRevList = service.getRecensioniByUtente(currentUser.idUtente);
                ObservableList<Recensione> myrevs = FXCollections.observableArrayList(myRevList);

                return new DashboardData(favs, myrevs);
            }
        };
        loadDataTask.setOnSucceeded(e -> {
            DashboardData res = loadDataTask.getValue();
            favoriteRestaurants = res.favorites;
            userReviews = res.reviews;
            favoritesListView.setItems(favoriteRestaurants);
            buildReviewTree();
            loadingIndicator.setVisible(false);
        });
        loadDataTask.setOnFailed(e -> {
            mainApp.showError("Impossibile caricare dati: " + loadDataTask.getException().getMessage());
            loadingIndicator.setVisible(false);
        });
        new Thread(loadDataTask).start();
    }

    /**
     * Costruisce il pannello (pane) per la scheda "Le mie Recensioni".
     * @return Un oggetto {@code VBox} contenente l'interfaccia della scheda delle recensioni.
     */
    private VBox createReviewsPane() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        reviewsTreeView = new TreeView<>();
        reviewsTreeView.setShowRoot(false);
        Button btnAddReview = new Button("Aggiungi Recensione");
        btnAddReview.getStyleClass().add("button-primary");
        btnAddReview.setOnAction(e -> showAddReviewDialog());
        Button btnEditReview = new Button("Modifica Recensione");
        btnEditReview.setOnAction(e -> showEditReviewDialog());
        Button btnDeleteReview = new Button("Elimina Recensione");
        btnDeleteReview.setOnAction(e -> deleteSelectedReview());
        HBox buttons = new HBox(10, btnAddReview, btnEditReview, btnDeleteReview);
        box.getChildren().addAll(new Label("Le tue recensioni (raggruppate per ristorante):"), reviewsTreeView, buttons);
        return box;
    }

    /**
     * Costruisce la struttura ad albero (TreeView) delle recensioni dell'utente.
     * <p>
     * Prende la lista piatta di recensioni e la organizza in modo gerarchico,
     * raggruppando le recensioni sotto il nome del rispettivo ristorante.
     * </p>
     */
    private void buildReviewTree() {
        if (userReviews == null || userReviews.isEmpty()) {
            reviewsTreeView.setRoot(null);
            return;
        }
        TreeItem<Object> root = new TreeItem<>();
        Map<Integer, TreeItem<Object>> nodes = new HashMap<>();

        for (Recensione rev : userReviews) {
            // Utilizziamo il nome del ristorante se già presente nell'oggetto (popolato dal server)
            String rName = (rev.restaurantName != null) ? rev.restaurantName : "Ristorante #" + rev.fkIdRistorante;

            nodes.putIfAbsent(rev.fkIdRistorante, new TreeItem<>(rName));
            nodes.get(rev.fkIdRistorante).getChildren().add(new TreeItem<>(rev));
        }
        root.getChildren().setAll(nodes.values());
        reviewsTreeView.setRoot(root);
    }

    /**
     * Gestisce l'azione del pulsante per eliminare la recensione selezionata.
     * <p>
     * Mostra un dialogo di conferma prima di procedere con l'eliminazione effettiva,
     * che viene eseguita in un Task in background.
     * </p>
     */
    private void deleteSelectedReview() {
        TreeItem<Object> selectedItem = reviewsTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !(selectedItem.getValue() instanceof Recensione)) {
            mainApp.showError("Seleziona una recensione da eliminare.");
            return;
        }
        Recensione selectedReview = (Recensione) selectedItem.getValue();
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler eliminare questa recensione?", ButtonType.YES, ButtonType.NO);
        confirmation.setHeaderText("Conferma eliminazione");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            Task<Boolean> deleteTask = new Task<>() {
                @Override
                protected Boolean call() {
                    return currentUser.eliminaRecensione(selectedReview.idRecensione);
                }
            };
            deleteTask.setOnSucceeded(e -> {
                if (deleteTask.getValue()) {
                    mainApp.showTemporaryInfo("Recensione eliminata.");
                    userReviews.remove(selectedReview);
                    buildReviewTree();
                } else {
                    mainApp.showError("Errore durante l'eliminazione.");
                }
            });
            new Thread(deleteTask).start();
        }
    }

    /**
     * Gestisce l'azione del pulsante per modificare la recensione selezionata.
     */
    private void showEditReviewDialog() {
        loadingIndicator.setVisible(true);
        Task<ArrayList<Ristorante>> loadRestTask = new Task<>() {
            @Override
            protected ArrayList<Ristorante> call() throws Exception {
                TheKnifeService service = RmiClientManager.getInstance().getService();
                if (service == null) throw new Exception("Servizio RMI non disponibile.");
                return service.getAllRistoranti();
            }
        };

        loadRestTask.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            ArrayList<Ristorante> allRestaurants = loadRestTask.getValue();

            TreeItem<Object> selectedItem = reviewsTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null || !(selectedItem.getValue() instanceof Recensione)) {
                mainApp.showError("Seleziona una recensione da modificare.");
                return;
            }
            Recensione selectedReview = (Recensione) selectedItem.getValue();
            ReviewDialog dialog = new ReviewDialog(allRestaurants, selectedReview);
            Optional<Recensione> result = dialog.showAndWait();
            result.ifPresent(editedReview -> {
                editedReview.fkIdUtente = currentUser.getIdUtente();
                editedReview.idRecensione = selectedReview.idRecensione;
                Task<Boolean> updateTask = new Task<>() {
                    @Override
                    protected Boolean call() {
                        return currentUser.modificaRecensione(selectedReview.idRecensione, editedReview);
                    }
                };
                updateTask.setOnSucceeded(ev -> {
                    if (updateTask.getValue()) {
                        mainApp.showTemporaryInfo("Recensione modificata.");
                        int index = userReviews.indexOf(selectedReview);
                        if (index != -1) {
                            userReviews.set(index, editedReview);
                            buildReviewTree();
                        }
                    } else {
                        mainApp.showError("Errore durante la modifica.");
                    }
                });
                new Thread(updateTask).start();
            });
        });

        loadRestTask.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            mainApp.showError("Errore nel caricamento dei ristoranti dal server.");
        });

        Thread t = new Thread(loadRestTask);
        t.start();
    }

    /**
     * Gestisce l'azione del pulsante per aggiungere una nuova recensione.
     */
    private void showAddReviewDialog() {
        loadingIndicator.setVisible(true);
        Task<ArrayList<Ristorante>> loadRestTask = new Task<>() {
            @Override
            protected ArrayList<Ristorante> call() throws Exception {
                TheKnifeService service = RmiClientManager.getInstance().getService();
                if (service == null) throw new Exception("Servizio RMI non disponibile.");
                return service.getAllRistoranti();
            }
        };

        loadRestTask.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            ArrayList<Ristorante> allRestaurants = loadRestTask.getValue();
            if (allRestaurants == null || allRestaurants.isEmpty()) {
                mainApp.showError("Nessun ristorante disponibile da recensire.");
                return;
            }
            ReviewDialog dialog = new ReviewDialog(allRestaurants, null);
            Optional<Recensione> result = dialog.showAndWait();
            result.ifPresent(newReview -> {
                newReview.fkIdUtente = currentUser.getIdUtente();
                Task<Boolean> saveTask = new Task<>() {
                    @Override
                    protected Boolean call() {
                        return currentUser.aggiungiRecensione(newReview);
                    }
                };
                saveTask.setOnSucceeded(ev -> {
                    if (saveTask.getValue()) {
                        mainApp.showTemporaryInfo("Recensione aggiunta!");
                        userReviews.add(newReview);
                        buildReviewTree();
                    } else {
                        mainApp.showError("Errore nel salvataggio.");
                    }
                });
                saveTask.setOnFailed(ev -> mainApp.showError("Errore critico: " + saveTask.getException().getMessage()));
                new Thread(saveTask).start();
            });
        });

        loadRestTask.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            mainApp.showError("Errore nel caricamento dei ristoranti dal server.");
        });

        Thread t = new Thread(loadRestTask);
        t.start();
    }

    /**
     * Restituisce il nodo radice (BorderPane) di questa vista per essere inserito nel layout principale.
     * @return Il {@code BorderPane} della vista.
     */
    public BorderPane getView() {
        return view;
    }
}