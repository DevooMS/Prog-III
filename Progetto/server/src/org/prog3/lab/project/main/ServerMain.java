package org.prog3.lab.project.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.prog3.lab.project.threadModel.*;
import org.prog3.lab.project.ui.ServerController;

import java.io.*;
import java.util.concurrent.Semaphore;

public class ServerMain extends Application {

    private static final Semaphore loginSem = new Semaphore(1);
    private static final Semaphore logoutSem = new Semaphore(1);
    private static final Semaphore connectionSem = new Semaphore(1);
    private static final Semaphore sendSem = new Semaphore(1);
    private static final Semaphore errorSendSem = new Semaphore(1);
    private static final Semaphore receivedSem = new Semaphore(1);
    private static final Semaphore removeSem = new Semaphore(1);

    public static void main(String[] args) {

        ServerThread st = new ServerThread(loginSem, logoutSem, connectionSem, sendSem, errorSendSem, receivedSem, removeSem);  //prepare di Thread con dentro i semafori binari e chiamo ServerThread
        Thread t = new Thread(st);                                                                                              //faccio partire un thread per ServerThread
        t.start();

        launch();                                                                                                               //chiamo la start
    }

    @Override
    public void start(Stage stage) {
        FXMLLoader loaderServerPanel = new FXMLLoader(getClass().getResource("../resources/panelAdmin.fxml"));                 //carico fxml        
        Scene scene = null;
        try {
            scene = new Scene(loaderServerPanel.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerController serverController = loaderServerPanel.getController();
        serverController.initialize(stage, loginSem, logoutSem, connectionSem, sendSem, errorSendSem, receivedSem, removeSem);  //chiamo initialize di ServerController
        stage.setTitle("main page");
        stage.setScene(scene);
        stage.setMinWidth(741);
        stage.setMinHeight(535);
        stage.setResizable(false);
        stage.show();
    }
}
