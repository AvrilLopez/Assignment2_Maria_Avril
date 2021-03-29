package org.am;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;

public class Controller {
    @FXML
    private TreeView treeViewLeft;
    @FXML
    private TreeView treeViewRight;

    public File clientDir;

    private Stage primaryStage;

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
        if (null != dirResponse) {
            TreeItem<String> root = new TreeItem<>();
            root.setExpanded(true);
            System.out.println(dirResponse);
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
    }

    public void createItem(String value, TreeItem root) {
        TreeItem<String> newItem = new TreeItem<>();
        newItem.setValue(value);

        root.getChildren().add(newItem);
    }

    public void rightClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getTarget().getClass().getName().equals("javafx.scene.Group")){

        } else if (mouseEvent.getTarget().getClass().getName().equals("com.sun.javafx.scene.control.LabeledText")) {
            String[] filename = mouseEvent.getTarget().toString().split("\"");
            this.currentFile = filename[1];
            this.local = true;
            dirRequest();
        } else {
            String[] filename = mouseEvent.getTarget().toString().split("'");
            this.currentFile = filename[1];
            this.local = true;
            dirRequest();
        }


    }

    public void leftClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getTarget().getClass().getName().equals("javafx.scene.Group")){

        } else if (mouseEvent.getTarget().getClass().getName().equals("com.sun.javafx.scene.control.LabeledText")) {
            String[] filename = mouseEvent.getTarget().toString().split("\"");
            this.currentFile = filename[1];
            this.local = false;
            dirRequest();
        } else {
            String[] filename = mouseEvent.getTarget().toString().split("'");
            this.currentFile = filename[1];
            this.local = false;
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
        this.dirResponse = client.dirRequest();
        parseDirResponse();
        populateLeftTreeView();
        populateRightTreeView();
    }
    @FXML
    public void uploadRequest(){
        Client client = new Client(clientDir);
        //this.response = client.uploadRequest(currentFile);
    }
    @FXML
    public void downloadRequest(){
        Client client = new Client(clientDir);
//        this.response = client.downloadRequest(currentFile);
    }
    @FXML
    public void deleteRequest(){
        Client client = new Client(clientDir);
//        this.response = client.deleteRequest(currentFile);
    }
    @FXML
    public void exit() {
        primaryStage.close();
    }



}
