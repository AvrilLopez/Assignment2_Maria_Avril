package org.am;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientConnectionHandler extends Thread{
    protected Socket socket;
    protected BufferedWriter out = null;
    protected BufferedReader in = null;

    public ClientConnectionHandler(Socket socket) {
        super();
        this.socket = socket;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOEXception while opening a read/write connection");
        }
    }

    public void run() {
        // initialize interaction

        boolean endOfSession = false;
        while(!endOfSession) {
            endOfSession = processCommand();
        }
        try {
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean processCommand() {
        String message = null;
        try {
            message = in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading command from socket.");
            return true;
        }
        if (message == null) {
            return true;
        }
        StringTokenizer st = new StringTokenizer(message);
        String command = st.nextToken();
        String args = null;
        if (st.hasMoreTokens()) {
            args = message.substring(command.length()+1, message.length());
        }
        return processCommand(command, args);
    }


    protected boolean processCommand(String command, String arguments) {
        if (command.equalsIgnoreCase("DIR")) {

            return false;
        } else if (command.equalsIgnoreCase("UPLOAD")) {

            return false;
        } else if (command.equalsIgnoreCase("DOWNLOAD")) {

            return false;
        } else {
            System.out.println("400 Unrecognized Command: "+command);
            return false;
        }
    }
}
