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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestisce la vista per la ricerca dei ristoranti.
 * <p>
 * Questa schermata presenta una serie di filtri sulla sinistra e una lista per i risultati
 * al centro. È un componente riutilizzabile, impiegato sia per la ricerca da parte
 * di utenti non registrati (guest) sia per quella di utenti loggati.
 * </p>
 * @author Simone Zamberletti
 */

public class GuestSearchView {
    private final MainApp mainApp;
    private final BorderPane view;
    private ListView<Ristorante> restaurantList;
    private ComboBox<String> cuisineTypeCombo;
    private TextField locationField;

    // NUOVI CAMPI PER LE COORDINATE
    private TextField latField;
    private TextField lonField;

    private TextField minPriceField;
    private TextField maxPriceField;
    private CheckBox deliveryCheck;
    private CheckBox reservationCheck;
    private Slider ratingSlider;
    private ProgressIndicator loadingIndicator;

    private final Map<Integer, double[]> statsCache = new HashMap<>();

    /**
     * Costruttore della vista di ricerca.
     * @param mainApp Il riferimento all'applicazione principale, necessario per la navigazione.
     */
    public GuestSearchView(MainApp mainApp) {
        this.mainApp = mainApp;
        this.view = new BorderPane();
        createUI();
    }

    /**
     * Assembla e configura i componenti grafici che compongono questa vista.
     * <p>
     * Organizza il layout generale posizionando il titolo, il pannello dei filtri
     * e la lista dei risultati.
     * </p>
     */
    private void createUI() {
        Label title = new Label("Ricerca Ristoranti");
        title.getStyleClass().add("label-title");
        title.setPadding(new Insets(10));

        VBox filtersBox = createFiltersBox();
        restaurantList = new ListView<>();
        restaurantList.setPlaceholder(new Label("Usa i filtri e clicca 'Cerca' per trovare un ristorante"));
        restaurantList.setPrefWidth(600);

        // Applica una visualizzazione personalizzata per ogni elemento della lista
        // Applica una visualizzazione personalizzata per ogni elemento della lista
        restaurantList.setCellFactory(param -> new ListCell<Ristorante>() {
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

                    // Imposta un testo temporaneo per non far laggare la grafica
                    ratingLabel.setText("Valutazione: Caricamento...");

                    // LOGICA ANTI-LAG: Uso la Cache o un Thread in background
                    if (statsCache.containsKey(item.idRistorante)) {
                        aggiornaLabelValutazione(statsCache.get(item.idRistorante));
                    } else {
                        // Se non c'è in cache, scarica senza bloccare la grafica
                        new Thread(() -> {
                            try {
                                TheKnifeService service = RmiClientManager.getInstance().getService();
                                if (service != null) {
                                    double[] stats = service.getStatisticheRistorante(item.idRistorante);
                                    statsCache.put(item.idRistorante, stats); // Salva in cache

                                    // Aggiorna la grafica in modo sicuro
                                    Platform.runLater(() -> {
                                        // Controlla che l'utente non abbia scrollato via troppo velocemente
                                        if (getItem() != null && getItem().idRistorante == item.idRistorante) {
                                            aggiornaLabelValutazione(stats);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                Platform.runLater(() -> ratingLabel.setText("Valutazione: N/D"));
                            }
                        }).start();
                    }

                    String price = String.format("Prezzo Medio: %.2f€", item.prezzo);
                    String delivery = item.consegna ? "✓ Delivery" : "✗ Delivery";
                    String reservation = item.pren_online ? "✓ Prenotazione Online" : "✗ Prenotazione Online";
                    servicesLabel.setText(price + "  |  " + delivery + "  |  " + reservation);

                    setGraphic(content);
                }
            }

            // Metodo di supporto per formattare la label
            private void aggiornaLabelValutazione(double[] stats) {
                double avgRating = stats[0];
                long reviewCount = (long) stats[1];
                DecimalFormat df = new DecimalFormat("#.0");
                ratingLabel.setText("Valutazione: " + df.format(avgRating) + "/5 (" + reviewCount + " recensioni)");
            }
        });

        // Gestisce il click su un ristorante per visualizzarne i dettagli
        restaurantList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mainApp.showRestaurantDetail(newVal, MainApp.ViewOrigin.SEARCH);
                Platform.runLater(() -> restaurantList.getSelectionModel().clearSelection());
            }
        });

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        StackPane centerPane = new StackPane(restaurantList, loadingIndicator);

        view.setTop(title);
        view.setLeft(filtersBox);
        view.setCenter(centerPane);
    }

    /**
     * Reimposta tutti i filtri di ricerca e la lista dei risultati al loro stato iniziale.
     * <p>
     * Viene chiamato quando l'utente esegue un'azione che invalida la ricerca corrente,
     * come il login o il ritorno alla schermata Home.
     * </p>
     */
    public void resetView() {
        statsCache.clear();
        locationField.clear(); latField.clear(); lonField.clear();
        minPriceField.clear(); maxPriceField.clear();
        cuisineTypeCombo.getSelectionModel().selectFirst();
        deliveryCheck.setSelected(false); reservationCheck.setSelected(false);
        ratingSlider.setValue(0);
        restaurantList.setItems(FXCollections.emptyObservableList());
        restaurantList.setPlaceholder(new Label("Usa i filtri e clicca 'Cerca' per trovare un ristorante"));
    }

    /**
     * Costruisce il pannello dei filtri posizionato a sinistra della schermata.
     * @return Un oggetto {@code VBox} contenente tutti i controlli per i filtri.
     */
    private VBox createFiltersBox() {
        VBox filtersBox = new VBox(15);
        filtersBox.setPadding(new Insets(10));
        filtersBox.setPrefWidth(280);
        filtersBox.setStyle("-fx-background-color: #f0f0f0;");
        cuisineTypeCombo = new ComboBox<>();
        cuisineTypeCombo.getItems().add("Qualsiasi");
        Arrays.stream(TipoCucina.values())
                .map(this::formatCuisineName)
                .sorted()
                .forEach(cuisineTypeCombo.getItems()::add);
        cuisineTypeCombo.getSelectionModel().selectFirst();

        locationField = new TextField();
        locationField.setPromptText("Inserisci località");

        // --- AGGIUNTA CAMPI COORDINATE ---
        Label coordsLabel = new Label("Oppure ricerca per Coordinate:");
        latField = new TextField();
        latField.setPromptText("Lat (es. 45.46)");
        lonField = new TextField();
        lonField.setPromptText("Lon (es. 9.19)");
        HBox coordsBox = new HBox(10, latField, lonField);

        Label priceLabel = new Label("Prezzo (€):");
        minPriceField = new TextField();
        minPriceField.setPromptText("Min");
        maxPriceField = new TextField();
        maxPriceField.setPromptText("Max");
        HBox priceBox = new HBox(10, minPriceField, maxPriceField);
        deliveryCheck = new CheckBox("Disponibilità Delivery");
        reservationCheck = new CheckBox("Disponibilità Prenotazione");
        Label ratingLabel = new Label("Valutazione Minima:");
        ratingSlider = new Slider(0, 5, 0);
        ratingSlider.setShowTickLabels(true);
        ratingSlider.setShowTickMarks(true);
        ratingSlider.setMajorTickUnit(1);
        ratingSlider.setMinorTickCount(0);
        ratingSlider.setBlockIncrement(1);
        ratingSlider.setSnapToTicks(true);
        Button searchButton = new Button("Cerca");
        searchButton.setMaxWidth(Double.MAX_VALUE);
        searchButton.getStyleClass().add("button-primary");
        searchButton.setOnAction(e -> performSearch());

        // Aggiunti coordsLabel e coordsBox nell'elenco
        filtersBox.getChildren().addAll(
                new Label("Tipologia di cucina:"), cuisineTypeCombo,
                new Label("Località:"), locationField,
                coordsLabel, coordsBox,
                priceLabel, priceBox,
                deliveryCheck, reservationCheck,
                ratingLabel, ratingSlider,
                searchButton
        );
        return filtersBox;
    }

    /**
     * Metodo di utilità per formattare un nome dell'enum {@code TipoCucina} in una stringa leggibile.
     * <p>
     * Esempio: da {@code ITALIAN_CONTEMPORARY} a {@code "Italian Contemporary"}.
     * </p>
     * @param tc Il valore dell'enum da formattare.
     * @return La stringa formattata.
     */
    private String formatCuisineName(TipoCucina tc) {
        return Arrays.stream(tc.name().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    /**
     * Esegue la ricerca dei ristoranti in un Task in background per non bloccare l'interfaccia.
     * <p>
     * Applica una logica di filtraggio a più stadi: prima utilizza i criteri principali
     * (città, tipo cucina) tramite la logica del backend, poi raffina ulteriormente
     * i risultati nel frontend con i filtri rimanenti (prezzo, servizi, valutazione).
     * </p>
     */
    private void performSearch() {
        String locationText = locationField.getText().trim();
        String latStr = latField.getText().trim();
        String lonStr = lonField.getText().trim();

        boolean hasLocation = !locationText.isEmpty();
        boolean hasCoords = !latStr.isEmpty() && !lonStr.isEmpty();

        // Validazione: deve esserci o la città o le coordinate
        if (!hasLocation && !hasCoords) {
            mainApp.showError("Devi inserire una Località oppure Latitudine e Longitudine.");
            return;
        }

        final Double[] coords = new Double[2]; // [0] = lat, [1] = lon
        final String[] finalLocation = new String[1];

        // Se usa le coordinate, annulliamo la località in modo che il server usi la Formula di Haversine
        if (hasCoords) {
            try {
                coords[0] = Double.parseDouble(latStr.replace(',', '.'));
                coords[1] = Double.parseDouble(lonStr.replace(',', '.'));
                finalLocation[0] = null;
            } catch (NumberFormatException e) {
                mainApp.showError("Coordinate non valide. Usa numeri (es. 45.81).");
                return;
            }
        } else {
            finalLocation[0] = locationText;
            coords[0] = null;
            coords[1] = null;
        }

        loadingIndicator.setVisible(true);
        restaurantList.setItems(FXCollections.emptyObservableList());
        restaurantList.setPlaceholder(new Label("Ricerca in corso..."));

        Task<ObservableList<Ristorante>> searchTask = new Task<>() {
            @Override
            protected ObservableList<Ristorante> call() throws Exception {
                TheKnifeService service = RmiClientManager.getInstance().getService();
                if (service == null) throw new Exception("Servizio RMI non disponibile.");

                String tipoCucinaSelezionato = cuisineTypeCombo.getValue();
                String enumStyleCuisine = (tipoCucinaSelezionato != null && !tipoCucinaSelezionato.equals("Qualsiasi"))
                        ? tipoCucinaSelezionato.toUpperCase().replace(" ", "_") : null;

                Float minPrice = parsePrice(minPriceField.getText(), null);
                Float maxPrice = parsePrice(maxPriceField.getText(), null);
                double minRating = ratingSlider.getValue();

                // Chiamata RMI aggiornata con finalLocation[0], coords[0], coords[1]
                ArrayList<Ristorante> risultati = service.cercaRistorantiAvanzata(
                        finalLocation[0], coords[0], coords[1], null, enumStyleCuisine,
                        minPrice, maxPrice,
                        deliveryCheck.isSelected(),
                        reservationCheck.isSelected(),
                        minRating > 0 ? minRating : null
                );

                return FXCollections.observableArrayList(risultati);
            }
        };
        searchTask.setOnSucceeded(e -> {
            restaurantList.setItems(searchTask.getValue());
            loadingIndicator.setVisible(false);
            if(searchTask.getValue().isEmpty()){
                restaurantList.setPlaceholder(new Label("Nessun ristorante trovato per i criteri selezionati."));
            }
        });
        searchTask.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            mainApp.showError("Errore durante la ricerca: " + searchTask.getException().getMessage());
            restaurantList.setPlaceholder(new Label("Errore nel caricamento dei dati."));
        });
        new Thread(searchTask).start();
    }

    /**
     * Metodo di utilità per convertire in modo sicuro una stringa in un numero Float.
     * @param text La stringa da convertire.
     * @param defaultValue Il valore da restituire se la stringa non è un numero valido.
     * @return Il numero Float convertito o il valore di default.
     */
    private Float parsePrice(String text, Float defaultValue) {
        try {
            if (text == null || text.trim().isEmpty()) return defaultValue;
            return Float.parseFloat(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Permette di impostare programmaticamente il campo della località.
     * <p>
     * Utilizzato da {@code MainApp} quando un utente accede come guest dalla schermata iniziale.
     * </p>
     * @param location La stringa della località da impostare.
     */
    public void setLocation(String location) {
        if (location != null && !location.trim().isEmpty()) {
            locationField.setText(location);
            performSearch();
        }
    }

    /**
     * Restituisce il nodo radice di questa vista per l'inserimento nel layout principale.
     * @return Il {@code BorderPane} che contiene l'intera interfaccia di questa vista.
     */
    public BorderPane getView() {
        return view;
    }
}