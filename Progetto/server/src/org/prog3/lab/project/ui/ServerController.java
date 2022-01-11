package org.prog3.lab.project.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.prog3.lab.project.model.Server;


public class ServerController {

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

    @FXML
    public void initialize(/*Login model,*/ Stage stage){
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model = new Server();
        this.stage = stage;

        listClients.itemsProperty().bind(model.listClientsProperty());
        listClients.setOnMouseClicked(this::showLog);
        listConnection.itemsProperty().bind(model.listConnectionProperty());
        listSend.itemsProperty().bind(model.listSendProperty());
        listReceived.itemsProperty().bind(model.listReceivedProperty());
        listLogin.itemsProperty().bind(model.listLoginProperty());
        listLogout.itemsProperty().bind(model.listLogoutProperty());

        model.addUser("./server/src/org/prog3/lab/project/resources/userEmails.txt");
    }

    private void showLog(MouseEvent mouseEvent) {
        if(model.showLogConnection((String) listClients.getSelectionModel().getSelectedItem())){
            noConnection.setVisible(false);
            listConnection.setVisible(true);
        } else{
            noConnection.setVisible(true);
            listConnection.setVisible(false);
        }

        if(model.showLogSend((String) listClients.getSelectionModel().getSelectedItem())){
            noSend.setVisible(false);
            listSend.setVisible(true);
        } else{
            noSend.setVisible(true);
            listSend.setVisible(false);
        }

        if(model.showLogReceived((String) listClients.getSelectionModel().getSelectedItem())){
            noReceived.setVisible(false);
            listReceived.setVisible(true);
        } else{
            noReceived.setVisible(true);
            listReceived.setVisible(false);
        }

        if(model.showLogLogin((String) listClients.getSelectionModel().getSelectedItem())){
            noLogin.setVisible(false);
            listLogin.setVisible(true);
        } else{
            noLogin.setVisible(true);
            listLogin.setVisible(false);
        }

        if(model.showLogLogout((String) listClients.getSelectionModel().getSelectedItem())){
            noLogout.setVisible(false);
            listLogout.setVisible(true);
        } else{
            noLogout.setVisible(true);
            listLogout.setVisible(false);
        }
    }

}
