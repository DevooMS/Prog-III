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

        FXMLLoader loaderEmailClient = new FXMLLoader(getClass().getResource("../resources/emailClient.fxml")); //carico il file fxml
        Scene scene = new Scene(loaderEmailClient.load());                  //scena nuova
        EmailClientController emailClientController = loaderEmailClient.getController();
        stage.setTitle("Email client");                                    //imposto la grandezza il titolo
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(650);
        stage.setResizable(true);
        stage.setMaximized(true);
        emailClientController.initialize(user, stage);                      //faccio partire il controller di EmailClientController.java faccendo initialize in EmailClientController.java
        //stage.show();

    }

    public static void main(String[] args) {
        user = new User(args[0]);       // fa un oggetto user gli passa useremail dal argument

        launch();                       //invoca il metodo start
    }

}
