package org.am;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Controller {
    @FXML
    private TreeView treeViewLeft;
    @FXML
    private TreeView treeViewRight;

    public File clientDir;

    private Stage primaryStage;

    private File currentFileLocal;
    private String currentFileRemote;
    private String currentFile;

    private Boolean local;

    private String response;

    private String dirResponse;

    @FXML
    public void initialize(File localDir) {

        this.clientDir = localDir;
        this.currentFile = null;
        this.local = null;

        dirRequest();
    }

    /*
     * populateLeftTreeView()
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
            System.err.println("Error in populationg Left TreeView - Index out of Bounds");
        }catch (NullPointerException e){
            System.err.println("Error in populationg Left TreeView - Null Pointer Exception");
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

        while (isInDir(filename,clientDir)){
            String[] temp1 = filename.split("\\.");
            filename = temp1[0] + "(" + 1 + ")." + temp1[1];
        }

        try {
            String[] content = getContentFromResponse(responseContent);
            createFile(filename, content, clientDir);

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


}