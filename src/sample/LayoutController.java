package sample;

import edu.princeton.cs.algs4.Picture;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
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

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ResourceBundle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;

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

    private Canvas imageCanvas;

    private double prevX;

    private double prevY;

    private boolean enableNorthResize = false;
    private boolean enableWestResize = false;

    private double imageWidth = 0;
    private double imageHeight = 0;

    private boolean enableImagePan = false;

    private AtomicInteger seamGraphResizerTaskCount = new AtomicInteger(0);

    private AtomicInteger totalNumberOfResizeTasksRun = new AtomicInteger(0);


    /*
    private ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true); // allows app to exit if tasks are running
        return t ;
    });
     */
    private ExecutorService exec = Executors.newSingleThreadExecutor();

    @FXML
    void handleImageDrag(ActionEvent event) {

    }

    @FXML
    void saveSeamCarvedImage(ActionEvent event) {
        try {
            Picture p = sf.verticalSeamGraph.toPicture();
            p.save("tmp_image.png");
            FileInputStream stream = new FileInputStream("tmp_image.png");
            Image image = new Image(stream);

            ImageView imageView = new ImageView(image);
            Group root = new Group(imageView);
            Scene popupScene = new Scene(root);
            Stage popupStage = new Stage();

            popupStage.setWidth(image.getWidth());
            popupStage.setHeight(image.getHeight());
            popupStage.setTitle("Displaying the image after seam carving.....");
            popupStage.setScene(popupScene);
            popupStage.setResizable(true);
            popupStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        //ImageView imageView = new ImageView(image);
        Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize ();
        double windowWidth = screenSize.getWidth();
        double windowHeight = screenSize.getHeight();

        //WritableImage writableImage = new WritableImage(image.widthProperty().intValue(), image.heightProperty().intValue());
        //ImageView imageView = new ImageView();
        imageCanvas = new Canvas(image.getWidth(),image.getHeight());
        this.sf = new DijkstraSeamFinderOptimized(image);

        imageWidth = image.widthProperty().doubleValue();
        imageHeight = image.heightProperty().doubleValue();

        //TODO: change this to renderSeamGraph(arg1, arg2, arg3), like in the event handlers
        renderSeamGraph();

        //drawImage(image);

        //WritableImage writableImage = new WritableImage(new SeamGraphPixelReader(sf), image.widthProperty().intValue(), image.heightProperty().intValue());

        //loadDataIntoWritableImage(writableImage, image, sf);
        //imageView.setImage(writableImage);
        //imageView.setImage(image);

        // changed from
        //imageView.setImage(image);
        // end of changed from

        Group root = new Group(imageCanvas);

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

        popupStage.setWidth(windowWidth);
        popupStage.setHeight(windowHeight);
        popupStage.setTitle("Displaying Image");
        popupStage.setScene(popupScene);
        popupStage.setResizable(true);
        popupStage.show();

        /*
        System.out.println("imageViewFitWidth = "  + imageView.getFitWidth());
        System.out.println("imageViewFitHeight = " + imageView.getFitHeight());
        imageView.setFitWidth(image.getWidth());
        imageView.setFitHeight(image.getHeight());
        imageView.setPreserveRatio(false);
         */

        imageCanvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                if (Math.abs(e.getX() - imageCanvas.getWidth()) < 10 && Math.abs(e.getY() - imageCanvas.getHeight()) < 10) {
                    imageCanvas.setCursor(Cursor.NW_RESIZE);
                    enableNorthResize = true;
                    enableWestResize = true;
                    enableImagePan = false;
                } else if (Math.abs(e.getX() - imageCanvas.getWidth()) < 10) {
                    imageCanvas.setCursor(Cursor.W_RESIZE);
                    enableNorthResize = false;
                    enableWestResize = true;
                    enableImagePan = false;
                } else if (Math.abs(e.getY() - imageCanvas.getHeight()) < 10) {
                    imageCanvas.setCursor(Cursor.N_RESIZE);
                    enableNorthResize = true;
                    enableWestResize = false;
                    enableImagePan = false;
                } else {
                    imageCanvas.setCursor(Cursor.DEFAULT);
                    enableNorthResize = false;
                    enableWestResize = false;
                    enableImagePan = true;
                }
            }
        });

        IntegerProperty pendingTasks = new SimpleIntegerProperty(0);

        imageCanvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                System.out.println("X coord of mouse event: " + e.getX());
                System.out.println("Y coord of mouse event: " + e.getY());
                double diffx = e.getX() - prevX;
                double diffy = e.getY() - prevY;

                if (diffx < 0 && enableWestResize) {
                    //imageWidth = imageWidth + diffx;
                    imageCanvas.setWidth(imageCanvas.widthProperty().doubleValue() + diffx);

                    for (int k=0; k<Math.abs(diffx); ++k) {
                        Task<SeamGraphTaskResult> task = new Task<>() {
                            @Override
                            public SeamGraphTaskResult call() {
                                sf.removeLowestEnergySeam();
                                return sf.getTaskResult();
                            }
                        };
                        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent workerStateEvent) {
                                System.out.println("Task completed!");
                                int numRunningTasks = seamGraphResizerTaskCount.decrementAndGet();
                                System.out.println("Number of remaining tasks = " + numRunningTasks);
                                SeamGraphTaskResult res = (SeamGraphTaskResult) workerStateEvent.getSource().getValue();
                                renderSeamGraph(res.imageData, res.numHorizVertices, res.numVertVertices);

                                System.out.println("numHorizVertices = " + res.numHorizVertices);
                                System.out.println("imageCanvas.width = "  + imageCanvas.widthProperty().doubleValue());
                            }
                        });
                        seamGraphResizerTaskCount.getAndIncrement();
                        totalNumberOfResizeTasksRun.getAndIncrement();
                        exec.submit(task); // single-threaded executor
                    }
                }
                if (diffy < 0 && enableNorthResize) {
                    //imageHeight = imageHeight + diffy;
                    imageCanvas.setHeight(imageCanvas.getHeight() + diffy);
                }
                prevX = e.getX();
                prevY = e.getY();
            }
        });

        imageCanvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                if (Math.abs(e.getX() - imageCanvas.getWidth()) < 10 || Math.abs(e.getY() - imageCanvas.getHeight()) < 10) {
                    prevX = e.getX();
                    prevY = e.getY();
                    System.out.println("prevX = " + prevX);
                    System.out.println("prevY = " + prevY);
                }
            }
        });

    }

    private void loadDataIntoWritableImage(WritableImage writableImage, Image image, DijkstraSeamFinderOptimized sf) {
        for (int x=0; x<image.widthProperty().intValue(); ++x) {
            for (int y=0; y<image.heightProperty().intValue(); ++y) {
                //writableImage.getPixelWriter().setArgb(x,y,image.getPixelReader().getArgb(x,y));
                writableImage.getPixelWriter().setArgb(x,y,sf.verticalSeamGraph.rgbfetcher.getRGB(x,y));
            }
        }
    }

    private void renderSeamGraph(byte[] imageData, int numHorizVertices, int numVertVertices) {
        imageCanvas.getGraphicsContext2D().getPixelWriter().setPixels(0,0,
                                                                      numHorizVertices,
                                                                      numVertVertices,
                                                                      PixelFormat.getByteRgbInstance(),
                                                                      imageData, 0,
                                                                      numHorizVertices*3);
        System.out.println("renderSeamGraph complete...");
    }

    @Deprecated
    private void renderSeamGraph() {
        byte[] imageData = new byte[sf.verticalSeamGraph.numHorizVertices * sf.verticalSeamGraph.numVertVertices * 3];

        int i=0;
        for (int y=0;y<sf.verticalSeamGraph.numVertVertices;++y) {
            for (int x=0;x<sf.verticalSeamGraph.numHorizVertices;++x) {
                int rgb = sf.verticalSeamGraph.rgbfetcher.getRGB(x,y);

                int b = (rgb >> 0) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int r = (rgb >> 16) & 0xFF;

                imageData[i] =   (byte) r;
                imageData[i+1] = (byte) g;
                imageData[i+2] = (byte) b;
                i+=3;
            }
        }

        imageCanvas.getGraphicsContext2D().getPixelWriter().setPixels(0,0,
                sf.verticalSeamGraph.numHorizVertices,
                sf.verticalSeamGraph.numVertVertices,
                PixelFormat.getByteRgbInstance(),
                imageData,
                0,
                sf.verticalSeamGraph.numHorizVertices*3);
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