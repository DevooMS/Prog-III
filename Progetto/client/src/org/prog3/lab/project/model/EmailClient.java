package org.prog3.lab.project.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class EmailClient {
    private final ObservableList<Email> listReceivedEmails;    //gli salvo dentro la lista di email da email.java utilizza Observer e Property per fare il bind
    private final ObservableList<Email> listSendedEmails;      //e una colezione oservabile di Email
    private final ListProperty<Email> sendedEmailsProperty;     //
    private final ListProperty<Email> receivedEmailsProperty;

    public EmailClient(){
        this.listReceivedEmails = FXCollections.observableArrayList();       //creo un Observable List per listReceivedEmails
        this.receivedEmailsProperty = new SimpleListProperty<>();           //creo un SimpleList
        this.receivedEmailsProperty.set(listReceivedEmails);               //PropertyList dentro quale ho un Observable List di listReceivedEmails
        this.listSendedEmails = FXCollections.observableArrayList();     //creo un Observable List
        this.sendedEmailsProperty = new SimpleListProperty<>(); 
        this.sendedEmailsProperty.set(listSendedEmails);                  //faccio la stessa cosa per listSendedEmails
    }

    public ListProperty<Email> receivedEmailsProperty(){ return receivedEmailsProperty;}   //restituisce receivedEmails

    public ListProperty<Email> sendedEmailsProperty(){ return sendedEmailsProperty;}        //restituisce sendedEmails

    public void serverLogin(User user){
        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("login");

                outStream.writeObject(operationRequest);

                outStream.writeObject(user);

            } catch (IOException e){
                e.printStackTrace();
            } finally{
                s.close();
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String deleteEmail(User user, Email email) {                             //chimatao da btnDelete
        String response = serverRemoveEmail(user, email.getType(), email.getId());   //chiama serverRemoveEmail e apre un socket

        if(response.equals("remove_correct")) {
            if (email.getType().equals("receivedEmails")) {
                user.removeReceivedEmail(email);
                listReceivedEmails.clear();
                listReceivedEmails.setAll(user.getReceivedEmails());
            }else
                listSendedEmails.remove(email);
                user.removeSendedEmail(email);
                listSendedEmails.clear();
                listSendedEmails.setAll(user.getSendedEmails());
        }

        return response;
    }

    public int updateEmailslists(User user, boolean updateSended, boolean startUpdate) {     //aggiorna email con il serverRequestUpdateList socket

        int countNewEmails;

        if (updateSended)
            serverRequestUpdateList(user,"sendedEmails", startUpdate);

        countNewEmails = serverRequestUpdateList(user, "receivedEmails", startUpdate);

        return countNewEmails;

    }

    private int serverRequestUpdateList(User user, String mailType, boolean startUpdate) {

        int countEmails;

        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("update");
                operationRequest.add(mailType);
                operationRequest.add(String.valueOf(startUpdate));

                outStream.writeObject(operationRequest);

                outStream.writeObject(user);

                countEmails = (Integer) inStream.readObject();

                Vector<String> emailDetail = new Vector<>();

                for (int i = 0; i < countEmails; i++) {

                    int j = 0;

                    String email = (String) inStream.readObject();

                    while (!email.equals("--END_EMAIL--")) {
                        emailDetail.add(j, email);
                        j++;
                        email = (String) inStream.readObject();
                    }

                    if (emailDetail.size() > 0) {
                        Email e;

                        if (mailType.equals("receivedEmails")) {
                            e = new Email(emailDetail.get(0), mailType, emailDetail.get(1), emailDetail.get(2), emailDetail.get(3), emailDetail.get(4), emailDetail.get(5));   //nuovo oggetto email
                            user.addReceivedEmail(e);
                        }else {
                            e = new Email(emailDetail.get(0), mailType, emailDetail.get(2), emailDetail.get(1), emailDetail.get(3), emailDetail.get(4), emailDetail.get(5));
                            user.addSendedEmails(e);
                        }
                    }

                }

                if (mailType.equals("receivedEmails")) {
                    listReceivedEmails.clear();
                    listReceivedEmails.addAll(user.getReceivedEmails());
                }else {
                    listSendedEmails.clear();
                    listSendedEmails.addAll(user.getSendedEmails());
                }

                countEmails = (Integer) inStream.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                countEmails = -1;
            } finally {
                s.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            countEmails = -1;
        }

        return countEmails;
    }

    private String serverRemoveEmail(User user, String emailType, String emailId){

        String response;

        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("remove");
                operationRequest.add(emailType);
                operationRequest.add(emailId);

                outStream.writeObject(operationRequest);

                outStream.writeObject(user);

                response = (String) inStream.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();

                response = "server_error";
            } finally{
                s.close();
            }

        } catch (IOException e) {
            e.printStackTrace();

            response = "server_error";
        }
        return response;
    }

    public void serverLogout(User user){
        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("logout");

                outStream.writeObject(operationRequest);

                outStream.writeObject(user);

            }catch (IOException e){
                e.printStackTrace();
            } finally{
                s.close();
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
