package org.prog3.lab.project.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.concurrent.Semaphore;

public class Server {
    private final ObservableList<String> listClients;
    private final ListProperty<String> listClientsProperty;
    private final ObservableList<String> listConnection;
    private final ListProperty<String> listConnectionProperty;
    private final ObservableList<String> listSend;
    private final ListProperty<String> listSendProperty;
    private final ObservableList<String> listErrorSend;
    private final ListProperty<String> listErrorSendProperty;
    private final ObservableList<String> listReceived;
    private final ListProperty<String> listReceivedProperty;
    private final ObservableList<String> listLogin;
    private final ListProperty<String> listLoginProperty;
    private final ObservableList<String> listLogout;
    private final ListProperty<String> listLogoutProperty;
    private final ObservableList<String> listRemove;
    private final ListProperty<String> listRemoveProperty;

    public Server(){
        this.listClients = FXCollections.observableArrayList();
        this.listClientsProperty = new SimpleListProperty<>();
        this.listClientsProperty.set(listClients);
        this.listConnection = FXCollections.observableArrayList();
        this.listConnectionProperty = new SimpleListProperty<>();
        this.listConnectionProperty.set(listConnection);
        this.listSend = FXCollections.observableArrayList();
        this.listSendProperty = new SimpleListProperty<>();
        this.listErrorSend = FXCollections.observableArrayList();
        this.listErrorSendProperty = new SimpleListProperty<>();
        this.listErrorSendProperty.set(listErrorSend);
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
        this.listRemove = FXCollections.observableArrayList();
        this.listRemoveProperty = new SimpleListProperty<>();
        this.listRemoveProperty.set(listRemove);
    }

    public ListProperty<String> listClientsProperty(){ return listClientsProperty;}

    public ListProperty<String> listConnectionProperty(){ return listConnectionProperty;}

    public ListProperty<String> listSendProperty(){ return listSendProperty;}

    public ListProperty<String> listErrorSendProperty(){ return listErrorSendProperty;}

    public ListProperty<String> listReceivedProperty(){ return listReceivedProperty;}

    public ListProperty<String> listLoginProperty(){ return listLoginProperty;}

    public ListProperty<String> listLogoutProperty(){ return listLogoutProperty;}

    public ListProperty<String> listRemoveProperty(){ return listRemoveProperty;}

    public void addUser(String filePath){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            String user = reader.readLine();

            while (user != null) {
                listClients.add(user);
                user = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showLogConnection(String user, Semaphore connectionSem){
        boolean ris = false;
        try {
            connectionSem.acquire();
            ris =  showLog(listConnection, getClass().getResource("../resources/log/connection/" + user).getPath());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            connectionSem.release();
        }

        return ris;
    }

    public boolean showLogSend(String user, Semaphore sendSem){
        boolean ris = false;

        try{
            sendSem.acquire();
            ris = showLog(listSend, getClass().getResource("../resources/log/send/" + user).getPath());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sendSem.release();
        }

        return ris;
    }

    public boolean showLogErrorSend(String user, Semaphore sendSem){
        boolean ris = false;

        try{
            sendSem.acquire();
            ris = showLog(listErrorSend, getClass().getResource("../resources/log/errorSend/" + user).getPath());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sendSem.release();
        }

        return ris;
    }

    public boolean showLogReceived(String user, Semaphore receivedSem){
        boolean ris = false;

        try{
            receivedSem.acquire();
            ris = showLog(listReceived, getClass().getResource("../resources/log/received/" + user).getPath());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            receivedSem.release();
        }

        return ris;
    }

    public boolean showLogLogin(String user, Semaphore loginSem){
        boolean ris = false;

        try{
            loginSem.acquire();
            ris = showLog(listLogin, getClass().getResource("../resources/log/login/" + user).getPath());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            loginSem.release();
        }

        return ris;
    }

    public boolean showLogLogout(String user, Semaphore logoutSem){
        boolean ris = false;

        try{
            logoutSem.acquire();
            ris = showLog(listLogout, getClass().getResource("../resources/log/logout/" + user).getPath());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logoutSem.release();
        }

        return ris;
    }

    public boolean showLogRemove(String user, Semaphore removeSem){
        boolean ris = false;

        try{
            removeSem.acquire();
            ris = showLog(listRemove,getClass().getResource("../resources/log/remove/" + user).getPath());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            removeSem.release();
        }

        return ris;
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

        try {
            String line = reader.readLine();

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
