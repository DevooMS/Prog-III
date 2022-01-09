package org.prog3.lab.project.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class EmailClient {
    private final ObservableList<Email> listReceivedEmails;    //gli salvo dentro la lista di email da email.java
    private final ObservableList<Email> listSendedEmails;
    private final ListProperty<Email> sendedEmailsProperty;
    private final ListProperty<Email> receivedEmailsProperty;
    private final StringProperty emailAddress;

    public EmailClient(String emailAddress){
        this.listReceivedEmails = FXCollections.observableArrayList();
        this.listSendedEmails = FXCollections.observableArrayList();
        this.receivedEmailsProperty = new SimpleListProperty<>();
        this.receivedEmailsProperty.set(listReceivedEmails);             //gli sto associando il suo observer cioe ListrecivedEmails
        this.sendedEmailsProperty = new SimpleListProperty<>();
        this.sendedEmailsProperty.set(listSendedEmails);
        this.emailAddress = new SimpleStringProperty(emailAddress);
    }

    public ListProperty<Email> receivedEmailsProperty(){ return receivedEmailsProperty;}

    public ListProperty<Email> sendedEmailsProperty(){ return sendedEmailsProperty;}

    public StringProperty emailAddressProperty(){
        return emailAddress;
    }

    public boolean deleteEmail(User user, Email email) {
        boolean result = serverRemoveEmail(user, email.getType(), email.getId());

        if(result) {
            if (email.getType().equals("receivedEmails"))
                listReceivedEmails.remove(email);
            else
                listSendedEmails.remove(email);
        }

        return result;
    }

    public int updateEmailslists(User user, boolean updateSended, boolean startUpdate) {

        int countNewEmails;

        if (updateSended)
            countNewEmails = serverRequestUpdateList(user, listSendedEmails, "sendedEmails", startUpdate);

        countNewEmails = serverRequestUpdateList(user, listReceivedEmails, "receivedEmails", startUpdate);

        return countNewEmails;

    }

    private int serverRequestUpdateList(User user, ObservableList list, String mailType, boolean startUpdate){

        int countEmails = 0;

        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("update");
                //operationRequest.add(emailAddressProperty().get());
                operationRequest.add(mailType);
                operationRequest.add(String.valueOf(startUpdate));

                outStream.writeObject(operationRequest);

                outStream.writeObject(user);

                countEmails = (Integer) inStream.readObject();

                Vector<String> emailDetail = new Vector<>();

                for(int i=0; i<countEmails; i++) {

                    int j = 0;

                    String email = (String) inStream.readObject();

                    while (!email.equals("--END_EMAIL--")) {
                        emailDetail.add(j, email);
                        j++;
                        email = (String) inStream.readObject();
                    }

                    if(emailDetail.size() > 0) {
                        Email e;

                        if(mailType.equals("receivedEmails"))
                             e = new Email(emailDetail.get(0), mailType, emailDetail.get(1), emailDetail.get(2), emailDetail.get(3), emailDetail.get(4), emailDetail.get(5));   //nuovo oggetto email
                        else
                             e = new Email(emailDetail.get(0), mailType, emailDetail.get(2), emailDetail.get(1), emailDetail.get(3), emailDetail.get(4), emailDetail.get(5));
                        list.add(e);
                    }

                }

                countEmails = (Integer) inStream.readObject();

            }finally{
                s.close();
            }

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            countEmails = -1;
        }

        return countEmails;

    }

    private boolean serverRemoveEmail(User user, String emailType, String emailId){

        boolean result;

        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("remove");
                //operationRequest.add(emailAddressProperty().get());
                operationRequest.add(emailType);
                operationRequest.add(emailId);

                outStream.writeObject(operationRequest);

                outStream.writeObject(user);

                result = true;
            }finally{
                s.close();
            }

        } catch (IOException e) {
            e.printStackTrace();

            result = false;
        }
        return result;
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

            }finally{
                s.close();
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
