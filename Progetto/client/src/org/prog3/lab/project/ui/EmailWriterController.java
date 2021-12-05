package org.prog3.lab.project.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;

import java.util.regex.Pattern;


public class EmailWriterController {

    @FXML
    private TextArea textReceivers;

    @FXML
    private Label labelError;

    @FXML
    public void initialize(){
        textReceivers.setOnKeyReleased(this::keyReleased);
        labelError.setStyle("-fx-text-fill: red");
    }

    private void keyReleased(KeyEvent keyEvent) {
        if(Pattern.matches("([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\s*)+", textReceivers.getText())) {
            textReceivers.setStyle("-fx-border-color: green");

            if(labelError.isVisible())
                labelError.setVisible(false);

        }else {
            textReceivers.setStyle("-fx-border-color: red");

            if(!labelError.isVisible())
                labelError.setVisible(true);
        }
    }


}
