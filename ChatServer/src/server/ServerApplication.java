package server;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ServerApplication extends Application implements Service {

    private AdminLoginFormController adminLoginFormController ;

    private double xOffset = 0;
    private double yOffset = 0;
    
    public ServerApplication() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        adminLoginFormController = (AdminLoginFormController) ServiceLocator.getService("adminLoginController");
        loader.setController(adminLoginFormController);
        loader.setLocation(getClass().getResource("AdminLoginForm.fxml"));
        Parent root = loader.load(getClass().getResource("AdminLoginForm.fxml").openStream());
        
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
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
       
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED.UNDECORATED);
        stage.setScene(scene);
        stage.resizableProperty().set(false);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public String getName() {
        return "server";
    }

    @Override
    public void excute() {

    }    
}
