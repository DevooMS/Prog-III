package org.prog3.lab.project.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.prog3.lab.project.model.Email;
import org.prog3.lab.project.model.EmailClient;
import org.prog3.lab.project.model.EmailWriter;
import org.prog3.lab.project.model.User;

public class EmailClientController {

    @FXML
    private Label labelConnectionError;

    @FXML
    private Label labelAccountName;

    @FXML
    private Button btnNewEmail;

    @FXML
    private Button btnUpdate;

    @FXML
    private Label labelError;

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

    @FXML
    private Tab tabSendedEmails;

    private EmailClient model;
    private User user;
    private Stage stage;
    private static Email selectEmail;
    private Email emptyEmail;
    private boolean activate = false;

    @FXML
    public void initialize(EmailClient model, User user, Stage stage){
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model =model;
        this.user = user;
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
        btnReply.setOnAction(this::btnReplyClick);
        btnReplyAll.setOnAction(this::btnReplyAllClick);
        btnForward.setOnAction(this::btnForwardClick);
        btnNewEmail.setOnAction(this::btnNewEmailClick);
        btnDelete.setOnAction(this::btnDeleteClick);
        btnUpdate.setOnAction(this::updateEmailsLists);
        //tabReceivedEmails.setOnSelectionChanged(this::updateEmailsLists);

        emptyEmail = new Email("", "","", "", "", "", "");

        viewEmailDetail(emptyEmail);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(30), ev -> {
            showEmails(false, false);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                timeline.stop();
                model.serverLogout(user);
            }
        });
    }

    private void updateEmailsLists(Event event) {

        showEmails(false, false);

    }

    private void showEmails(boolean updateSended, boolean startUpdate){
        int countNewEmails = model.updateEmailslists(user, updateSended, startUpdate );

        if(countNewEmails < 0) {
            labelConnectionError.setVisible(true);
            labelAccountName.setVisible(false);
        } else if(countNewEmails==0){
            labelConnectionError.setVisible(false);
            labelAccountName.setVisible(true);
        } else if(countNewEmails > 0) {
            labelConnectionError.setVisible(false);
            labelAccountName.setVisible(true);
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

        btnShow();

        viewEmailDetail(selectEmail);
    }

    protected void showSelectSendedEmail(MouseEvent mouseEvent) {

        if(!activate)
            activeFiled();

        selectEmail = (Email) listSendedEmails.getSelectionModel().getSelectedItem();

        btnShow();

        viewEmailDetail(selectEmail);
    }

    protected void btnShow(){
        if(selectEmail.getType().equals("sendedEmails")){
            if(!btnReply.isDisable() && !btnReplyAll.isDisable() && !btnForward.isDisable()){
                btnReply.setDisable(true);
                btnReplyAll.setDisable(true);
                btnForward.setDisable(true);
            }
        } else{
            if(btnReply.isDisable() && btnReplyAll.isDisable() && btnForward.isDisable()) {
                btnReply.setDisable(false);
                btnReplyAll.setDisable(false);
                btnForward.setDisable(false);
            }
        }

    }

    protected void activeFiled(){
        paneDetails.setVisible(true);
        paneNoSelect.setVisible(false);
        activate=true;
    }

    private void btnNewEmailClick(ActionEvent actionEvent) {

        showEmailWriter(null, null, null);

    }

    private void btnReplyClick(ActionEvent actionEvent) {

        showEmailWriter(selectEmail.getSender(), "R: "+ selectEmail.getObject(), selectEmail.getText());

    }

    private void btnReplyAllClick(ActionEvent actionEvent) {
        showEmailWriter(selectEmail.getSender() +","+selectEmail.getReceivers(), "R: "+ selectEmail.getObject(), selectEmail.getText());
    }

    private void btnForwardClick(ActionEvent actionEvent) {
        showEmailWriter("", "I: "+ selectEmail.getObject(), selectEmail.getText());
    }

    private void showEmailWriter(String to, String object, String text){

        try {
            FXMLLoader loaderEmailWriter = new FXMLLoader(getClass().getResource("../resources/emailWriter.fxml"));
            Scene scene = new Scene(loaderEmailWriter.load());
            Stage writeStage = new Stage();
            EmailWriterController emailWriterController = loaderEmailWriter.getController();
            EmailWriter modelWriter = new EmailWriter();
            emailWriterController.initialize(modelWriter, /*model,*/ writeStage, user /*this.model.emailAddressProperty()*/, to, object, text);
            writeStage.initModality(Modality.APPLICATION_MODAL);
            writeStage.setScene(scene);
            writeStage.setMinWidth(650);
            writeStage.setMinHeight(500);
            writeStage.setResizable(false);
            writeStage.show();

            writeStage.setOnCloseRequest( new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    showEmails(true, false);
                }
            });

        }catch (Exception e){
            System.out.println("Exception: "+ e.getMessage());
        }

    }

    protected void btnDeleteClick(ActionEvent actionEvent){

        boolean result = model.deleteEmail(user, selectEmail);

        if(result) {
            viewEmailDetail(emptyEmail);
            labelConnectionError.setVisible(false);
            labelAccountName.setVisible(true);
        }else{
            labelConnectionError.setVisible(true);
            labelAccountName.setVisible(false);
        }
    }

    protected void viewEmailDetail(Email email) {
        if(email != null) {
            textFrom.setText(email.getSender());    //campi assegnati da scenebuilder fx::ID
            textReceivers.setText(email.getReceivers());
            textObject.setText(email.getObject());
            fieldDateHour.setText(email.getDate());
            textEmail.setText(email.getText());
        }
    }
}
