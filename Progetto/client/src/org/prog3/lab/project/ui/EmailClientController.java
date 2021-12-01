package org.prog3.lab.project.ui;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.prog3.lab.project.model.Email;
import org.prog3.lab.project.model.EmailClient;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class EmailClientController {

    @FXML
    private Label labelAccountName;

    @FXML
    private Label labelSettings;

    @FXML
    private Label labelLogout;

    @FXML
    private Button btnNewEmail;

    @FXML
    private Button btnUpdate;

    @FXML
    private ListView listReceivedEmails;

    @FXML
    private ListView listSendedEmails;

    @FXML
    private TextArea textReceivers;

    @FXML
    private TextArea textFrom;

    @FXML
    private TextArea textObject;

    @FXML
    private TextField fieldDateHour;

    @FXML
    private TextArea textEmail;

    @FXML
    private Button btnReply;

    @FXML
    private Button btnReplyAll;

    @FXML
    private Button btnDelete;

    @FXML
    private Tab tabReceivedEmails;

    @FXML
    private Tab tabSendedEmails;

    private EmailClient model;
    private Stage stage;
    private Email selectEmail;
    private Email emptyEmail;

    @FXML
    public void initialize(EmailClient model, Stage stage){
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model =model;
        this.stage = stage;
        //istanza nuovo client
        //model.generateRandomEmails(10);
        model.updateEmailslists(true, true);
        selectEmail = null;

        //binding tra lstEmails e inboxProperty
        labelAccountName.textProperty().bind(model.emailAddressProperty());
        listReceivedEmails.itemsProperty().bind(model.receivedEmailsProperty());
        listReceivedEmails.setOnMouseClicked(this::showSelectReceivedEmail);
        listSendedEmails.itemsProperty().bind(model.sendedEmailsProperty());
        listSendedEmails.setOnMouseClicked(this::showSelectSendedEmail);
        btnNewEmail.setOnAction(this::btnNewEmailClick);
        btnDelete.setOnAction(this::btnDeleteClick);
        btnUpdate.setOnAction(this::updateEmailsLists);
        labelSettings.setOnMouseClicked(this::labelSettingsClick);
        labelLogout.setOnMouseClicked(this::labelLogoutClick);
        tabReceivedEmails.setOnSelectionChanged(this::updateEmailsLists);


        emptyEmail = new Email("", "", List.of(""), "", "", "");

        viewEmailDetail(emptyEmail);
    }

    private void updateEmailsLists(Event event) {

        model.updateEmailslists(false, false );

    }

    protected void showSelectReceivedEmail(MouseEvent mouseEvent) {
        Email email = (Email) listReceivedEmails.getSelectionModel().getSelectedItem();

        selectEmail = email;
        viewEmailDetail(email);
    }

    protected void showSelectSendedEmail(MouseEvent mouseEvent) {
        Email email = (Email) listSendedEmails.getSelectionModel().getSelectedItem();

        selectEmail = email;
        viewEmailDetail(email);
    }

    private void btnNewEmailClick(ActionEvent actionEvent) {

        try {

            FXMLLoader loaderEmailClient = new FXMLLoader(getClass().getResource("../resources/emailWriter.fxml"));

            Stage writeStage = new Stage();

            Scene scene = new Scene(loaderEmailClient.load());
            writeStage.setScene(scene);
            writeStage.initModality(Modality.APPLICATION_MODAL);
            writeStage.show();

        }catch (Exception e){
            System.out.println("Exception: "+ e.getMessage());
        }

    }

    protected void btnDeleteClick(ActionEvent actionEvent){
        model.deleteEmail(selectEmail);
        viewEmailDetail(emptyEmail);
    }

    protected void labelSettingsClick(MouseEvent mouseEvent){
        FXMLLoader loaderEmailClient = new FXMLLoader(getClass().getResource("../resources/login.fxml"));

        try {
            Scene scene = new Scene(loaderEmailClient.load());
            stage.setScene(scene);
        }catch (Exception e){
            System.out.println("Exception: "+ e.getMessage());
        }
    }

    protected void labelLogoutClick(MouseEvent mouseEvent){
        stage.close();
    }

    protected void viewEmailDetail(Email email) {
        if(email != null) {
            textFrom.setText(email.getSender());
            textReceivers.setText(String.join(", ", email.getReceivers()));
            textObject.setText(email.getObject());
            fieldDateHour.setText(String.join(" - ", List.of(email.getDate())));
            textEmail.setText(email.getText());
        }
    }
}
