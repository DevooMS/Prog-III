package org.prog3.lab.project.threadModel;

import org.prog3.lab.project.model.User;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class SendTask implements Runnable{

    private final Semaphore connectionSem;
    private final Semaphore sendSem;
    private final Semaphore errorSendSem;
    private final Semaphore receivedSem;
    private final ExecutorService logThreads;
    private final String path;
    private final User user;
    private final String receivers;
    private final String object;
    private final String text;
    private final ObjectOutputStream outStream;
    private final ArrayList<String> listReceivers = new ArrayList<>();
    private final DateTimeFormatter logDateFormatter;
    private final DateTimeFormatter fileDateFormatter;
    private String wrongAddress;
    private String logDate;
    private String fileDate;
    private File file_send;

    public SendTask(Semaphore connectionSem, Semaphore sendSem, Semaphore errorSendSem, Semaphore receivedSem, ExecutorService logThreads, String path, User user, String receivers, String object, String text, ObjectOutputStream outStream) {
        this.connectionSem = connectionSem;
        this.sendSem = sendSem;
        this.errorSendSem = errorSendSem;
       this.receivedSem = receivedSem;
        this.logThreads = logThreads;
        this.path = path;
        this.user = user;
        this.receivers = receivers;
        this.object = object;
        this.text = text;
        this.outStream = outStream;
        wrongAddress = "";
        logDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        fileDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmmss");
    }

    public void run(){

        logDate = logDateFormatter.format(LocalDateTime.now());
        logThreads.execute(new LogTask(connectionSem, Objects.requireNonNull(getClass().getResource("../resources/log/connection/" + user.getUserEmail())).getPath(), "send connection", logDate));

        fileDate = fileDateFormatter.format(LocalDateTime.now());
        file_send = new File(path + fileDate + ".txt");             //creo l'oggetto

        String response = "send_error";

        try {

            if(file_send.exists()){                                 //se il file esiste gia allora faccio
                response = "send_error";
            }else {

                checkAddress(receivers);                            //verifica se indirizzo e corretto chiama il metodo

                if(file_send.createNewFile()) {                     //crea il file

                    PrintWriter out = new PrintWriter(file_send);       //usato per scrivere su file Se nella cartella corrente esiste già un file dinome nomeFile, viene aperto tale file. Altrimenti, viene creato un nuovo file con il nome specificato

                    out.println("--NO_READ--");
                    try {
                        user.getReadWrite().acquire();

                        logDate = logDateFormatter.format(LocalDateTime.now());

                        writeFile(user.getUserEmail(), false, out);             // chiamo il metodo writeFile e faccio la scrittura dei dati
                        writeFile(receivers, false, out);
                        writeFile(object, false, out);
                        writeFile(logDate, false, out);
                        writeFile(text, true, out);

                        out.close();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        user.getReadWrite().release();              //accedo alla sezione critica
                    }

                    sendToReceivers();                              //chiamo il metodo per receiver    

                    logThreads.execute(new LogTask(sendSem, Objects.requireNonNull(getClass().getResource("../resources/log/send/" + user.getUserEmail())).getPath(), "send email", logDate));

                    response = "send_correct";

                } else {

                    logDate = logDateFormatter.format(LocalDateTime.now());

                    logThreads.execute(new LogTask(errorSendSem, Objects.requireNonNull(getClass().getResource("../resources/log/errorSend/" + user.getUserEmail())).getPath(), "send to wrong address", logDate));

                    response = "send_error";

                }
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();

            response = "send_error";

        } finally {
            try {
                outStream.writeObject(response);

                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkAddress(String receivers){

        int start=0;
        int end = 0;

        while(end>=0){

            end = receivers.indexOf(",", start);

            String receiver;

            if(end>=0)
                receiver = receivers.substring(start, end);
            else
                receiver = receivers.substring(start);

            String user_folder_path = Objects.requireNonNull(getClass().getResource("../resources/userInbox/")).getPath();

            File folder = new File( user_folder_path + "/" + receiver);

            if(receiver.equals(user.getUserEmail()) || !folder.isDirectory()){                               //se non e presente il directory oppure send=reciver
                wrongAddress += receiver + " ";                                                             //aggiungo gli indirizzi non trovati
            } else {
                if(!listReceivers.contains(receiver))
                    listReceivers.add(receiver);                                                            //se non ce nella lista receivers allora aggiungilo nella lista
            }

            start = end+1;
        }
    }

    private void writeFile(String text, boolean isTextEmail, PrintWriter out){

        if(!isTextEmail){
            out.println("--START--");
            out.println(text);
            out.println("--END--");
        } else {
            out.println("--START_TEXT--");
            out.println(text);
            out.println("--END_TEXT--");
        }

        out.flush();
    }

    private void sendToReceivers() throws IOException, URISyntaxException {

        for (int i = 0; i < listReceivers.size(); i++) {                                                //salvo dentro la lista dei reciver che devono ricevere email


            String file_receiver_path = Objects.requireNonNull(getClass().getResource("../resources/userInbox/" + listReceivers.get(i) + "/receivedEmails/")).getPath();
            File file_receiver = new File(file_receiver_path + file_send.getName() + "_" + user.getUserEmail() + ".txt");

            if(file_receiver.createNewFile()) {                                                         //creo il file

                //file_receiver.createNewFile();
                FileChannel from = new FileInputStream(file_send).getChannel();
                FileChannel receiver = new FileOutputStream(file_receiver).getChannel();

                receiver.transferFrom(from, 0, from.size());

                from.close();
                receiver.close();

                logDate = logDateFormatter.format(LocalDateTime.now());

                logThreads.execute(new LogTask(receivedSem, Objects.requireNonNull(getClass().getResource("../resources/log/received/" + listReceivers.get(i))).getPath(), "received email", logDate));

            }else{  //se non riesce creare il file faccio il log del errore
                logDate = logDateFormatter.format(LocalDateTime.now());

                logThreads.execute(new LogTask(errorSendSem, Objects.requireNonNull(getClass().getResource("../resources/log/errorSend/" + user.getUserEmail())).getPath(), "error send to receiver", logDate));

            }
        }

        if(wrongAddress.length() > 0){                          // vado stampare errore nel caso email non e stato trovato

            logDate = logDateFormatter.format(LocalDateTime.now());

            logThreads.execute(new LogTask(errorSendSem, Objects.requireNonNull(getClass().getResource("../resources/log/errorSend/" + user.getUserEmail())).getPath(), "send to wrong address", logDate));

            wrongAddress = wrongAddress.replace(" ", "\n");

            fileDate = fileDateFormatter.format(LocalDateTime.now());

            String sender_path = Objects.requireNonNull(getClass().getResource("../resources/userInbox/" + user.getUserEmail() + "/receivedEmails/")).getPath();

            File file_error;

            file_error = new File(sender_path + fileDate + "_error.txt");

            PrintWriter out = new PrintWriter(file_error);

            out.println("--NO_READ--");

            logDate = logDateFormatter.format(LocalDateTime.now());

            writeFile("no_reply@e.it", false, out);
            writeFile(user.getUserEmail(), false, out);
            writeFile("Indirizzi email errati", false, out);
            writeFile(logDate, false, out);
            writeFile("Nella mail con oggetto \""+object+"\", iseguenti indirizzi email sono errati: \n\n"+wrongAddress+"\n\nN:B:: si prega di non rispondere a questa email.", true, out);

            out.close();
        }

    }

}
