package org.prog3.lab.project.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.prog3.lab.project.model.Server;
import org.prog3.lab.project.threadModel.LoginTask;
import org.prog3.lab.project.threadModel.UpdateTask;
import org.prog3.lab.project.ui.ServerController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerMain extends Application {

    private static final int NUM_THREAD = 1;

    public static void main(String[] args) throws IOException {
        try {
            ServerSocket s = new ServerSocket(8190);

            ExecutorService loginThreads = Executors.newFixedThreadPool(NUM_THREAD);
            ExecutorService updateThreads = Executors.newFixedThreadPool(NUM_THREAD);

            try{
                while(true) {
                    Socket incoming = s.accept();

                    ObjectOutputStream outObjectStream = new ObjectOutputStream(incoming.getOutputStream());
                    ObjectInputStream inStream = new ObjectInputStream(incoming.getInputStream());

                    OutputStream outSimpleStream = incoming.getOutputStream();
                    PrintWriter out = new PrintWriter(outSimpleStream, true);

                    Vector<String> v = null;

                    try {
                        v = ((Vector<String>) inStream.readObject());
                    } catch (ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                    }

                    String operation = null;

                    if (v != null)
                        operation = v.get(0);

                    switch (operation) {
                        case "login":
                            String filePath = "./server/src/org/prog3/lab/project/resources/userEmails";
                            Runnable loginTask = new LoginTask(v.get(1), v.get(2), filePath, out);
                            loginThreads.execute(loginTask);
                            break;
                        case "update":
                            //System.out.println(v.get(1)+" "+v.get(2));
                            //System.out.println("ok");
                            String directoryPath = "./server/src/org/prog3/lab/project/resources/userClients/"+v.get(1)+"/"+v.get(2);
                            Runnable updateTask = new UpdateTask(directoryPath, Boolean.parseBoolean(v.get(3)), out, outObjectStream);
                            //System.out.println(v.get(3));
                            updateThreads.execute(updateTask);
                            break;
                        default:
                            new IOException();
                            break;
                    }
                }
            } finally {
                s.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        //launch();
    }



    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loaderServerPanel = new FXMLLoader(getClass().getResource("../ui/panelAdmin.fxml"));
        Scene scene = new Scene(loaderServerPanel.load());

        ServerController loaderController = loaderServerPanel.getController();
        Server model = new Server();
        loaderController.initialize(model);
        stage.setTitle("main page");
        stage.setScene(scene);
        stage.setMinWidth(741);
        stage.setMinHeight(535);
        stage.setResizable(false);
        stage.show();


    }

}
