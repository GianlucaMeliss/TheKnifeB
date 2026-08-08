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
    private TextField minPriceField;
    private TextField maxPriceField;
    private CheckBox deliveryCheck;
    private CheckBox reservationCheck;
    private Slider ratingSlider;
    private ProgressIndicator loadingIndicator;

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
                    ArrayList<Recensione> allReviews = new UtenteNonRegistrato("").caricaRecensioni();
                    double avgRating = allReviews.stream()
                            .filter(rev -> rev.fkIdRistorante.equals(item.idRistorante) && rev.voto != -1)
                            .mapToInt(rev -> rev.voto)
                            .average().orElse(0.0);
                    long reviewCount = allReviews.stream()
                            .filter(rev -> rev.fkIdRistorante.equals(item.idRistorante) && rev.voto != -1)
                            .count();

                    DecimalFormat df = new DecimalFormat("#.0");
                    ratingLabel.setText("Valutazione: " + df.format(avgRating) + "/5 (" + reviewCount + " recensioni)");
                    String price = String.format("Prezzo Medio: %.2f€", item.prezzo);
                    String delivery = item.consegna ? "✓ Delivery" : "✗ Delivery";
                    String reservation = item.pren_online ? "✓ Prenotazione Online" : "✗ Prenotazione Online";
                    servicesLabel.setText(price + "  |  " + delivery + "  |  " + reservation);

                    setGraphic(content);
                }
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
        locationField.clear(); minPriceField.clear(); maxPriceField.clear();
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
        filtersBox.getChildren().addAll(
                new Label("Tipologia di cucina:"), cuisineTypeCombo,
                new Label("Località (obbligatoria):"), locationField,
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
        String location = locationField.getText().trim();
        if (location.isEmpty()) {
            mainApp.showError("Il campo Località è obbligatorio.");
            return;
        }
        loadingIndicator.setVisible(true);
        restaurantList.setItems(FXCollections.emptyObservableList());
        restaurantList.setPlaceholder(new Label("Ricerca in corso..."));
        Task<ObservableList<Ristorante>> searchTask = new Task<>() {
            @Override
            protected ObservableList<Ristorante> call() throws Exception {
                ArrayList<Ristorante> tuttiRistoranti = Gestione.Deserializer.fromJsonFile(
                        "data/ristoranti.json", Ristorante.class, new Ristorante.RistoranteDeserializer());
                ArrayList<Recensione> tutteRecensioni = new UtenteNonRegistrato("").caricaRecensioni();
                Map<CriterioRicerca, String> criteriIniziali = new HashMap<>();
                criteriIniziali.put(CriterioRicerca.CITTA, location);
                String tipoCucinaSelezionato = cuisineTypeCombo.getValue();
                if (tipoCucinaSelezionato != null && !tipoCucinaSelezionato.equals("Qualsiasi")) {
                    String enumStyleCuisine = tipoCucinaSelezionato.toUpperCase().replace(" ", "_");
                    criteriIniziali.put(CriterioRicerca.TIPO_CUCINA, enumStyleCuisine);
                }
                UtenteNonRegistrato operatoreRicerca = new UtenteNonRegistrato(location);
                ArrayList<Ristorante> risultatiParziali = operatoreRicerca.cercaRistorante(tuttiRistoranti, criteriIniziali);
                Float minPrice = parsePrice(minPriceField.getText(), 0f);
                Float maxPrice = parsePrice(maxPriceField.getText(), Float.MAX_VALUE);
                double minRating = ratingSlider.getValue();
                ArrayList<Ristorante> risultatiFinali = risultatiParziali.stream()
                        .filter(r -> r.prezzo >= minPrice && r.prezzo <= maxPrice)
                        .filter(r -> !deliveryCheck.isSelected() || r.consegna)
                        .filter(r -> !reservationCheck.isSelected() || r.pren_online)
                        .filter(r -> {
                            if (minRating == 0) return true;
                            double avgRating = tutteRecensioni.stream()
                                    .filter(rev -> rev.fkIdRistorante.equals(r.idRistorante) && rev.voto != -1)
                                    .mapToInt(rev -> rev.voto)
                                    .average()
                                    .orElse(0.0);
                            return avgRating >= minRating;
                        })
                        .collect(Collectors.toCollection(ArrayList::new));
                return FXCollections.observableArrayList(risultatiFinali);
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
        locationField.setText(location);
    }

    /**
     * Restituisce il nodo radice di questa vista per l'inserimento nel layout principale.
     * @return Il {@code BorderPane} che contiene l'intera interfaccia di questa vista.
     */
    public BorderPane getView() {
        return view;
    }
}