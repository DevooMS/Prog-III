package org.prog3.lab.project.threadModel;
//new version 1.0

import java.io.*;

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
            File[] listOfFiles = dir.listFiles();
            int countFiles = listOfFiles.length;

            outObjectStream.writeObject(countFiles);

            for(int i=0; i<countFiles; i++) {

                BufferedReader reader = new BufferedReader(new FileReader(directoryPath + "/"+ listOfFiles[i].getName()));

                String line = reader.readLine();

                String lineToSend = "";

                boolean rewrite = false;

                StringBuilder fileContent = new StringBuilder();

                if((line.equals("--READ--") && startUpdate) || line.equals("--NO_READ--")) {

                    outObjectStream.writeObject(listOfFiles[i].getName());

                    if(line.equals("--NO_READ--")){
                        rewrite = true;
                        fileContent.append("--READ--"+System.getProperty("line.separator"));
                    }

                    line = reader.readLine();

                    while (line != null) {

                        if (line.equals("--START--")) {

                            line = reader.readLine();

                            while (!line.equals("--END--")) {
                                lineToSend += line;

                                line = reader.readLine();
                            }

                            if(rewrite) {
                                fileContent.append("--START--" + System.getProperty("line.separator"));
                                fileContent.append(lineToSend + System.getProperty("line.separator"));
                                fileContent.append("--END--" + System.getProperty("line.separator"));
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

                            if(rewrite) {
                                fileContent.append("--START_TEXT--" + System.getProperty("line.separator"));
                                fileContent.append(lineToSend + System.getProperty("line.separator"));
                                fileContent.append("--END_TEXT--" + System.getProperty("line.separator"));
                            }

                        }

                        outObjectStream.writeObject(lineToSend);

                        lineToSend = "";

                        line = reader.readLine();

                    }

                }

                if(rewrite) {
                    FileWriter fstreamWrite = new FileWriter(directoryPath + "/"+ listOfFiles[i].getName());
                    BufferedWriter out = new BufferedWriter(fstreamWrite);
                    out.write(fileContent.toString());
                    out.flush();
                    fstreamWrite.close();
                    out.close();
                }

                outObjectStream.writeObject("--END_EMAIL--");

            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
