package org.prog3.lab.project.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

public class EmailWriter {

    public String serverSendEmail(User user, String receivers, String object, String text){

        String response;

        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());      //apro un socket in uscita

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());            //apro un socke in entrata

                Vector<String> operationRequest = new Vector<>();                                   //faccio un vector  e aggiungo le operazioni add oggetto receivers object e testo
                operationRequest.add("send");
                operationRequest.add(receivers);
                operationRequest.add(object);
                operationRequest.add(text);

                outStream.writeObject(operationRequest);                                            //invio la richiesta a operationRequest

                outStream.writeObject(user);                                                        //invio a user

                response = (String) inStream.readObject();                                          //leggo la risposta 

            }catch (IOException | ClassNotFoundException e){            
                e.printStackTrace();
                response ="server_error";
            }finally{
                s.close();
            }

        } catch (IOException e){
            e.printStackTrace();
            response ="server_error";
        }

        return response;
    }

}
