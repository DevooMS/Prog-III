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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EmailClient {
    private final ObservableList<Email> listReceivedEmails;
    private final ObservableList<Email> listSendedEmails;
    private final ListProperty<Email> sendedEmailsProperty;
    private final ListProperty<Email> receivedEmailsProperty;
    private final StringProperty emailAddress;

    public EmailClient(String emailAddress){
        this.listReceivedEmails = FXCollections.observableArrayList();
        this.listSendedEmails = FXCollections.observableArrayList();
        this.receivedEmailsProperty = new SimpleListProperty<>();
        this.receivedEmailsProperty.set(listReceivedEmails);
        this.sendedEmailsProperty = new SimpleListProperty<>();
        this.sendedEmailsProperty.set(listSendedEmails);
        this.emailAddress = new SimpleStringProperty(emailAddress);
    }

    //public ListProperty<Email> emailsListProperty(){ return emailsProperty;}

    public ListProperty<Email> receivedEmailsProperty(){ return receivedEmailsProperty;}

    public ListProperty<Email> sendedEmailsProperty(){ return sendedEmailsProperty;}

    public StringProperty emailAddressProperty(){
        return emailAddress;
    }

    public void deleteEmail(Email email) {
        listSendedEmails.remove(email);
    }

    public void updatelistReceivedEmails(){
        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {
                //InputStream inStream = s.getInputStream();
                //Scanner in = new Scanner(inStream);

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("update");
                operationRequest.add(emailAddressProperty().get());
                operationRequest.add("receivedEmails");

                outStream.writeObject(operationRequest);

                int countEmails = (Integer) inStream.readObject();

                Vector<String> emailDetail = new Vector<>();

                for(int i=0; i<countEmails; i++) {

                    int j = 0;

                    String email = (String) inStream.readObject();
                    //System.out.println(email);


                    do {
                        emailDetail.add(j, email);
                        //System.out.println(email);
                        email = (String) inStream.readObject();
                        j++;

                    } while (!email.equals("--END_EMAIL--"));

                    Email e = new Email("1", emailDetail.get(0), Collections.singletonList(emailDetail.get(1)), emailDetail.get(2), emailDetail.get(3), emailDetail.get(4), emailDetail.get(5));
                    listReceivedEmails.add(e);

                }

            }finally{
                s.close();
            }
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void updatelistSendedEmails(){
        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {
                //InputStream inStream = s.getInputStream();
                //Scanner in = new Scanner(inStream);

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("update");
                operationRequest.add(emailAddressProperty().get());
                operationRequest.add("sendedEmails");

                outStream.writeObject(operationRequest);

                int countEmails = (Integer) inStream.readObject();

                Vector<String> emailDetail = new Vector<>();

                for(int i=0; i<countEmails; i++) {

                    int j = 0;

                    String email = (String) inStream.readObject();
                    //System.out.println(email);


                    do {
                        emailDetail.add(j, email);
                        //System.out.println(email);
                        email = (String) inStream.readObject();
                        j++;

                    } while (!email.equals("--END_EMAIL--"));

                    Email e = new Email("1", emailDetail.get(0), Collections.singletonList(emailDetail.get(1)), emailDetail.get(2), emailDetail.get(3), emailDetail.get(4), emailDetail.get(5));
                    listSendedEmails.add(e);

                }

            }finally{
                s.close();
            }
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
