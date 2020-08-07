package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;


public class Main extends Application {

    final FileChooser fileChooser = new FileChooser();

        @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newlayout.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root, 495, 31);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Content-Aware Image Resizer");

        LayoutController controller = (LayoutController)loader.getController();
        controller.setStageAndSetupListeners(primaryStage, fileChooser); // or what you want to do

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}



