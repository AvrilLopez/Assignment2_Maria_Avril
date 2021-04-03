package org.am;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class Main extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        Scene scene = new Scene(root);

        // Ask user to input a username
        TextInputDialog input = new TextInputDialog();
        input.getDialogPane().setContentText("Enter your username: ");
        input.showAndWait();
        TextField textInput = input.getEditor();

        String username = textInput.getText();

        if (null == username){
            username = "Guest";
        }

        // Ask user to choose local directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("./src/main/resources"));
        directoryChooser.setTitle("Select Local folder");
        File localDir = directoryChooser.showDialog(primaryStage);

        controller.initialize(localDir, username);

        primaryStage.setTitle("File Server");
        primaryStage.setScene(scene);
        primaryStage.show();

    }



}

