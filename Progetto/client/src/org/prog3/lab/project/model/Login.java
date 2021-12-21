package org.prog3.lab.project.model;

import javafx.scene.control.Label;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

public class Login {

    public String searchUser(String userEmail, String userPassword) {

        String response = "denied";

        try {

            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("login");
                operationRequest.add(userEmail);
                operationRequest.add(userPassword);

                outStream.writeObject(operationRequest);

                response = (String) inStream.readObject();

                return response;
            }finally{
                s.close();
            }

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            response = "error";
        }

        return response;
    }
}
