package org.prog3.lab.project.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.prog3.lab.project.model.Login;
import org.prog3.lab.project.ui.LoginController;

public class EmailClientMain extends Application {

    private static String data;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loaderLogin = new FXMLLoader(getClass().getResource("../resources/login.fxml"));
        Scene scene = new Scene(loaderLogin.load());

        LoginController loginController = loaderLogin.getController();
        Login model = new Login();
        loginController.initialize(model, stage);
        stage.setTitle("User Login");
        stage.setScene(scene);
        stage.setMinWidth(300);
        stage.setMinHeight(300);
        stage.setResizable(false);
        stage.show();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        data = formatter.format(LocalDateTime.now());
        //Email a = new Email("1", "", Collections.singletonList(""), "", data.toString(),"");
        //list.add(a);

    }

    public static void main(String[] args) {
        launch();
    }

    public static String getDate(){
        return data.toString();
    }
}
