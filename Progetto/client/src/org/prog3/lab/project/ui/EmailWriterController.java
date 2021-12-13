package org.prog3.lab.project.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.prog3.lab.project.model.EmailClient;
import org.prog3.lab.project.model.EmailWriter;

import java.io.IOException;
import java.util.regex.Pattern;


public class EmailWriterController {

    @FXML
    private TextArea textReceivers;

    @FXML
    private TextArea textObject;

    @FXML
    private TextArea textEmail;

    @FXML
    private Label labelError;

    @FXML
    private Button btnSend;


    private EmailWriter modelWriter;
    private EmailClient modelClient;
    private StringProperty emailAddress;
    private Stage writeStage;

    @FXML
    public void initialize(EmailWriter modelWriter, EmailClient modelClient, Stage writeStage, StringProperty emailAddress){
        if (this.modelWriter != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.modelWriter = modelWriter;
        this.modelClient = modelClient;
        this.writeStage = writeStage;
        this.emailAddress = emailAddress;

        textReceivers.setOnKeyReleased(this::keyReleased);
        labelError.setStyle("-fx-text-fill: red");

        btnSend.setDisable(true);
        btnSend.setOnAction(this::btnSendClick);
    }

    private void btnSendClick(ActionEvent actionEvent) {
        String response = modelWriter.serverSendEmail(emailAddress, textReceivers.getText(), textObject.getText(), textEmail.getText());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Informazioni invio");
        alert.setContentText(response);
        alert.showAndWait();

        if(response.equals("Email inviata correttamente.")) {
            modelClient.updateEmailslists(true, false);
            writeStage.close();
        }
    }

    private void keyReleased(KeyEvent keyEvent) {
        if(Pattern.matches("^([A-Za-z0-9\\.|-|_]*[@]{1}[A-Za-z0-9\\.|-|_]*[.]{1}[a-z]{2,5})(,[A-Za-z0-9\\.|-|_]*[@]{1}[A-Za-z0-9\\.|-|_]*[.]{1}[a-z]{2,5})*?$", textReceivers.getText())) {
            textReceivers.setStyle("-fx-border-color: green");

            if(labelError.isVisible())
                labelError.setVisible(false);

            if(btnSend.isDisable())
                btnSend.setDisable(false);

        }else {
            textReceivers.setStyle("-fx-border-color: red");

            if(!labelError.isVisible())
                labelError.setVisible(true);

            if(!btnSend.isDisable())
                btnSend.setDisable(true);
        }
    }

}
