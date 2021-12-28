package org.prog3.lab.project.model;

import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

public class EmailWriter {

    public String serverSendEmail(User user /*StringProperty emailAddress*/, String receivers, String object, String text){

        String response;

        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("send");
                //operationRequest.add(emailAddress.get());
                operationRequest.add(receivers);
                operationRequest.add(object);
                operationRequest.add(text);

                outStream.writeObject(operationRequest);

                outStream.writeObject(user);

                response = (String) inStream.readObject();

            }finally{
                s.close();
            }

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            response ="Errore di comunicazione con il server.\nControllare le mail inviate prima di procedere con un nuovo invio.";
        }

        return response;
    }

}
