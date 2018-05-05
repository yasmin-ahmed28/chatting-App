package server;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AdminLoginFormController implements Initializable, Service {

    @FXML
    private Button signInBtn;
    @FXML
    private Button signUpBtn;
    @FXML
    private TextField userNameTxt;
    @FXML
    private TextField passwordTxt;

    private Stage primaryStage;
    private static AdminLoginFormController instance = new AdminLoginFormController();
    private DataBaseConnection baseConnection;

    private double xOffset = 0;
    private double yOffset = 0;

    private AdminLoginFormController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        baseConnection = (DataBaseConnection) ServiceLocator.getService("dbConn");
    }

    public static AdminLoginFormController getInstance() {
        return instance;
    }

    public void signUpBtn() {
        try {
            primaryStage = (Stage) userNameTxt.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            ServerFormController serverFormController = (ServerFormController) ServiceLocator.
                    getService("ServerFormController");
            loader.setController(serverFormController);
            loader.setLocation(getClass().getResource("ServerForm.fxml"));
            Parent root = loader.load(getClass().getResource("ServerForm.fxml").openStream());
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
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(AdminLoginFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void signInBtn() throws IOException {
        try {
            primaryStage = (Stage) userNameTxt.getScene().getWindow();
            if (baseConnection.adminValidate(userNameTxt.getText(), passwordTxt.getText())) {
                FXMLLoader loader = new FXMLLoader();
                ServerController serverController = (ServerController) ServiceLocator.
                        getService("serverController");
                loader.setController(serverController);
                loader.setLocation(getClass().getResource("Server.fxml"));
                Parent root = loader.load(getClass().getResource("Server.fxml").openStream());
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
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                userNameTxt.setText("Wrong user name or password");
                passwordTxt.setText("");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminLoginFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void minimizeStage(MouseEvent event) {
        Stage stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
        stage.setIconified(true);
    }

    public void closeStage(MouseEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    @Override
    public String getName() {
        return "adminLoginController";
    }

    @Override
    public void excute() {

    }

}
