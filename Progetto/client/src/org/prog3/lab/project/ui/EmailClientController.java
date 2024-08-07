package org.prog3.lab.project.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.prog3.lab.project.model.Email;
import org.prog3.lab.project.model.EmailClient;
import org.prog3.lab.project.model.User;

import java.io.File;
import java.util.Objects;

public class EmailClientController {

    @FXML
    private Label labelAccountName;

    @FXML
    private Button btnNewEmail;

    @FXML
    private Button btnUpdate;

    @FXML
    private Label onlineLabel;

    @FXML
    private Label offlineLabel;

    @FXML
    private ListView listReceivedEmails;

    @FXML
    private ListView listSendedEmails;

    @FXML
    private StackPane paneDetails;

    @FXML
    private StackPane paneNoSelect;

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
    private Button btnForward;

    @FXML
    private Button btnDelete;

    @FXML
    private Tab tabReceivedEmails;

    private EmailClient model;
    private User user;
    Stage stage;
    boolean startUpdate;
    private static Email selectEmail;
    private Email emptyEmail;
    private boolean activate = false;
    private Timeline emailUpdate;

    @FXML
    public void initialize(User user, Stage stage) {   //chiamato da Email.java emailClientController.initialize
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.user = user;
        this.stage = stage;
        this.model = new EmailClient();

        model.serverLogin(user);

        startUpdate = true;

        //istanza nuovo client
        showEmails(true, startUpdate);  //inizio partendo con update della lista inviate

        //binding tra lstEmails e inboxProperty
        labelAccountName.setText(user.getUserEmail());          //Prendo il labelAcc.. E accedo al textProperty e faccio il bind al nostro property|| ogni volta che si modifica model mi modifica anche listReceiv e modifica in tempo reale il view di questo label
        listReceivedEmails.itemsProperty().bind(model.receivedEmailsProperty());     //stessa cosa per le listReceivedEmails il bind cambia i dati la view cambia quando i dati sono cambiati
        listReceivedEmails.setOnMouseClicked(this::showSelectReceivedEmail);         //chiamo la funzione showSelectRecivedEmail alla pressione del mouse
        listSendedEmails.itemsProperty().bind(model.sendedEmailsProperty());         
        listSendedEmails.setOnMouseClicked(this::showSelectSendedEmail);
        btnReply.setOnAction(this::btnReplyClick);                                  //sono i relativi bottoni che chiamano i relativi funzioni
        btnReplyAll.setOnAction(this::btnReplyAllClick);
        btnForward.setOnAction(this::btnForwardClick);
        btnNewEmail.setOnAction(this::btnNewEmailClick);
        btnDelete.setOnAction(this::btnDeleteClick);
        btnUpdate.setOnAction(this::updateEmailsLists);
        tabReceivedEmails.setOnSelectionChanged(this::updateEmailsLists);

        emptyEmail = new Email("", "", "", "", "", "", "");   //parto con il view vuoto

        viewEmailDetail(emptyEmail);                                                                //chiamo il metodo viewEmailDetail prende l'email e cambia i componenti grafici

        emailUpdate = new Timeline(new KeyFrame(Duration.seconds(30), ev -> showEmails(false, startUpdate)));   //set di update ogni 30 secondi
        emailUpdate.setCycleCount(Timeline.INDEFINITE);
        emailUpdate.play();

        stage.setOnCloseRequest(windowEvent -> {
            emailUpdate.stop();
            model.serverLogout(user);                                               //Chiamo serverLogout di Email e apre un socket nel quale chiude il socket
        });
    }

    private void updateEmailsLists(Event event) {                                    //chiamato dal setOnAction

        showEmails(false, startUpdate);

    }

    private void showEmails(boolean updateSended, boolean startUpdate) {            //Quando e stato chiamato chiama updateEmailslists apre un socket verso il server
        if(!stage.isShowing())  //si riferisce allo stage del client
            stage.show();

        int countNewEmails = model.updateEmailslists(user, updateSended, startUpdate);

        if (countNewEmails < 0) {                                                  //fa la richiesta se richiesta e negativa torna  countEmails = -1;         
            onlineLabel.setVisible(false);
            offlineLabel.setVisible(true);
        } else if (countNewEmails == 0) {
            onlineLabel.setVisible(true);
            offlineLabel.setVisible(false);

            if(this.startUpdate)                                                  //condizione che ci sono email          
                this.startUpdate = false;
        } else {
            onlineLabel.setVisible(true);
            offlineLabel.setVisible(false);

            if(this.startUpdate)                                                //va vedere nel log se il contenuto non e READ allora email e nuovo e viene messo nel countNewEmails
                this.startUpdate = false;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Nuove email ricevute");
            alert.setContentText("Hai ricevuto " + countNewEmails + "email");
            alert.show();
        }
    }

    protected void showSelectReceivedEmail(MouseEvent mouseEvent) {             //chiamato dal setOnmouseEvent

        if (!activate)
            activeFiled();

        selectEmail = (Email) listReceivedEmails.getSelectionModel().getSelectedItem(); 

        btnShow();

        viewEmailDetail(selectEmail);
    }

    protected void showSelectSendedEmail(MouseEvent mouseEvent) {

        if (!activate)
            activeFiled();

        selectEmail = (Email) listSendedEmails.getSelectionModel().getSelectedItem();

        btnShow();

        viewEmailDetail(selectEmail);
    }

    protected void btnShow() {
        if (selectEmail.getType().equals("sendedEmails")) {
            if (!btnReply.isDisable() && !btnReplyAll.isDisable() && !btnForward.isDisable()) {
                btnReply.setDisable(true);
                btnReplyAll.setDisable(true);
                btnForward.setDisable(true);
            }
        } else {
            if (btnReply.isDisable() && btnReplyAll.isDisable() && btnForward.isDisable()) {
                btnReply.setDisable(false);
                btnReplyAll.setDisable(false);
                btnForward.setDisable(false);
            }
        }

    }

    protected void activeFiled() {
        paneDetails.setVisible(true);
        paneNoSelect.setVisible(false);
        activate = true;
    }

    private void btnNewEmailClick(ActionEvent actionEvent) {

        showEmailWriter(null, null, null);

    }

    private void btnReplyClick(ActionEvent actionEvent) {

        showEmailWriter(selectEmail.getSender(), "R: " + selectEmail.getObject(), selectEmail.getText());

    }

    private void btnReplyAllClick(ActionEvent actionEvent) {
        int start=0;
        int end = 0;
        String receivers = selectEmail.getReceivers();
        String replyAllReceivers = "";

        while(end>=0){

            end = receivers.indexOf(",", start);

            String receiver;

            if(end>=0)
                receiver = receivers.substring(start, end);
            else
                receiver = receivers.substring(start);

            if(!receiver.equals(user.getUserEmail())) {
                if(receiver.equals(""))
                    replyAllReceivers += receiver;
                else
                    replyAllReceivers += ","+receiver;
            }

            start = end+1;
        }
        showEmailWriter(selectEmail.getSender() + replyAllReceivers, "R: " + selectEmail.getObject(), selectEmail.getText());
    }

    private void btnForwardClick(ActionEvent actionEvent) {
        showEmailWriter("", "I: " + selectEmail.getObject(), selectEmail.getText());
    }

    private void showEmailWriter(String to, String object, String text) {                               //faccio partire EmailWriter

        try {
            FXMLLoader loaderEmailWriter = new FXMLLoader(getClass().getResource("../resources/emailWriter.fxml"));
            Scene scene = new Scene(loaderEmailWriter.load());
            Stage writeStage = new Stage();
            EmailWriterController emailWriterController = loaderEmailWriter.getController();
            emailWriterController.initialize(user, to, object, text);
            writeStage.initModality(Modality.APPLICATION_MODAL);
            writeStage.setScene(scene);
            writeStage.setMinWidth(650);
            writeStage.setMinHeight(500);
            writeStage.setResizable(false);
            writeStage.show();

            writeStage.setOnCloseRequest(windowEvent -> showEmails(true, startUpdate));

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

    protected void btnDeleteClick(ActionEvent actionEvent) {

        String response = model.deleteEmail(user, selectEmail);

        switch (response) {
            case "remove_correct" -> {
                viewEmailDetail(emptyEmail);                                                //chiamo il metodo viewEmailDetail prende l'email e cambia i componenti grafici
                onlineLabel.setVisible(true);
                offlineLabel.setVisible(false);
            }
            case "remove_error" -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Errore durante la rimozione");
                alert.setContentText("Impossibile eliminare la mail selezionata al momento");
                alert.showAndWait();
            }
            case "server_error" -> {
                onlineLabel.setVisible(false);
                offlineLabel.setVisible(true);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Errore durante la rimozione");
                alert.setContentText("Errore di comunicazione con il server. Riprovare più tardi.");
                alert.showAndWait();
            }
        }
    }

    protected void viewEmailDetail(Email email) {
        if (email != null) {
            textFrom.setText(email.getSender());    //campi assegnati da scenebuilder fx::ID
            textReceivers.setText(email.getReceivers());
            textObject.setText(email.getObject());
            fieldDateHour.setText(email.getDate());
            textEmail.setText(email.getText());
        }
    }
}