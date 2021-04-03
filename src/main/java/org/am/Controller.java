package org.am;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.util.Scanner;

public class Controller {
    @FXML
    private TreeView treeViewLeft;
    @FXML
    private TreeView treeViewRight;
    @FXML
    private TextArea textArea;

    public File clientDir;

    private Stage primaryStage;

    private File currentFileLocal;
    private String currentFileRemote;
    private String currentFile;

    private Boolean local;

    private String response;

    private String dirResponse;

    private Utils utils;

    @FXML
    public void initialize(File localDir) {

        this.clientDir = localDir;
        this.currentFile = null;
        this.local = null;
//        utils = new utils();

        dirRequest();
    }

    /*
     * populateRightTreeView()
     *
     * This method populates the local directory view
     *
     */
    public void populateRightTreeView(){
        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);
        try {
            root.setValue(clientDir.getName());
            File[] files = clientDir.listFiles();
            for (File currentFile : files){
                createItem(currentFile.getName(), root);
            }

            treeViewRight.setRoot(root);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    /*
     * populateLeftTreeView()
     *
     * This method populates the server directory view with the latest dir
     * response form the server
     *
     */
    public void populateLeftTreeView(){
        try {
            if (null != dirResponse) {
                TreeItem<String> root = new TreeItem<>();
                root.setExpanded(true);
                root.setValue("Server");

                String[] files = dirResponse.split("\r\n");
                String[] length = files[4].split(" ");
                Integer responseLength = Integer.parseInt(length[1]);

                Integer i = 0;
                for (String file : files) {
                    if (i > 5 && i < 6 + responseLength) {
                        createItem(file, root);
                    }
                    i++;
                }
                treeViewLeft.setRoot(root);
            }
        } catch (IndexOutOfBoundsException e){
            System.err.println("Error in populating Left TreeView - Index out of Bounds");
        }catch (NullPointerException e){
            System.err.println("Error in populating Left TreeView - Null Pointer Exception");
        }
    }

    /*
     * createItem(String value, TreeItem root)
     *
     * This method creates a tree item with a specific value under the
     * specified root
     *
     */
    public void createItem(String value, TreeItem root) {
        TreeItem<String> newItem = new TreeItem<>();
        newItem.setValue(value);

        root.getChildren().add(newItem);
    }

    /*
     * rightClicked(MouseEvent mouseEvent)
     *
     * This method saves the last clicked file when the user clicks on a file
     * form the local directory
     *
     */
    public void rightClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getTarget().getClass().getName().equals("com.sun.javafx.scene.control.LabeledText")) {
            String[] filename = mouseEvent.getTarget().toString().split("\"");
            setCurrentFileLocal(filename[1]);
            currentFile = filename[1];
            local = true;
            dirRequest();
        } else if (mouseEvent.getTarget().getClass().getName().equals("javafx.scene.control.skin.TreeViewSkin$1")){
            String[] filename = mouseEvent.getTarget().toString().split("'");
            setCurrentFileLocal(filename[1]);
            currentFile = filename[1];
            local = true;
            dirRequest();
        }


    }

    /*
     * leftClicked(MouseEvent mouseEvent)
     *
     * This method saves the last clicked file when the user clicks on a file
     * form the server directory
     *
     */
    public void leftClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getTarget().getClass().getName().equals("com.sun.javafx.scene.control.LabeledText")) {
            String[] filename = mouseEvent.getTarget().toString().split("\"");
            currentFileRemote = filename[1];
            currentFile = filename[1];
            local = false;
            dirRequest();
        } else if (mouseEvent.getTarget().getClass().getName().equals("javafx.scene.control.skin.TreeViewSkin$1")){
            String[] filename = mouseEvent.getTarget().toString().split("'");
            currentFileRemote = filename[1];
            currentFile = filename[1];
            local = false;
            dirRequest();
        }

    }

    /*
     * deleteRequest()
     *
     * This method creates a new client connection and sends a dir request
     *
     */
    @FXML
    public void dirRequest(){
        Client client = new Client(clientDir);
        String tempDirResponse = client.dirRequest();

        if ( null != tempDirResponse && !tempDirResponse.equals(dirResponse)) {
            dirResponse = tempDirResponse;
            populateLeftTreeView();
        }
        populateRightTreeView();
    }

    /*
     * deleteRequest()
     *
     * This method creates a new client connection and sends an upload request for
     * a specific file
     *
     */
    @FXML
    public void uploadRequest(){
        Client client = new Client(clientDir);
        response = client.uploadRequest(currentFileLocal);
        dirRequest();
    }

    /*
     * deleteRequest()
     *
     * This method creates a new client connection and sends a download request for
     * a specific file
     *
     */
    @FXML
    public void downloadRequest(){
        Client client = new Client(clientDir);
        response = client.downloadRequest(currentFileRemote);

        // Parsing the response
        String[] responseContent = response.split("\r\n");
        String[] file = responseContent[3].split("File: ");
        String filename = file[1];

        while (utils.isInDir(filename,clientDir)){
            String[] temp1 = filename.split("\\.");
            filename = temp1[0] + "(" + 1 + ")." + temp1[1];
        }

        try {
            String[] content = getContentFromResponse(responseContent);
            utils.createFile(filename, content, clientDir);

        } catch (IOException e){
            System.err.println("Error copying the file into the local directory");
        }

        dirRequest();
    }

    /*
     * deleteRequest()
     *
     * This method creates a new client connection and sends a delete request for
     * a specific file
     *
     */
    @FXML
    public void deleteRequest(){
        Client client = new Client(clientDir);
        response = client.deleteRequest(currentFile, local);
        dirRequest();
    }

    /*
     * setCurrentFileLocal(String filename)
     *
     * @param filename - name of the file
     *
     * This method sets the current local selected file to the filename specified
     *
     */
    private void setCurrentFileLocal(String filename){
        try {
            File uploadFile = null;
            File[] files = clientDir.listFiles();
            for (File file : files) {
                if (file.getName().equals(filename)) {
                    uploadFile = file;
                }
            }

            if (null == uploadFile){
                System.err.println("Error, file could not be found");
            } else {
                currentFileLocal = uploadFile;
            }
        } catch (Exception e){
            System.err.println("Error, file could not be found");
        }
    }


    /*
     * getContentFromResponse(String response)
     *
     * @param response - Server response
     *
     * This method returns a list of the lines of content in the file sent in the
     * server response specified.
     *
     */
    private String[] getContentFromResponse(String[] responseContent){
        String[] content = new String[responseContent.length - 6];
        String[] length = responseContent[4].split(" ");
        Integer responseLength = Integer.parseInt(length[1]);

        Integer i = 0;
        for (String line : responseContent) {
            if (i > 5 && i < 6 + responseLength) {
                content[i-5] = responseContent[i];
            }
            i++;
        }

        return content;
    }


    public void previewRequest(ActionEvent actionEvent) throws FileNotFoundException {
        textArea.clear();
        if (!local) {
            //textArea.appendText("remote\n");
            Client client = new Client(clientDir);
            response = client.downloadRequest(currentFileRemote);

            // Parsing the response
            String[] responseContent = response.split("\r\n");
            String[] file = responseContent[3].split("File: ");
            String filename = file[1];

            textArea.appendText("Preview of file: "+"./src/main/resources/ServerDirectory/" + filename+"\n\n");

            String[] content = getContentFromResponse(responseContent);

            for (String line: content){
                if (line!=null) {
                    textArea.appendText(line+"\n");
                }

            }
        }
        else {
            //textArea.appendText("local\n");
            String path = clientDir.getPath() + "\\" + currentFile;
            textArea.appendText("Preview of file: "+"./src/main/resources/client-files/"+currentFile+"\n\n");

            String currentLine;
            Scanner scanner = new Scanner(currentFileLocal);
            while (scanner.hasNext() && null != (currentLine = scanner.nextLine())) {
                textArea.appendText(currentLine+"\n");

            }
            scanner.close();

        }


    }
}