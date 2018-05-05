package chatapplication;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;

public class ClientUI extends Application implements Service {

    private Scene scene;
    private Stage stage;
    
    private double xOffset = 0;
    private double yOffset = 0;


    private static ClientUI instance = new ClientUI();

    public ClientUI() {

    }

    public static ClientUI getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            ClientLogFormController clientLogController = (ClientLogFormController) ServiceLocator.getService("clientLogController");
            loader.setController(clientLogController);
            loader.setLocation(getClass().getResource("ClientLogForm.fxml"));          
            Parent root = loader.load(getClass().getResource("ClientLogForm.fxml").openStream());
            root.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
            });
            scene = new Scene(root);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);        
            primaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {        
        Application.launch();
    }

    @Override
    public String getName() {
        return "main";
    }

    @Override
    public void excute() {

    }
}