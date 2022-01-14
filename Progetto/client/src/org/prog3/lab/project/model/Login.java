package org.prog3.lab.project.model;

import javafx.scene.control.Label;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

public class Login {

    User user;

    public String searchUser(String userEmail, String userPassword) {

        String response = "denied";

        try {

            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);          //apro un scoket con la porta 8190

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());      //faccio un oggetto di outStream

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());          //faccio un oggetto di inputStream

                Vector<String> operationRequest = new Vector<>();                                //creo un array Vector
                operationRequest.add("login");                                                   //aggiungo un request di tipo string login
                operationRequest.add(userEmail);                                                 //aggiungo un request di tipo string userEmail
                operationRequest.add(userPassword);                                              //aggiungo un request di tipo string userPassword

                outStream.writeObject(operationRequest);                                         //invio questo vector sul outStream

                user = (User) inStream.readObject();                                             //assegno oggetto al user di inputstream

                response = (String) inStream.readObject();                                       //prendo la stringa di response

                return response;
            }finally{
                s.close();                                                                        //chiudi il socket
            }

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            response = "error";
        }

        return response;
    }

    public User getUser(){
        return user;
    }
}
