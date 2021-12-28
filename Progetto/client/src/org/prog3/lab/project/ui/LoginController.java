package org.prog3.lab.project.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.prog3.lab.project.model.EmailClient;
import org.prog3.lab.project.model.Login;
import org.prog3.lab.project.model.User;


public class LoginController {
    private User user;

    @FXML
    private TextField fieldEmail;

    @FXML
    private PasswordField fieldPassword;

    @FXML
    private Label labelResult;

    @FXML
    private Button btnLogin;

    private Login model;
    private Stage stage;

    @FXML
    public void initialize(Login model, Stage stage){
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model = model;
        this.stage = stage;

        fieldEmail.setOnMouseClicked(this::userDataClick);
        fieldPassword.setOnMouseClicked(this::userDataClick);
        btnLogin.setOnAction(this::btnLoginClick);
    }

    private void btnLoginClick(ActionEvent actionEvent) {

        try {
            labelResult.setStyle("-fx-text-fill:GREEN");
            labelResult.setText("Login incorso. Attendere...");
            String access = model.searchUser(fieldEmail.getText(), fieldPassword.getText());
            //System.out.println(access);
            if (access.equals("accept")) {
                user = model.getUser();
                loadEmailClient(user);
            } else if (access.equals("denied")) {
                labelResult.setStyle("-fx-text-fill:RED");
                labelResult.setText("Email o password errati. Riprovare!");
            } else {
                labelResult.setStyle("-fx-text-fill:RED");
                labelResult.setText("Errore di connessione al server.");
            }
        } catch (Exception e){
            System.out.println("Exception: "+ e.getMessage());
        }

    }

    protected void userDataClick(MouseEvent mouseEvent) {
        labelResult.setText("");
    }



    public void loadEmailClient(User user) {
        try{
            FXMLLoader loaderEmailClient = new FXMLLoader(getClass().getResource("../resources/emailClient.fxml"));
            Scene scene = new Scene(loaderEmailClient.load());
            EmailClientController emailClientController = loaderEmailClient.getController();
            EmailClient model = new EmailClient(fieldEmail.getText());
            emailClientController.initialize(model, user, stage);
            stage.setTitle("Email client");
            stage.setScene(scene);
            stage.setMinWidth(950);
            stage.setMinHeight(650);
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
