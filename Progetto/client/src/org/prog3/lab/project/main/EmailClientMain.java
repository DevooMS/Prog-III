package org.prog3.lab.project.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.prog3.lab.project.ui.LoginController;

public class EmailClientMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loaderLogin = new FXMLLoader(getClass().getResource("../resources/login.fxml"));  //carico fxml
        Scene scene = new Scene(loaderLogin.load());                                                       //creo nuova scena

        LoginController loginController = loaderLogin.getController();
        //Login model = new Login();                                             //model una nuova instanza del model package login
        loginController.initialize(/*model,*/ stage);                            //faccio il stage
        stage.setTitle("User Login");
        stage.setScene(scene);
        stage.setMinWidth(300);                                                  //grandezza della scena
        stage.setMinHeight(300);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
