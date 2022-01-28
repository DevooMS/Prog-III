package org.prog3.lab.project.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.prog3.lab.project.model.User;
import org.prog3.lab.project.ui.EmailClientController;

public class EmailClientMain extends Application {

    private static User user;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loaderEmailClient = new FXMLLoader(getClass().getResource("../resources/emailClient.fxml"));
        Scene scene = new Scene(loaderEmailClient.load());
        EmailClientController emailClientController = loaderEmailClient.getController();
        emailClientController.initialize(user, stage);                     //faccio partire il controller di EmailClientController.java
        stage.setTitle("Email client");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(650);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();

    }

    public static void main(String[] args) {
        user = new User(args[0]);

        launch();
    }

}
