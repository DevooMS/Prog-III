package org.prog3.lab.project.ui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.prog3.lab.project.model.Server;
//import org.prog3.lab.project.model.Login;
//import org.prog3.lab.project.ui.LoginController;


public class ServerController {

    private Server model;
    @FXML
    public void initialize(Server model){
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model = model;
    }




    
}
