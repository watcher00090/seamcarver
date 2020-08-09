package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.assertj.core.internal.bytebuddy.implementation.bind.annotation.IgnoreForBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

enum CursorMode {
    NW_RESIZE, N_RESIZE, W_RESIZE, NORMAL;
}

public class LayoutController {

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

    private FileChooser fileChooser;

    private Stage stage;

    private DijkstraSeamFinderOptimized sf;

    private Canvas canvas;

    @FXML
    void handleImageDrag(ActionEvent event) {

    }

    @FXML
    void saveSeamCarvedImage(ActionEvent event) {

    }


    @FXML
    void uploadImage(ActionEvent event) {
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                openFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openFile(File file) throws FileNotFoundException {
        System.out.println("Uploading a new image...");
        FileInputStream stream = new FileInputStream(file);
        Image image = new Image(stream);
        ImageView imageView = new ImageView();
        canvas = new Canvas(400,400);

        this.setSeamFinder(new DijkstraSeamFinderOptimized(image));
        drawImage(image);

        //WritableImage writableImage = new WritableImage(new SeamGraphPixelReader(sf), image.widthProperty().intValue(), image.heightProperty().intValue());

        //imageView.setImage(image);

        // changed from
        //imageView.setImage(image);
        // end of changed from

        Group root = new Group(canvas);

        //open a scene in a new window and display the image
        //Group root = new Group(imageView);
        Scene popupScene = new Scene(root);

        Stage popupStage = new Stage();

        /*
        popupStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
                //  ((AnchorPane) root).setPrefWidth(newSceneWidth.doubleValue());
                System.out.println("Image Width: " + image.getWidth());
            }
        });
        popupStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
                // ((AnchorPane) root).setPrefHeight(newSceneHeight.doubleValue());
                System.out.println("Image Height: " + image.getHeight());

            }
        });
        */

        popupStage.setTitle("Displaying Image");
        popupStage.setScene(popupScene);
        popupStage.setResizable(true);
        popupStage.show();

        imageView.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                if (Math.abs(e.getX() - image.getWidth()) < 10 && Math.abs(e.getY() - image.getHeight()) < 10) {
                    imageView.setCursor(Cursor.NW_RESIZE);
                } else if (Math.abs(e.getX() - image.getWidth()) < 10 ) {
                    imageView.setCursor(Cursor.W_RESIZE);
                } else if (Math.abs(e.getY() - image.getHeight()) < 10) {
                    imageView.setCursor(Cursor.N_RESIZE);
                } else {
                    imageView.setCursor(Cursor.DEFAULT);
                }
            }
        });

        imageView.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                System.out.println("X coord of mouse event: " + e.getX());
                System.out.println("Y coord of mouse event: " + e.getY());
            }
        });

    }

    private void drawImage(Image image) {
        byte[] imageData = new byte[image.widthProperty().intValue()
                * image.heightProperty().intValue()
                * 3];

        int i=0;
        for (int y=0;y<image.heightProperty().intValue();++y) {
            for (int x=0;x<image.widthProperty().intValue();++x) {
                int rgb = image.getPixelReader().getArgb(x,y);

                int b = (rgb >> 0) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int r = (rgb >> 16) & 0xFF;


                imageData[i] = (byte) r;
                imageData[i+1] = (byte) g;
                imageData[i+2] = (byte) b;
                i+=3;
            }
        }
        canvas.getGraphicsContext2D().getPixelWriter().setPixels(0,0,
                image.widthProperty().intValue(),
                image.heightProperty().intValue(),
                PixelFormat.getByteRgbInstance(),
                imageData,
                0,
                image.widthProperty().intValue()*3);
    }

    @FXML
    void undoLastChange(ActionEvent event) {

    }

    @FXML
    void openSaveDialog(ActionEvent event) {

    }

    void setStageAndSetupListeners(Stage stage, FileChooser fileChooser) {
        this.fileChooser = fileChooser;
        this.stage = stage;
    }

    void setSeamFinder(DijkstraSeamFinderOptimized sf) {
        this.sf = sf;
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