/**
 * Sample Skeleton for 'sample.fxml' Controller Class
 */

package sample;

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

public class LayoutControllerOld {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="imagePane"
    private Pane imagePane; // Value injected by FXMLLoader

    @FXML // fx:id="undoLastChangeButton"
    private Button undoLastChangeButton; // Value injected by FXMLLoader

    @FXML // fx:id="saveButton"
    private Button saveButton; // Value injected by FXMLLoader

    @FXML // fx:id="uploadButton"
    private Button uploadButton; // Value injected by FXMLLoader

    @FXML // fx:id="scene"
    private AnchorPane scene; // Value injected by FXMLLoader


    @FXML
    void handleImageDrag(ActionEvent event) {

    }

    @FXML
    void saveSeamCarvedImage(ActionEvent event) {

    }

    @FXML
    void uploadImage(ActionEvent event) {
    }

    @FXML
    void undoLastChange(ActionEvent event) {

    }

    @FXML
    void openSaveDialog(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert imagePane != null : "fx:id=\"imagePane\" was not injected: check your FXML file 'sample.fxml'.";
        assert undoLastChangeButton != null : "fx:id=\"undoLastChangeButton\" was not injected: check your FXML file 'sample.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'sample.fxml'.";
        assert uploadButton != null : "fx:id=\"uploadButton\" was not injected: check your FXML file 'sample.fxml'.";
        assert scene != null : "fx:id=\"scene\" was not injected: check your FXML file 'sample.fxml'.";
    }
}
