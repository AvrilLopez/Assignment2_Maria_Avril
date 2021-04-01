package org.am;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 *  Server class
 *
 *  for server fet up and, listening for connections and
 *  thread setup.
 *
 */
public class Server {

    protected Socket clientSocket           = null;
    protected ServerSocket serverSocket     = null;
    protected ClientConnectionHandler[] threads    = null;
    protected int numClients                = 0;
    protected File serverDirectory;

    public static int SERVER_PORT = 16789;
    public static int MAX_CLIENTS = 100;

    public Server(File serverDirectory) {
        this.serverDirectory = serverDirectory;
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("---------------------------");
            System.out.println("File Server is running");
            System.out.println("---------------------------");
            System.out.println("Listening to port: "+ SERVER_PORT);
            threads = new ClientConnectionHandler[MAX_CLIENTS];
            while(true) {
                clientSocket = serverSocket.accept();
                System.out.println("Client connected.");
                threads[numClients] = new ClientConnectionHandler(clientSocket);
                threads[numClients].start();
                numClients++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException while creating server connection");
        }
    }

    public static void main(String[] args) {
        Server server = new Server(new File("./src/main/resources/ServerDirectory"));
    }
}
