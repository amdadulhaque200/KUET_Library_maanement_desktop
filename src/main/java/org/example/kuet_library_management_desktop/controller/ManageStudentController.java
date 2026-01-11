package org.example.kuet_library_management_desktop.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.example.kuet_library_management_desktop.controls.AutoCompleteTextField;
import org.example.kuet_library_management_desktop.service.StudentService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * ManageStudentController (minimal wiring). If you already have this controller,
 * merge the initialize() content below: set suggestions and setOnAutoCompleted.
 */
public class ManageStudentController implements Initializable {

    @FXML
    private AutoCompleteTextField rollSearchField;

    // ...existing code...

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // load roll suggestions from service
        var rolls = StudentService.getAllStudentRolls();
        if (rollSearchField != null) {
            rollSearchField.setSuggestions(rolls);
            rollSearchField.setOnAutoCompleted(selectedRoll -> {
                // called when a suggestion is clicked.
                // perform the same action as "search" or load the student's record.
                loadStudentByRoll(selectedRoll);
            });

            // Optional: react when user presses Enter in the field
            rollSearchField.setOnAction(evt -> {
                String txt = rollSearchField.getText();
                if (txt != null && !txt.isBlank()) {
                    loadStudentByRoll(txt.trim());
                }
            });
        }
    }

    private void loadStudentByRoll(String roll) {
        // TODO: Replace with actual lookup and UI update logic
        System.out.println("Load student details for roll: " + roll);
        // Example: studentService.findByRoll(roll) -> populate fields / table
    }

    // ...existing code...
}

