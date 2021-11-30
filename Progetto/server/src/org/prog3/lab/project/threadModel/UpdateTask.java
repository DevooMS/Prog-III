package org.prog3.lab.project.threadModel;
//new version 1.0

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateTask implements Runnable{
    private final String directoryPath;
    private final boolean startUpdate;
    private final PrintWriter outSimpleStream;
    private final ObjectOutputStream outObjectStream;

    public UpdateTask(String directoryPath, boolean startUpdate, PrintWriter outSimpleStream, ObjectOutputStream outObjectStream){
        this.directoryPath = directoryPath;
        this.startUpdate = startUpdate;
        this.outSimpleStream = outSimpleStream;
        this.outObjectStream = outObjectStream;
    }

    public void run(){

        try {
            File dir = new File (directoryPath);
            int countFiles = dir.listFiles ().length;

            //System.out.println(directoryPath);
            //int countFiles = listFiles.length;
            outObjectStream.writeObject(countFiles);

            for(int i=0; i<countFiles; i++) {

                BufferedReader reader = new BufferedReader(new FileReader(directoryPath + "/e" + i));

                String line = reader.readLine();

                String lineToSend = "";

                int j=0;

                if((line.equals("--READ--") && startUpdate) || line.equals("--NO_READ--")) {

                    line = reader.readLine();

                    while (line != null) {

                        if (line.equals("--START--")) {

                            line = reader.readLine();

                            while (!line.equals("--END--")) {
                                lineToSend += line;

                                line = reader.readLine();
                            }
                        } else if (line.equals("--START_TEXT--")) {

                            line = reader.readLine();

                            while (!line.equals("--END_TEXT--")) {
                                if (line.equals(""))
                                    lineToSend += "\n\n";
                                else
                                    lineToSend += line + "\n";

                                line = reader.readLine();
                            }
                        }

                        outObjectStream.writeObject(lineToSend);

                        lineToSend = "";

                        line = reader.readLine();

                    }

                }

                outObjectStream.writeObject("--END_EMAIL--");

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
