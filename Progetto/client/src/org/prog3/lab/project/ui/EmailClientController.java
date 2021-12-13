package org.prog3.lab.project.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.prog3.lab.project.model.Email;
import org.prog3.lab.project.model.EmailClient;
import org.prog3.lab.project.model.EmailWriter;

import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EmailClientController {

    @FXML
    private Label labelAccountName;

    @FXML
    private Button btnNewEmail;

    @FXML
    private Button btnUpdate;

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
    private Button btnDelete;

    @FXML
    private Tab tabReceivedEmails;

    @FXML
    private Tab tabSendedEmails;

    private EmailClient model;
    private Stage stage;
    private static Email selectEmail;
    private Email emptyEmail;
    private boolean activate = false;

    @FXML
    public void initialize(EmailClient model, Stage stage){
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model =model;
        this.stage = stage;
        //istanza nuovo client
        //model.generateRandomEmails(10);
        showEmails(true, true);  //inizio partendo con update della lista inviate

        //binding tra lstEmails e inboxProperty
        labelAccountName.textProperty().bind(model.emailAddressProperty());
        listReceivedEmails.itemsProperty().bind(model.receivedEmailsProperty());
        listReceivedEmails.setOnMouseClicked(this::showSelectReceivedEmail);    //this prende il metodo showselectRecivedEmail e gli passa mouse event
        listSendedEmails.itemsProperty().bind(model.sendedEmailsProperty());
        listSendedEmails.setOnMouseClicked(this::showSelectSendedEmail);
        btnNewEmail.setOnAction(this::btnNewEmailClick);
        btnDelete.setOnAction(this::btnDeleteClick);
        btnUpdate.setOnAction(this::updateEmailsLists);
        tabReceivedEmails.setOnSelectionChanged(this::updateEmailsLists);

        emptyEmail = new Email("", "", List.of(""), "", "", "");

        viewEmailDetail(emptyEmail);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(32), ev -> {
            showEmails(false, false);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                timeline.stop();
            }
        });
    }

    private void updateEmailsLists(Event event) {
        showEmails(false, false);
    }

    private void showEmails(boolean updateSended, boolean startUpdate){
        int countNewEmails = model.updateEmailslists(updateSended, startUpdate );

        //System.out.println(countNewEmails);
        if(countNewEmails > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Nuove email ricevute");
            alert.setContentText("Hai ricevuto "+ countNewEmails + "email");
            alert.show();
        }
    }

    protected void showSelectReceivedEmail(MouseEvent mouseEvent) {

        if(!activate)
            activeFiled();

        selectEmail = (Email) listReceivedEmails.getSelectionModel().getSelectedItem(); //casting ??

        viewEmailDetail(selectEmail);
    }

    protected void showSelectSendedEmail(MouseEvent mouseEvent) {

        if(!activate)
            activeFiled();

        selectEmail = (Email) listSendedEmails.getSelectionModel().getSelectedItem();

        viewEmailDetail(selectEmail);
    }

    protected void activeFiled(){
        paneDetails.setVisible(true);
        paneNoSelect.setVisible(false);
        activate=true;
    }

    private void btnNewEmailClick(ActionEvent actionEvent) {

        try {
            FXMLLoader loaderEmailWriter = new FXMLLoader(getClass().getResource("../resources/emailWriter.fxml"));
            Scene scene = new Scene(loaderEmailWriter.load());
            Stage writeStage = new Stage();
            EmailWriterController emailWriterController = loaderEmailWriter.getController();
            EmailWriter modelWriter = new EmailWriter();
            emailWriterController.initialize(modelWriter, model, writeStage, this.model.emailAddressProperty());
            writeStage.initModality(Modality.APPLICATION_MODAL);
            writeStage.setScene(scene);
            writeStage.setMinWidth(650);
            writeStage.setMinHeight(500);
            writeStage.setResizable(false);

            writeStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    System.out.println("tiraporco");
                    showEmails(true, false);
                }
            });

            writeStage.show();

        }catch (Exception e){
            System.out.println("Exception: "+ e.getMessage());
        }

    }

    protected void btnDeleteClick(ActionEvent actionEvent){

        if(tabReceivedEmails.isSelected()){
            model.deleteEmail(selectEmail, "receivedEmails", selectEmail.getId());
        }
        if(tabSendedEmails.isSelected()){
            model.deleteEmail(selectEmail, "sendedEmails", selectEmail.getId());
        }


        viewEmailDetail(emptyEmail);
    }

    protected void viewEmailDetail(Email email) {
        if(email != null) {
            textFrom.setText(email.getSender());    //campi assegnati da scenebuilder fx::ID
            textReceivers.setText(String.join(", ", email.getReceivers()));
            textObject.setText(email.getObject());
            fieldDateHour.setText(email.getDate());
            textEmail.setText(email.getText());
        }
    }
}
