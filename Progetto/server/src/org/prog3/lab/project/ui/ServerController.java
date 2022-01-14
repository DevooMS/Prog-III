package org.prog3.lab.project.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.prog3.lab.project.model.Server;
import org.prog3.lab.project.model.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Semaphore;


public class ServerController {

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

    private Server model;
    private Stage stage;
    private Semaphore loginSem;
    private Semaphore logoutSem;
    private Semaphore connectionSem;
    private Semaphore sendSem;
    private Semaphore receivedSem;

    @FXML
    public void initialize(Stage stage, Semaphore loginSem, Semaphore logoutSem, Semaphore connectionSem, Semaphore sendSem, Semaphore receivedSem){
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model = new Server();
        this.stage = stage;
        this.loginSem = loginSem;
        this.logoutSem = logoutSem;
        this.connectionSem = connectionSem;
        this.sendSem = sendSem;
        this.receivedSem = receivedSem;

        listClients.itemsProperty().bind(model.listClientsProperty());
        listClients.setOnMouseClicked(this::updateLog);
        listConnection.itemsProperty().bind(model.listConnectionProperty());
        listSend.itemsProperty().bind(model.listSendProperty());
        listReceived.itemsProperty().bind(model.listReceivedProperty());
        listLogin.itemsProperty().bind(model.listLoginProperty());
        listLogout.itemsProperty().bind(model.listLogoutProperty());

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(30), ev -> {
            updateLogForType();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
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
            }
        });

        model.addUser("./server/src/org/prog3/lab/project/resources/userEmails.txt");
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
            logConnection((String) listClients.getSelectionModel().getSelectedItem(), connectionSem);

            logSend((String) listClients.getSelectionModel().getSelectedItem(), sendSem);

            logReceived((String) listClients.getSelectionModel().getSelectedItem(), receivedSem);

            logLogin((String) listClients.getSelectionModel().getSelectedItem(), loginSem);

            logLogout((String) listClients.getSelectionModel().getSelectedItem(), logoutSem);
        }
    }

    private void logConnection(String item, Semaphore connectionSem){
        if (model.showLogConnection(item, connectionSem)) {
            noConnection.setVisible(false);
            listConnection.setVisible(true);
        } else {
            noConnection.setVisible(true);
            listConnection.setVisible(false);
        }
    }

    private void logSend(String item, Semaphore sendSem){
        if(model.showLogSend(item, sendSem)){
            noSend.setVisible(false);
            listSend.setVisible(true);
        } else{
            noSend.setVisible(true);
            listSend.setVisible(false);
        }
    }

    private void logReceived(String item, Semaphore receivedSem){
        if(model.showLogReceived(item, receivedSem)){
            noReceived.setVisible(false);
            listReceived.setVisible(true);
        } else{
            noReceived.setVisible(true);
            listReceived.setVisible(false);
        }
    }

    private void logLogin(String item, Semaphore loginSem){
        if(model.showLogLogin(item, loginSem)){
            noLogin.setVisible(false);
            listLogin.setVisible(true);
        } else{
            noLogin.setVisible(true);
            listLogin.setVisible(false);
        }
    }

    private void logLogout(String item, Semaphore logoutSem){
        if(model.showLogLogout(item, logoutSem)){
            noLogout.setVisible(false);
            listLogout.setVisible(true);
        } else{
            noLogout.setVisible(true);
            listLogout.setVisible(false);
        }
    }
}
