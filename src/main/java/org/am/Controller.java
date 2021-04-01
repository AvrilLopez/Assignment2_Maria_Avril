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

    public void createItem(String value, TreeItem root) {
        TreeItem<String> newItem = new TreeItem<>();
        newItem.setValue(value);

        root.getChildren().add(newItem);
    }

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

    private void parseDirResponse(){
//        String[] responseLines = this.dirResponse.split("\n");
//        this.dirResponse = "";
//        for (int i = 3; i < responseLines.length-1; i++){
//            dirResponse = responseLines[i] + "\r\n";
//        }
    }

    // Working up until here

    @FXML
    public void dirRequest(){
        Client client = new Client(clientDir);
        String tempDirResponse = client.dirRequest();

        if ( null != tempDirResponse && !tempDirResponse.equals(dirResponse)) {
            dirResponse = tempDirResponse;
            parseDirResponse();
            populateLeftTreeView();
        }
        populateRightTreeView();
    }
    @FXML
    public void uploadRequest(){
        Client client = new Client(clientDir);
        response = client.uploadRequest(currentFileLocal);
        dirRequest();
    }
    @FXML
    public void downloadRequest(){
        Client client = new Client(clientDir);
        response = client.downloadRequest(currentFileRemote);

        // Parsing the response
        String[] responseContent = response.split("\r\n");
        String[] length = responseContent[4].split(" ");
        Integer responseLength = Integer.parseInt(length[1]);
        String[] file = responseContent[3].split(" ");
        String filename = file[1];

        try {
            String path = clientDir.getPath() + "\\" + filename;
            File newFile = new File(path);
            newFile.createNewFile();
            PrintWriter fileOutput = new PrintWriter(path);

            Integer i = 0;
            for (String line : responseContent) {
                if (i > 5 && i < 6 + responseLength) {
                    fileOutput.println(responseContent[i]);
                }
                i++;
            }

            fileOutput.close();

        } catch (IOException e){
            System.err.println("Error copying the file into the local directory");
        }

        dirRequest();
    }
    @FXML
    public void deleteRequest(){
        Client client = new Client(clientDir);
        response = client.deleteRequest(currentFile, local);
        dirRequest();
    }

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


}
