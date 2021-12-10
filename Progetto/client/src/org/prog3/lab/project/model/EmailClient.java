package org.prog3.lab.project.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.List;

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

    public void deleteEmail(Email email) {
        listSendedEmails.remove(email);
    }

    public int updateEmailslists(boolean updateSended, boolean startUpdate){

        int countNewEmails = serverRequestUpdateList(listReceivedEmails, "receivedEmails", startUpdate);

        if(updateSended)
            serverRequestUpdateList(listSendedEmails, "sendedEmails", startUpdate);

        return countNewEmails;

    }

    private int serverRequestUpdateList(List list, String mailsType, boolean startUpdate){

        int countEmails = 0;

        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8190);

            try {

                ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());

                ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());

                Vector<String> operationRequest = new Vector<>();
                operationRequest.add("update");
                operationRequest.add(emailAddressProperty().get());
                operationRequest.add(mailsType);
                operationRequest.add(String.valueOf(startUpdate));

                outStream.writeObject(operationRequest);

                countEmails = (Integer) inStream.readObject();

                Vector<String> emailDetail = new Vector<>();

                for(int i=0; i<countEmails; i++) {

                    int j = 0;

                    String email = (String) inStream.readObject();

                    while (!email.equals("--END_EMAIL--")) {
                        emailDetail.add(j, email);
                        j++;
                        //System.out.println(email);
                        email = (String) inStream.readObject();
                    }

                    if(emailDetail.size() > 0) {
                        //System.out.println(emailDetail.get(0)+" "+emailDetail.get(1)+" "+Collections.singletonList(emailDetail.get(2))+" "+emailDetail.get(3)+" "+emailDetail.get(4)+" "+emailDetail.get(5));
                        Email e = new Email(emailDetail.get(0), emailDetail.get(1), Collections.singletonList(emailDetail.get(2)), emailDetail.get(3), emailDetail.get(4), emailDetail.get(5));   //nuovo oggetto email 
                        list.add(e);
                    }

                }

                //Collections.sort(list, Comparator.comparing((Email email) -> email.getDate()));
                //if(list.size() > 0)
                Collections.reverse(list);

                countEmails = (Integer) inStream.readObject();

            }finally{
                s.close();
            }

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

        System.out.println(countEmails);
        return countEmails;

    }

}
