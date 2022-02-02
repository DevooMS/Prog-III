package org.prog3.lab.project.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.prog3.lab.project.model.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class ServerController {

    @FXML
    private Button btnLogoff;

    @FXML
    private Button btnUpdate;

    @FXML
    private Label labelInfo;

    @FXML
    private TabPane tabLog;

    @FXML
    private ListView listClients;

    @FXML
    private Label noConnection;

    @FXML
    private ListView listConnection;

    @FXML
    private Label noSend;

    @FXML
    private ListView listSend;

    @FXML
    private Label noErrorSend;

    @FXML
    private ListView listErrorSend;

    @FXML
    private Label noReceived;

    @FXML
    private ListView listReceived;

    @FXML
    private Label noLogin;

    @FXML
    private ListView listLogin;

    @FXML
    private Label noLogout;

    @FXML
    private ListView listLogout;

    @FXML
    private Label noRemove;

    @FXML
    private ListView listRemove;

    private Server model;
    private Stage stage;
    private Semaphore loginSem;
    private Semaphore logoutSem;
    private Semaphore connectionSem;
    private Semaphore sendSem;
    private Semaphore errorSendSem;
    private Semaphore receivedSem;
    private Semaphore removeSem;
    Timeline timeline;

    @FXML
    public void initialize(Stage stage, Semaphore loginSem, Semaphore logoutSem, Semaphore connectionSem, Semaphore sendSem,  Semaphore errorSendSem, Semaphore receivedSem, Semaphore removeSem)  {
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model = new Server();
        this.stage = stage;
        this.loginSem = loginSem;
        this.logoutSem = logoutSem;
        this.connectionSem = connectionSem;
        this.sendSem = sendSem;
        this.errorSendSem = errorSendSem;
        this.receivedSem = receivedSem;
        this.removeSem = removeSem;

        listClients.itemsProperty().bind(model.listClientsProperty());
        listClients.setOnMouseClicked(this::updateLog);
        listConnection.itemsProperty().bind(model.listConnectionProperty());
        listSend.itemsProperty().bind(model.listSendProperty());
        listErrorSend.itemsProperty().bind(model.listErrorSendProperty());
        listReceived.itemsProperty().bind(model.listReceivedProperty());
        listLogin.itemsProperty().bind(model.listLoginProperty());
        listLogout.itemsProperty().bind(model.listLogoutProperty());
        listRemove.itemsProperty().bind(model.listRemoveProperty());

        timeline = new Timeline(new KeyFrame(Duration.seconds(30), ev -> updateLogForType()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        btnLogoff.setOnAction(this::btnLogOffClick);

        btnUpdate.setOnAction(this::btnUpdateClick);

        stage.setOnCloseRequest(this::stageClose);

        model.addUser(getClass().getResource("../resources/userEmails").getPath());
    }

    private void btnLogOffClick(ActionEvent actionEvent) {
        serverLogOff();
    }

    private void btnUpdateClick(ActionEvent actionEvent) { updateLogForType(); }

    private void stageClose(WindowEvent windowEvent) {
        serverLogOff();
    }

    private void serverLogOff() {
        timeline.stop();
        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

            Vector<String> operationRequest = new Vector<>();

            operationRequest.add(0, "terminate");

            outStream.writeObject(operationRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.close();
    }

    private void updateLog(MouseEvent mouseEvent) {
        tabLog.setVisible(false);
        labelInfo.setText("Update log in corso");
        labelInfo.setVisible(true);

        updateLogForType();

        labelInfo.setVisible(false);
        tabLog.setVisible(true);
    }

    private void updateLogForType() {
        if (listClients.getSelectionModel().getSelectedItem() != null){
            logConnection((String) listClients.getSelectionModel().getSelectedItem());

            logSend((String) listClients.getSelectionModel().getSelectedItem());

            logErrorSend((String) listClients.getSelectionModel().getSelectedItem());

            logReceived((String) listClients.getSelectionModel().getSelectedItem());

            logLogin((String) listClients.getSelectionModel().getSelectedItem());

            logLogout((String) listClients.getSelectionModel().getSelectedItem());

            logRemove((String) listClients.getSelectionModel().getSelectedItem());
        }
    }

    private void logConnection(String item){
        if (model.showLogConnection(item, connectionSem)) {
            noConnection.setVisible(false);
            listConnection.setVisible(true);
        } else {
            noConnection.setVisible(true);
            listConnection.setVisible(false);
        }
    }

    private void logSend(String item){
        if(model.showLogSend(item, sendSem)){
            noSend.setVisible(false);
            listSend.setVisible(true);
        } else{
            noSend.setVisible(true);
            listSend.setVisible(false);
        }
    }

    private void logErrorSend(String item){
        if(model.showLogErrorSend(item, errorSendSem)){
            noErrorSend.setVisible(false);
            listErrorSend.setVisible(true);
        } else{
            noErrorSend.setVisible(true);
            listErrorSend.setVisible(false);
        }
    }

    private void logReceived(String item){
        if(model.showLogReceived(item, receivedSem)){
            noReceived.setVisible(false);
            listReceived.setVisible(true);
        } else{
            noReceived.setVisible(true);
            listReceived.setVisible(false);
        }
    }

    private void logLogin(String item){
        if(model.showLogLogin(item, loginSem)){
            noLogin.setVisible(false);
            listLogin.setVisible(true);
        } else{
            noLogin.setVisible(true);
            listLogin.setVisible(false);
        }
    }

    private void logLogout(String item){
        if(model.showLogLogout(item, logoutSem)){
            noLogout.setVisible(false);
            listLogout.setVisible(true);
        } else{
            noLogout.setVisible(true);
            listLogout.setVisible(false);
        }
    }

    private void logRemove(String item){
        if(model.showLogRemove(item, removeSem)){
            noRemove.setVisible(false);
            listRemove.setVisible(true);
        } else{
            noRemove.setVisible(true);
            listRemove.setVisible(false);
        }
    }
}
