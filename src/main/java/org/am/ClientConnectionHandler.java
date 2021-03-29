package org.am;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientConnectionHandler extends Thread{
    protected Socket clientSocket;
    protected PrintWriter responseOutput = null;
    protected BufferedReader requestInput = null;
    private File serverDirectory = new File("C:\\Users\\Avril\\Desktop\\Server");

    public ClientConnectionHandler(Socket socket) {
        super();
        this.clientSocket = socket;
        try {
            responseOutput = new PrintWriter(socket.getOutputStream());
            requestInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOException while opening a read/write connection");
        }
    }

    public void run() {
        // initialize interaction
        responseOutput.println("-----------------------------------");
        responseOutput.flush();

        String line;
        try{
            if (requestInput.ready()){
                line = requestInput.readLine();
                handleRequest(line);
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                requestInput.close();
                clientSocket.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

    }


    protected boolean handleRequest(String line) {
        String[] requestLine = line.split(" ");
        String request = requestLine[0];
        if (request.equalsIgnoreCase("DIR")) {
            handleDir(line);
            return false;
        } else if (request.equalsIgnoreCase("UPLOAD")) {
            handleUpload(line);
            return false;
        } else if (request.equalsIgnoreCase("DOWNLOAD")) {
            handleDownload(line);
            return false;
        } else if (request.equalsIgnoreCase("DELETE")) {
            handleDelete(line);
            return false;
        } else {
            String responseCode = "400 Unrecognized Request: " + request;
            System.out.println(responseCode);

            try {
                sendResponse(responseCode, "-/-", null, "-/-");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    private void handleDir(String line) {
        String request = line;
        try{
            while(null != (line = requestInput.readLine()) && !line.equals("-/-")) {
                request += "\r\n" + line;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(request);
        sendDirResponse();
    }

    private void sendDirResponse() {
        String responseCode = "200 Ok: DIR";

        File[] files = serverDirectory.listFiles();
        String content = "";
        Integer lines = 0;
        for (File currentFile : files){
            content += currentFile.getName() + "\r\n";
            lines++;
        }

        try {
            sendResponse(responseCode, serverDirectory.getName(), content, lines.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUpload(String line) {
        String request = "UPLOAD";
        try{
            for (int i = 0; i < 5; i++){
                request += "\r\n" + requestInput.readLine();
            }
            String parameters = "";
            while(!requestInput.readLine().equalsIgnoreCase("-/-")) {
                parameters += "\r\n" + requestInput.readLine();
            }

            request += parameters;
            System.out.println(request);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDownload(String line) {
        String request = "DOWNLOAD";
        try{
            for (int i = 0; i < 5; i++){
                request += "\r\n" + requestInput.readLine();
            }

            System.out.println(request);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(String line) {
        String request = "DELETE";
        try{
            for (int i = 0; i < 5; i++){
                request += "\r\n" + requestInput.readLine();
            }

            System.out.println(request);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(String responseCode, String filename, String content, String lines) throws IOException {
        responseOutput.print(responseCode + "\r\n");
        responseOutput.print("Date: " + (new Date()) + "\r\n");
        responseOutput.print("Server: FileSharingServer v1.0.0\r\n");
        responseOutput.print("File: " + filename + "\r\n");
        responseOutput.print("Response-Length: " + lines + "\r\n\r\n");
        responseOutput.print(content);
        responseOutput.print("\r\nConnection: Close\r\n\r\n");
        responseOutput.flush();

    }
}