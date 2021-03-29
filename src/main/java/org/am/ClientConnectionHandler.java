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
            line = requestInput.readLine();
            handleRequest(line);
        } catch(IOException e) {
            System.err.println("Error reading from client output stream (Server input)");
        } finally {
            try {
                requestInput.close();
                clientSocket.close();
                System.out.println("Client Disconnected\r\n");
            } catch(IOException e) {
                System.err.println("Error closing the client socket or input stream");
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
                System.err.println("Error sending Unrecognized Request Response");
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
            System.err.println("Error reading DIR request from the Client.");
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
            System.err.println("400: Error while sending DIR response.");
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

        } catch(IOException e) {
            System.err.println("Error reading UPLOAD request from the Client.");
        }

        System.out.println(request);
        sendUploadResponse();
    }

    private void sendUploadResponse(){

    }

    private void handleDownload(String line) {
        String request = line;
        String[] temp = line.split(" ");
        String filename = temp[1];
        try{
            while (requestInput.ready() && null != (line = requestInput.readLine())){
                request += "\r\n" + line;
            }

        } catch(IOException e) {
            System.err.println("Error reading DOWNLOAD request from the Client.");
        }

        System.out.println(request);
        sendDownloadResponse(filename);
    }

    private void sendDownloadResponse(String filename){
        String responseCode = "200 Ok: DOWNLOAD";

        File downloadFile = null;
        File[] files = serverDirectory.listFiles();
        for (File file : files) {
            if (file.getName().equals(filename)) {
                downloadFile = file;
            }
        }


        String content = "";

        try {
            String currentLine = "";
            BufferedReader br = new BufferedReader(new FileReader(downloadFile));
            Integer lines = 0;
            while (null != (currentLine = br.readLine())) {
                content += currentLine +"\r\n";
                lines++;

            }

            try {
                sendResponse(responseCode, filename, content, lines.toString());
            } catch (IOException e) {
                System.err.println("400: Error while sending DOWNLOAD response.");
            }
        } catch (IOException e) {
            System.err.println("404: Error File for download not found in the server.");
        }


    }

    private void handleDelete(String line) {
        String request = line;
        String[] temp = line.split(" ");
        String filename = temp[1];
        try{
            while (requestInput.ready() && null != (line = requestInput.readLine())){
                request += line + "\r\n";
            }

        } catch(IOException e) {
            System.err.println("Error reading DELETE request from the Client.");
        }

        System.out.println(request);
        sendDeleteResponse(filename);
    }

    private void sendDeleteResponse(String filename){
        String responseCode = "200 Ok: DELETE";

        File deleteFile = null;
        File[] files = serverDirectory.listFiles();
        for (File file : files) {
            if (file.getName().equals(filename)) {
                deleteFile = file;
            }
        }

        if (null == deleteFile){
            System.err.println("Error, file for removal could not be found in the server.");
        } else {
            deleteFile.delete();
            try {
                sendResponse(responseCode, filename, "-/-", "0");
            } catch (IOException e) {
                System.err.println("400: Error while sending DELETE response.");
            }
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