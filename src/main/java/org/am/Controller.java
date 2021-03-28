package org.am;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;


import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class Controller {
    @FXML
    ListView<String> listViewRight;
    private List<String> serverFileNames=new ArrayList<>();
    private ObservableList<String> observableListServer = FXCollections.observableArrayList();

    @FXML
    ListView<String> listViewLeft;
    private List<String> clientFileNames=new ArrayList<>();
    private ObservableList<String> observableListClient = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws URISyntaxException {

        File[] serverFilesList = (new File(getClass().getResource("/server-files").toURI())).listFiles();
        File[] clientFilesList = (new File(getClass().getResource("/client-files").toURI())).listFiles();
        populateListView(serverFilesList, serverFileNames, observableListServer, listViewRight);
        populateListView(clientFilesList, clientFileNames, observableListClient, listViewLeft);

    }

    public void populateListView(File[] whichFileList, List<String> fileNames, ObservableList<String> obsList, ListView lv){
        for (File file : whichFileList) {
            fileNames.add(file.getName());
        }
        obsList.setAll(fileNames);
        lv.setItems(obsList);
    }
    @FXML
    public void downloadClicked(ActionEvent actionEvent) {
        //selected file in the right will transfer to the local client's shared folder
        String fileName=listViewRight.getSelectionModel().getSelectedItem();
        System.out.print(fileName);

    }
    @FXML
    public void uploadClicked(ActionEvent actionEvent) {
        //transfer from the client's local folder to the server's remote shared folder
        String fileName=listViewLeft.getSelectionModel().getSelectedItem();
    }
}
