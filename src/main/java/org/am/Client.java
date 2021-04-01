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
import java.util.Scanner;

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


    /*
     * dirRequest()
     *
     * This method structures the DIR request and sends it to the server.
     * Then, it reads the response and returns it.
     *
     */
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
            socket.close();
        } catch (IOException e){
            System.err.println("Error reading response from the Server Socket");
        }
        return response;
    }

    /*
     * downloadRequest()
     *
     * This method structures the DOWNLOAD request and sends it to the server.
     * Then, it reads the response and returns it.
     *
     */
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

    /*
     * uploadRequest()
     *
     * This method structures the UPLOAD request and sends it to the server.
     * Then, it reads the response and returns it.
     *
     */
    public String uploadRequest(File file) {
        String request = "UPLOAD " + file.getName() + "\r\n" +
                "User: " + this.username + "\r\n" +
                "Host: " + this.SERVER_ADDRESS + " " + this.SERVER_PORT + "\r\n" +
                "Content:" + "\r\n";

        // iterate through file and add each line to the request content
        try {
            request += getFileContent(file);
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

    /*
     * deleteRequest()
     *
     * This method structures the DELETE request and sends it to the server.
     * Then, it reads the response and returns it.
     *
     */
    public String deleteRequest(String filename, Boolean local) {
        String request = "DELETE " + filename + "\r\n" +
                "User: " + this.username + "\r\n" +
                "Host: " + this.SERVER_ADDRESS + " " + this.SERVER_PORT + "\r\n" +
                "Content:" + "\r\n" +
                "-/-";

        networkOut.println(request);
        networkOut.flush();

        String response = null;
        // if the file to be deleted is remote
        if (!local) {
            networkOut.println(request); // we issue the request
            networkOut.flush();
            // read response
            try {
                response = readResponse(); // and read the response
                socket.close();
            } catch (IOException e) {
                System.err.println("Error reading response from the Server Socket");
            }
        } else { // if the file is local
            // we handle the deletion ourselves
            // no need to get the server involved
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

    /*
     * readResponse()
     *
     * This method reads the response from the server and returns it as a string
     *
     */
    public String readResponse() throws IOException {
        String line;
        String response = "";
        while (null != (line = networkIn.readLine())){
            response += line + "\r\n";
        }


        System.out.println(response);
        return response;
    }

    /*
     * getFileContent(File file)
     *
     * @param file - File we want the content from
     *
     * This method returns the content of the file in a string
     *
     */
    private String getFileContent(File file) throws IOException{
        String content = "";

        String currentLine;
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext() && null != (currentLine = scanner.nextLine())) {
            content += currentLine +"\r\n";

        }


        scanner.close();

        return content;
    }

    /*
     * getDir()
     *
     * This method returns the Client Directory
     *
     */
    public File getDir(){
        return localDir;
    }



}
