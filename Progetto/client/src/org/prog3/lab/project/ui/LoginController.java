package org.prog3.lab.project.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.prog3.lab.project.model.EmailClient;
import org.prog3.lab.project.model.Login;

public class LoginController {
    @FXML
    private TextField fieldEmail;

    @FXML
    private TextField fieldPassword;

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
        btnLogin.setOnAction(ActionEvent -> {
            try {
                btnLoginClick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    protected void userDataClick(MouseEvent mouseEvent) {
        labelResult.setText("");
    }


    protected void btnLoginClick() throws Exception{
        labelResult.setStyle("-fx-text-fill:GREEN");
        labelResult.setText("Login incorso. Attendere...");
        String access = model.searchUser(fieldEmail.getText(), fieldPassword.getText());
        //System.out.println(access);
        if (access.contains("accept")) {
            loadEmailClient();
        } else {
            labelResult.setStyle("-fx-text-fill:RED");
            labelResult.setText("Email o password errati. Riprovare!");
        }
        //model.searchUser(fieldEmail.getText(), fieldPassword.getText());
    }

    public void loadEmailClient() throws Exception{
        try{
            FXMLLoader loaderEmailClient = new FXMLLoader(getClass().getResource("../resources/emailClient.fxml"));
            Scene scene = new Scene(loaderEmailClient.load());
            EmailClientController emailClientController = loaderEmailClient.getController();
            EmailClient model = new EmailClient(fieldEmail.getText());
            emailClientController.initialize(model, stage);
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
