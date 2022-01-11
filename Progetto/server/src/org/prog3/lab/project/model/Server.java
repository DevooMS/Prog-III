package org.prog3.lab.project.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Server {
    private final ObservableList<String> listClients;
    private final ListProperty<String> listClientsProperty;
    private final ObservableList<String> listConnection;
    private final ListProperty<String> listConnectionProperty;
    private final ObservableList<String> listSend;
    private final ListProperty<String> listSendProperty;
    private final ObservableList<String> listReceived;
    private final ListProperty<String> listReceivedProperty;
    private final ObservableList<String> listLogin;
    private final ListProperty<String> listLoginProperty;
    private final ObservableList<String> listLogout;
    private final ListProperty<String> listLogoutProperty;

    public Server(){
        this.listClients = FXCollections.observableArrayList();
        this.listClientsProperty = new SimpleListProperty<>();
        this.listClientsProperty.set(listClients);
        this.listConnection = FXCollections.observableArrayList();
        this.listConnectionProperty = new SimpleListProperty<>();
        this.listConnectionProperty.set(listConnection);
        this.listSend = FXCollections.observableArrayList();
        this.listSendProperty = new SimpleListProperty<>();
        this.listSendProperty.set(listSend);
        this.listReceived = FXCollections.observableArrayList();
        this.listReceivedProperty = new SimpleListProperty<>();
        this.listReceivedProperty.set(listReceived);
        this.listLogin = FXCollections.observableArrayList();
        this.listLoginProperty = new SimpleListProperty<>();
        this.listLoginProperty.set(listLogin);
        this.listLogout = FXCollections.observableArrayList();
        this.listLogoutProperty = new SimpleListProperty<>();
        this.listLogoutProperty.set(listLogout);
    }

    public ListProperty<String> listClientsProperty(){ return listClientsProperty;}

    public ListProperty<String> listConnectionProperty(){ return listConnectionProperty;}

    public ListProperty<String> listSendProperty(){ return listSendProperty;}

    public ListProperty<String> listReceivedProperty(){ return listReceivedProperty;}

    public ListProperty<String> listLoginProperty(){ return listLoginProperty;}

    public ListProperty<String> listLogoutProperty(){ return listLogoutProperty;}

    public void addUser(String filePath){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;

        try {
            line = reader.readLine();

            while (line != null) {
                int pos = line.indexOf('-');
                String lineClient = line.substring(0,pos);
                listClients.add(lineClient);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showLogConnection(String user){
        return showLog(listConnection, "./server/src/org/prog3/lab/project/resources/log/connection/"+user);
    }

    public boolean showLogSend(String user){
        return showLog(listSend, "./server/src/org/prog3/lab/project/resources/log/send/"+user);
    }

    public boolean showLogReceived(String user){
        return showLog(listReceived, "./server/src/org/prog3/lab/project/resources/log/received/"+user);
    }

    public boolean showLogLogin(String user){
        return showLog(listLogin,"./server/src/org/prog3/lab/project/resources/log/login/"+user);
    }

    public boolean showLogLogout(String user){
        return showLog(listLogout,"./server/src/org/prog3/lab/project/resources/log/logout/"+user);
    }

    private boolean showLog(ObservableList<String> list, String filePath){
        boolean find = false;

        list.clear();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;

        try {
            line = reader.readLine();

            while (line != null) {
                if(!line.equals("")) {
                    list.add(line);
                    if(!find)
                        find = true;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return find;
    }
}
