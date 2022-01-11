package org.prog3.lab.project.threadModel;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.prog3.lab.project.model.User;
import org.prog3.lab.project.ui.LoginController;
import org.prog3.lab.project.ui.ServerController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

public class ServerThread extends Application implements Runnable {

    @Override
    public void run() {
        launch();
    }

    @Override
    public void start(Stage stage) {
        FXMLLoader loaderServerPanel = new FXMLLoader(getClass().getResource("../ui/panelAdmin.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(loaderServerPanel.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerController serverController = loaderServerPanel.getController();
        serverController.initialize(stage);
        stage.setTitle("main page");
        stage.setScene(scene);
        stage.setMinWidth(741);
        stage.setMinHeight(535);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
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


    }
}
