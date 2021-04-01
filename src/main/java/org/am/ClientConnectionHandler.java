package org.am;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class ClientConnectionHandler extends Thread{
    protected Socket clientSocket;
    protected PrintWriter responseOutput = null;
    protected BufferedReader requestInput = null;
    private File serverDirectory = new File("./src/main/resources/ServerDirectory");

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


    /*
     * handleRequest(String line)
     *
     * @param line - first line of Client Request.
     *
     * This method delegates the request to the appropriate handler.
     *
     */
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

    /*
     * handleDir(String line)
     *
     * @param line - first line of Client Request.
     *
     * This method reads in the DIR request, prints it to the Server terminal and
     * calls a function for sending the response
     */
    private void handleDir(String line) {
        String request = line;
        try{
            request += readRequest();

        } catch (IOException e) {
            System.err.println("Error reading DIR request from the Client.");
        }

        System.out.println(request);
        sendDirResponse();
    }

    /*
     * sendDirResponse()
     *
     * This method lists the files in the server directory and sends them as parameters
     * for the response
     */
    private void sendDirResponse() {
        String responseCode = "200 Ok: DIR";

        // get list of filenames into a string to send back
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

    /*
     * handleUpload(String line)
     *
     * @param line - first line of Client Request.
     *
     * This method reads in the UPLOAD request, prints it to the Server terminal and
     * calls a function for sending the response
     */
    private void handleUpload(String line) {
        String request = line;
        String[] temp = line.split("UPLOAD ");

        // The following line of code to take the extra spacing away was taken form stack overflow
        String filename = temp[1].trim().replaceAll(" +", " ");

        try{
            request += readRequest();
        } catch(IOException e) {
            System.err.println("Error reading UPLOAD request from the Client.");
        }

        System.out.println(request);
        sendUploadResponse(filename, request);
    }

    /*
     * sendUploadResponse(String filename, String request)
     *
     * @param filename - Name of the file that is involved
     * @param request - Client Request
     *
     * This method creates a new file in the server directory with the information
     * provided in the Client Request and sends the parameters necessary for the response.
     */
    private void sendUploadResponse(String filename, String request){

        try {
            while (isInDir(filename,serverDirectory)){
                String[] temp1 = filename.split("\\.");
                filename = temp1[0] + "(" + 1 + ")." + temp1[1];
            }

            String[] content = getContentFromRequest(request);
            createFile(filename,content,serverDirectory);


            String responseCode = "200 Ok: UPLOAD";

            try {
                sendResponse(responseCode, filename, "-/-", "0");
            } catch (IOException e) {
                System.err.println("400: Error while sending UPLOAD response.");
            }

        } catch (IOException e){
            System.err.println("Error, file for printing into not found in server");
        }
    }

    /*
     * handleDownload(String line)
     *
     * @param line - first line of Client Request.
     *
     * This method reads in the DOWNLOAD request, prints it to the Server terminal and
     * calls a function for sending the response
     *
     */
    private void handleDownload(String line) {
        String request = line;
        String[] temp = line.split("DOWNLOAD");

        // The following line of code to take the extra spacing away was taken form stack overflow
        String filename = temp[1].trim().replaceAll(" +", " ");

        try{
            request += readRequest();

        } catch(IOException e) {
            System.err.println("Error reading DOWNLOAD request from the Client.");
        }

        System.out.println(request);
        sendDownloadResponse(filename);
    }

    /*
     * sendDownloadResponse(String filename)
     *
     * @param filename - Name of the file that is involved
     *
     * This method reads the content and lines form the file the client wants to download
     * and sends them as a parameter for the response.
     */
    private void sendDownloadResponse(String filename){
        String responseCode = "200 Ok: DOWNLOAD";

        if (isInDir(filename,serverDirectory)){

            File downloadFile = getFromDir(filename,serverDirectory);

            try {
                String[] contentAndLines = getFileContent(downloadFile).split("-/-");
                String content = contentAndLines[0];
                String lines = contentAndLines[1];

                try {
                    sendResponse(responseCode, filename, content, lines);
                } catch (IOException e) {
                    System.err.println("400: Error while sending DOWNLOAD response.");
                }

            } catch (IOException e) {
                System.err.println("404: Error File for download not found in the server.");
            }

        } else {
            System.err.println("404: File for download could not be found.");
        }

    }

    /*
     * handleDelete(String line)
     *
     * @param line - first line of Client Request.
     *
     * This method reads in the DELETE request, prints it to the Server terminal and
     * calls a function for sending the response
     *
     */
    private void handleDelete(String line) {
        String request = line;
        String[] temp = line.split("DELETE");

        // The following line of code to take the extra spacing away was taken form stack overflow
        String filename = temp[1].trim().replaceAll(" +", " ");

        try{
            request += readRequest();

        } catch(IOException e) {
            System.err.println("Error reading DELETE request from the Client.");
        }

        System.out.println(request);
        sendDeleteResponse(filename);
    }

    /*
     * sendDeleteResponse(String filename)
     *
     * @param filename - Name of the file that is involved
     *
     */
    private void sendDeleteResponse(String filename){
        String responseCode = "200 Ok: DELETE";

        if (isInDir(filename, serverDirectory)){
            File deleteFile = getFromDir(filename, serverDirectory);
            deleteFile.delete();
            try {
                sendResponse(responseCode, filename, "-/-", "0");
            } catch (IOException e) {
                System.err.println("400: Error while sending DELETE response.");
            }
        } else {
            System.err.println("Error, file for removal could not be found in the server.");
        }
    }


    /*
     * sendResponse(String responseCode, String filename, String content, String lines)
     *
     * @param responseCode - Code for indicating weather or not the request was successfully processed
     * @param filename - Name of the file that is involved
     * @param content - Content of the relevant file
     * @param lines - Number of lines in the content
     *
     * This method sets up the response and sends is to the client.
     *
     */
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


    /*
     * isInDir(String filename, File directory)
     *
     * @param filename - Name of the file we are looking for
     * @param directory - Directory we are looking in
     *
     * This method returns true is the file can be found in the specified directory
     *
     */
    private boolean isInDir(String filename, File directory){
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().equals(filename)) {
                return true;
            }
        }
        return false;
    }


    /*
     * getFromDir(String filename, File directory)
     *
     * @param filename - Name of the file we want
     * @param directory - Directory we want it from
     *
     * This method returns the file from the specified directory
     *
     */
    private File getFromDir(String filename, File directory){
        File searchedFile = null;
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().equals(filename)) {
                searchedFile = file;
            }
        }
        return searchedFile;
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
        Integer lines = 0;

        String currentLine;
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext() && null != (currentLine = scanner.nextLine())) {
            content += currentLine +"\r\n";
            lines++;

        }


        scanner.close();


        return content + "-/-" + lines.toString();
    }

    /*
     * readRequest()
     *
     * This method reads everything that's left from the client input and
     * returns it as a string
     *
     */
    private String readRequest() throws IOException{
        String request = "";
        String line;
        while(requestInput.ready() && null != (line = requestInput.readLine())) {
            request += "\r\n" + line;
        }
        return request;
    }

    /*
     * createFile(String filename, String content, File directory)
     *
     * @param filename - Name of the file we want to create
     * @param content - Content of the file we want to create
     * @param directory - Place of the file we want to create
     *
     * This method creates a file in the directory specified with the content specified.
     *
     */
    private void createFile(String filename, String[] content, File directory) throws IOException{
        String path = directory.getPath() + "\\" + filename;
        File newFile = new File(path);
        newFile.createNewFile();
        PrintWriter fileOutput = new PrintWriter(path);
        for (String line: content){
            fileOutput.println(line);
        }
        fileOutput.close();
    }

    /*
     * getContentFromRequest(String request)
     *
     * @param request - Client Request
     *
     * This method returns a list of the lines of content in the file sent in the
     * client request specified.
     *
     */
    private String[] getContentFromRequest(String request){
        String[] requestAsList = request.split("\r\n");
        String[] content = new String[requestAsList.length - 5];
        int i = 4;
        while(!requestAsList[i].equals("-/-")){
            content[i-4] = requestAsList[i];
            i++;
        }
        return content;
    }

}