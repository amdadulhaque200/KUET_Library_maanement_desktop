package org.example.kuet_library_management_desktop.controls;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Lightweight autocomplete TextField.
 * Usage:
 *   AutoCompleteTextField field = new AutoCompleteTextField();
 *   field.setSuggestions(listOfRolls);
 *   field.setOnAutoCompleted(selected -> { ... });
 *
 * In FXML, reference with full package name:
 *   <controls:AutoCompleteTextField fx:id="rollSearchField" />
 */
public class AutoCompleteTextField extends TextField {

    private final ContextMenu suggestionsPopup;
    private ObservableList<String> suggestions = FXCollections.observableArrayList();
    private Consumer<String> onAutoCompleted;

    public AutoCompleteTextField() {
        super();
        suggestionsPopup = new ContextMenu();
        // update suggestions as user types
        textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                suggestionsPopup.hide();
            } else {
                List<String> filtered = suggestions.stream()
                        .filter(s -> s.toLowerCase().contains(newText.toLowerCase()))
                        .sorted()
                        .limit(10)
                        .collect(Collectors.toList());
                if (!filtered.isEmpty()) {
                    populatePopup(filtered);
                    if (!suggestionsPopup.isShowing()) {
                        suggestionsPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                    }
                } else {
                    suggestionsPopup.hide();
                }
            }
        });

        focusedProperty().addListener((obs, wasFocused, isNow) -> {
            if (!isNow) {
                // hide suggestions when focus lost
                Platform.runLater(suggestionsPopup::hide);
            }
        });
    }

    public void setSuggestions(List<String> items) {
        if (items == null) {
            this.suggestions = FXCollections.observableArrayList();
        } else {
            this.suggestions = FXCollections.observableArrayList(items);
        }
    }

    public void setOnAutoCompleted(Consumer<String> callback) {
        this.onAutoCompleted = callback;
    }

    private void populatePopup(List<String> searchResult) {
        var menuItems = searchResult.stream().map(result -> {
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(actionEvent -> {
                setText(result);
                positionCaret(result.length());
                suggestionsPopup.hide();
                if (onAutoCompleted != null) {
                    onAutoCompleted.accept(result);
                }
            });
            return item;
        }).collect(Collectors.toList());

        suggestionsPopup.getItems().clear();
        suggestionsPopup.getItems().addAll(menuItems);
    }
}

