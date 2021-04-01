package org.am;

/*
 *  Client class
 *
 *  @param localDir
 */

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client{


    private String username;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter networkOut = null;
    private BufferedReader networkIn = null;
    private File localDir;


    public String SERVER_ADDRESS = "localhost";
    public int    SERVER_PORT = 16789;

    public Client(File localDir) {
        this.username = "avril"; // change later
        this.localDir = localDir;
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: "+SERVER_ADDRESS);
        } catch (IOException e) {
            System.err.println("IOException while connecting to server: "+SERVER_ADDRESS);
        }
        if (null == socket) {
            System.err.println("socket is null");
        }
        try {
            networkOut = new PrintWriter(socket.getOutputStream());
            networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOException while opening a read/write connection");
        }

        try {
            String newResponse = networkIn.readLine();
            System.out.println(newResponse);
        } catch (IOException e){
            e.printStackTrace();
        }


    }


    // DIR
    public String dirRequest() {
        String request = "DIR -/-\r\n" +
                "User: " + this.username + "\r\n" +
                "Host: " + this.SERVER_ADDRESS + " " + this.SERVER_PORT + "\r\n" +
                "Content:" + "\r\n" +
                "-/-";
        networkOut.println(request);
        networkOut.flush();

        String response = null;
        // read response
        try {
            response = readResponse();
//            System.out.println(response);
            socket.close();
        } catch (IOException e){
            System.err.println("Error reading response from the Server Socket");
        }
        return response;
    }

    // DOWNLOAD
    public String downloadRequest(String filename) {
        String request = "DOWNLOAD " + filename + "\r\n" +
                "User: " + this.username + "\r\n" +
                "Host: " + this.SERVER_ADDRESS + " " + this.SERVER_PORT + "\r\n" +
                "Content:" + "\r\n" +
                "-/-";
        networkOut.println(request);
        networkOut.flush();

        String response = null;
        // read response
        try {
            response = readResponse();
            System.out.println(response);
            socket.close();
        } catch (IOException e){
            System.err.println("Error reading response from the Server Socket");
        }
        return response;
    }

    // UPLOAD
    public String uploadRequest(File file) {
        String request = "UPLOAD " + file.getName() + "\r\n" +
                "User: " + this.username + "\r\n" +
                "Host: " + this.SERVER_ADDRESS + " " + this.SERVER_PORT + "\r\n" +
                "Content:" + "\r\n";

        // iterate through file and add each line to the request content
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null) {
                request += line + "\r\n";
            }
        } catch (Exception e) {
            System.err.println("404 File for UPLOAD nor Found.");
        }

        request += "-/-";

        networkOut.println(request);
        networkOut.flush();

        String response = null;
        // read response
        try {
            response = readResponse();
            socket.close();
        } catch (IOException e){
            System.err.println("Error reading response from the Server Socket");
        }
        return response;
    }

    // DELETE
    public String deleteRequest(String filename, Boolean local) {
        String request = "DELETE " + filename + "\r\n" +
                "User: " + this.username + "\r\n" +
                "Host: " + this.SERVER_ADDRESS + " " + this.SERVER_PORT + "\r\n" +
                "Content:" + "\r\n" +
                "-/-";

        networkOut.println(request);
        networkOut.flush();

        String response = null;
        if (!local) {
            networkOut.println(request);
            networkOut.flush();
            // read response
            try {
                response = readResponse();
                socket.close();
            } catch (IOException e) {
                System.err.println("Error reading response from the Server Socket");
            }
        } else {
            File deleteFile = null;
            File[] files = localDir.listFiles();
            for (File file: files){
                if (file.getName().equals(filename)){
                    deleteFile = file;
                }
            }
            if(null != deleteFile) {
                deleteFile.delete();
                response = "File Locally Deleted Successfully";
            } else {
                System.err.println("404 File for deletion not found");
            }
        }
        return response;
    }

    public String readResponse() throws IOException {
//        String line = networkIn.readLine();
//        String[] request = line.split(" ");
//
//        String response = line + "\r\n";
//        for (int i = 0; i < 4; i++){
//            line = networkIn.readLine();
//            response += line + "\r\n";
//        }
//        String[] contentLines = line.split(" ");
//
//        String content = "";
//        if (request[2].equals("DOWNLOAD")){
//            for (int i = 0; i < Integer.parseInt(contentLines[2]); i++){
//                content += networkIn.readLine() + "\r\n";
//            }
//        } else {
//            content += "-/-";
//        }
        String line;
        String response = "";
        while (null != (line = networkIn.readLine())){
            response += line + "\r\n";
        }


        System.out.println(response);
        return response;
    }


    public File getDir(){
        return localDir;
    }

}
