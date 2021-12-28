package org.prog3.lab.project.ui;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.prog3.lab.project.model.EmailClient;
import org.prog3.lab.project.model.EmailWriter;
import org.prog3.lab.project.model.User;

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

    @FXML
    private Label sendResult;


    private EmailWriter modelWriter;
    //private EmailClient modelClient;
    //private StringProperty emailAddress;
    private User user;
    private Stage writeStage;

    @FXML
    public void initialize(EmailWriter modelWriter, /*EmailClient modelClient,*/ Stage writeStage, User user/*StringProperty emailAddress*/, String to, String object, String text){
        if (this.modelWriter != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.modelWriter = modelWriter;
        //this.modelClient = modelClient;
        this.writeStage = writeStage;
        //this.emailAddress = emailAddress;
        this.user = user;

        if(to!=null && object!=null && text!=null){
            textReceivers.setText(to);
            textObject.setText(object);
            textEmail.setText("\n\n\n--- Messaggio originale ---\n\n"+text);
        }

        textReceivers.setOnKeyReleased(this::keyReleased);
        labelError.setStyle("-fx-text-fill: red");

        if(textReceivers.getText().equals(""))
            btnSend.setDisable(true);

        btnSend.setOnAction(this::btnSendClick);
    }

    private void btnSendClick(ActionEvent actionEvent) {
        String response = modelWriter.serverSendEmail(user/*emailAddress*/, textReceivers.getText(), textObject.getText(), textEmail.getText());

        btnSend.setVisible(false);
        sendResult.setText(response);
        sendResult.setVisible(true);
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
