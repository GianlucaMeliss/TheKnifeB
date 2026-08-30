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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestisce un dialogo personalizzato per l'inserimento e la modifica di una recensione.
 * <p>
 * Questa classe estende {@link Dialog} per creare un pop-up che si adatta a due contesti:
 * la creazione di una nuova recensione (con una ricerca ristorante "AutoComplete") o
 * la modifica di una recensione esistente. Implementa una validazione per prevenire
 * l'invio di dati incompleti e un selettore di stelle grafico per la valutazione.
 * </p>
 * @author Simone Zamberletti
 */
public class ReviewDialog extends Dialog<Recensione> {

    private final ArrayList<Ristorante> originalRestaurants;
    private Ristorante selectedRestaurant;
    private boolean isUpdatingFromSelection = false;

    /**
     * Costruttore del dialogo per le recensioni.
     * <p>
     * Costruisce l'interfaccia e la logica interna del dialogo, adattandosi
     * alla modalità di aggiunta o di modifica a seconda dei parametri forniti.
     * </p>
     * @param restaurants La lista completa di ristoranti da cui l'utente può cercare, usata in modalità "aggiunta".
     * @param existingReview La recensione esistente da modificare, usata in modalità "modifica". Passare {@code null} per la modalità "aggiunta".
     */
    public ReviewDialog(ArrayList<Ristorante> restaurants, Recensione existingReview) {
        this.originalRestaurants = restaurants;

        boolean isEditMode = existingReview != null;
        if (isEditMode) {
            if (this.originalRestaurants != null) {
                this.selectedRestaurant = this.originalRestaurants.stream()
                        .filter(r -> r.idRistorante == existingReview.fkIdRistorante)
                        .findFirst().orElse(null);
            }
        } else if (restaurants != null && restaurants.size() == 1) {
            this.selectedRestaurant = restaurants.get(0);
        }

        setTitle(isEditMode ? "Modifica Recensione" : "Aggiungi Nuova Recensione");
        setHeaderText(isEditMode ? "Modifica la tua valutazione." : "Lascia la tua valutazione.");

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(15);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Ristorante:"), 0, 0);

        VBox searchContainer = new VBox(5);
        if (isEditMode || (selectedRestaurant != null && restaurants.size() == 1)) {
            Label restaurantLabel = new Label(selectedRestaurant != null ? selectedRestaurant.nome : "Sconosciuto");
            searchContainer.getChildren().add(restaurantLabel);
        } else {
            TextField searchField = new TextField();
            searchField.setPromptText("Cerca un ristorante...");
            Label selectionLabel = new Label("Nessun ristorante selezionato");
            selectionLabel.setStyle("-fx-font-style: italic;");
            ListView<Ristorante> resultsList = new ListView<>();
            resultsList.setPrefHeight(100);
            resultsList.setVisible(false);
            resultsList.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Ristorante item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.nome + " (" + item.citta + ")");
                }
            });
            VBox searchBox = new VBox(5, searchField, resultsList);
            searchContainer.getChildren().addAll(searchBox, selectionLabel);
            searchField.textProperty().addListener((obs, oldText, newText) -> {
                if (isUpdatingFromSelection) return;
                if (selectedRestaurant != null && !newText.equals(selectedRestaurant.nome)) {
                    selectedRestaurant = null;
                }
                selectionLabel.setText("Nessun ristorante selezionato");
                selectionLabel.setStyle("-fx-font-style: italic; -fx-font-weight: normal;");
                if (newText == null || newText.isEmpty()) {
                    resultsList.setVisible(false);
                } else {
                    ArrayList<Ristorante> filteredList = originalRestaurants.stream()
                            .filter(r -> r.nome.toLowerCase().contains(newText.toLowerCase()))
                            .collect(Collectors.toCollection(ArrayList::new));
                    resultsList.setItems(FXCollections.observableArrayList(filteredList));
                    resultsList.setVisible(!filteredList.isEmpty());
                }
            });
            resultsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    isUpdatingFromSelection = true;
                    selectedRestaurant = newVal;
                    searchField.setText(newVal.nome);
                    selectionLabel.setText("Selezionato: " + newVal.nome + " (" + newVal.citta + ")");
                    selectionLabel.setStyle("-fx-font-style: normal; -fx-font-weight: bold;");
                    resultsList.setVisible(false);
                    isUpdatingFromSelection = false;
                }
            });
        }
        grid.add(searchContainer, 1, 0);

        IntegerProperty rating = new SimpleIntegerProperty(isEditMode ? existingReview.voto : 5);
        HBox starSelector = createStarRatingSelector(rating);

        TextArea reviewText = new TextArea();
        reviewText.setWrapText(true);

        grid.add(new Label("Valutazione:"), 0, 1);
        grid.add(starSelector, 1, 1);
        grid.add(new Label("Commento:"), 0, 2);
        grid.add(reviewText, 1, 2);

        if (isEditMode) {
            reviewText.setText(existingReview.commento);
        } else {
            reviewText.setPromptText("Scrivi qui la tua recensione...");
        }

        getDialogPane().setContent(grid);

        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!isEditMode && this.selectedRestaurant == null) {
                showValidationError("Devi cercare e selezionare un ristorante dalla lista.");
                event.consume();
            } else if (reviewText.getText().trim().isEmpty()) {
                showValidationError("Il commento non può essere vuoto.");
                event.consume();
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (this.selectedRestaurant == null || reviewText.getText().trim().isEmpty()) {
                    return null;
                }
                return new Recensione(
                        this.selectedRestaurant.idRistorante, -1,
                        rating.get(),
                        reviewText.getText().trim(), LocalDate.now()
                );
            }
            return null;
        });
    }

    /**
     * Crea un componente grafico {@code HBox} contenente 5 stelle interattive per la valutazione.
     * <p>
     * Le stelle reagiscono al passaggio del mouse per mostrare un'anteprima del voto
     * e al click per impostare il valore definitivo. Lo stato della valutazione è
     * collegato a una {@code IntegerProperty}.
     * </p>
     * @param ratingProperty La proprietà intera che memorizza il voto e viene aggiornata dal componente.
     * @return Un {@code HBox} contenente le stelle cliccabili.
     */
    private HBox createStarRatingSelector(IntegerProperty ratingProperty) {
        HBox starBox = new HBox(2);
        List<Label> stars = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Label star = new Label("☆");
            star.setFont(Font.font("System", FontWeight.BOLD, 22));
            star.setTextFill(Color.GOLD);
            star.setCursor(Cursor.HAND);

            final int ratingValue = i;

            star.setOnMouseEntered(e -> {
                for (int j = 0; j < 5; j++) {
                    stars.get(j).setText(j < ratingValue ? "★" : "☆");
                }
            });

            starBox.setOnMouseExited(e -> {
                int currentRating = ratingProperty.get();
                for (int j = 0; j < 5; j++) {
                    stars.get(j).setText(j < currentRating ? "★" : "☆");
                }
            });

            star.setOnMouseClicked(e -> ratingProperty.set(ratingValue));

            stars.add(star);
        }

        ratingProperty.addListener((obs, oldVal, newVal) -> {
            for (int i = 0; i < 5; i++) {
                stars.get(i).setText(i < newVal.intValue() ? "★" : "☆");
            }
        });

        int initialRating = ratingProperty.get();
        for (int i = 0; i < 5; i++) {
            stars.get(i).setText(i < initialRating ? "★" : "☆");
        }

        starBox.getChildren().addAll(stars);
        return starBox;
    }

    /**
     * Metodo di utilità per mostrare un pop-up di errore di validazione.
     * @param message Il messaggio specifico da visualizzare all'utente.
     */
    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Dati Mancanti");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}