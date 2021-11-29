package org.prog3.lab.project.threadModel;
//new version 1.0

import java.io.*;

public class UpdateTask implements Runnable{
    private final String directoryPath;
    private final PrintWriter outSimpleStream;
    private final ObjectOutputStream outObjectStream;

    public UpdateTask(String directoryPath, PrintWriter outSimpleStream, ObjectOutputStream outObjectStream){
        this.directoryPath = directoryPath;
        this.outSimpleStream = outSimpleStream;
        this.outObjectStream = outObjectStream;
    }

    public void run(){

        try {
            File dir = new File(directoryPath);
            //System.out.println(directoryPath);
            int countFiles = dir.listFiles().length;

            outObjectStream.writeObject(countFiles);

            for(int i=0; i<countFiles; i++) {

                BufferedReader reader = new BufferedReader(new FileReader(directoryPath + "\\e" + i));

                String line = reader.readLine();

                String lineToSend = "";

                int j=0;

                while(line!=null) {

                    if(line.equals("--START--")){

                        line = reader.readLine();

                        while(!line.equals("--END--")){
                            lineToSend += line;

                            line = reader.readLine();
                        }
                    } else if(line.equals("--START_TEXT--")){

                        line = reader.readLine();

                        while(!line.equals("--END_TEXT--")) {
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
                outObjectStream.writeObject("--END_EMAIL--");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
