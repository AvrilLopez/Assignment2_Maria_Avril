package org.am;

/*
 * Client is main class for the client.
 * The ServerRunner can take in parameters from the user on run
 * The typical expected parameters are <hostname> <folderPath>
 * <p>
 * Alternatively you can also provide only one of these parameters if you wish
 * the runner has code to handle only one input.
 * If no parameters are provided the Client will run with default values
 *
 * @param hostname @default localhost
 * @param folderPath @default ./server-files/ where '.' is a relative path
 */
import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class Client {
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter networkOut = null;
    private BufferedReader networkIn = null;
    public  static int  SERVER_PORT = 16789;

    //we read this from the user
    public static String SERVER_ADDRESS = "localhost";
    public static String FILE_PATH = null;



    public Client(String serverAddress) throws IOException, ClassNotFoundException {
        try {
            socket = new Socket(serverAddress, SERVER_PORT);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: "+serverAddress);
        } catch (IOException e) {
            System.err.println("IOException while connecting to server: "+serverAddress);
        }
        if (socket == null) {
            System.err.println("socket is null");
        }
        try {
            networkOut = new PrintWriter(socket.getOutputStream(), true);
            networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOException while opening a read/write connection");
        }

        /*in = new BufferedReader(new InputStreamReader(System.in));

        // force the user to type in a username and password
        boolean ok = login();

        if (!ok) {
            System.exit(0);
        }

        ok = true;
        while(ok) {
            ok = processUserInput();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }

    private  void listFiles(String folderPath) throws IOException, ClassNotFoundException, URISyntaxException {
        //ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        //File folder=new File(FILE_PATH);
        //File[] filesList = (File[]) in.readObject(); //Deserialise
        //File[] filesList=folder.listFiles();


        File[] filesList = (new File(getClass().getResource("/server-files").toURI())).listFiles();
        for(int i = 0; i < filesList.length; i++) {
            System.out.println(filesList[i].getName());

        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException {
        String current = System.getProperty("user.dir");
        System.out.println("Current working directory in Java : " + current);
        try{
            SERVER_ADDRESS=args[0];
            FILE_PATH=args[1];
        } catch (Exception e) {

            //if no arguments are provided
            if(args.length == 0){
                System.out.println("No arguments provided. Defaulting to localhost and ./server-files dir");

                FILE_PATH = Client.class.getClassLoader().getResource("./server-files").toString();


            }
            else if(args.length ==1){
                System.out.println("1 argument provided. Defaulting to ./server-files dir");
                SERVER_ADDRESS=args[0];
                FILE_PATH = Client.class.getClassLoader().getResource("./server-files").toString();
            }
            else{
                System.err.println("Received 2+ parameters but unable to use them. \n Please submit <hostname> <path>.");
                return;
            }
        }
        Client client = new Client(SERVER_ADDRESS);
        client.listFiles(FILE_PATH);




    }

}
