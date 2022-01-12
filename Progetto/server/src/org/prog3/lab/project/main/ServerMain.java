package org.prog3.lab.project.main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.prog3.lab.project.model.User;
import org.prog3.lab.project.threadModel.*;
import org.prog3.lab.project.ui.ServerController;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ServerMain extends Application {

    private static final int NUM_THREAD = 3;
    static Semaphore loginSem = new Semaphore(1);
    static Semaphore logoutSem = new Semaphore(1);
    static Semaphore connectionSem = new Semaphore(1);
    static Semaphore sendSem = new Semaphore(1);
    static Semaphore receivedSem = new Semaphore(1);

    public static void main(String[] args) throws Exception {

        ServerThread st = new ServerThread(loginSem, logoutSem, connectionSem, sendSem, receivedSem);
        Thread t = new Thread(st);
        t.start();

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
        serverController.initialize(stage, loginSem, logoutSem, connectionSem, sendSem, receivedSem);
        stage.setTitle("main page");
        stage.setScene(scene);
        stage.setMinWidth(741);
        stage.setMinHeight(535);
        stage.setResizable(false);
        stage.show();
    }
}
