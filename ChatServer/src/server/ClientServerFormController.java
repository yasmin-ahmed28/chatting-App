package server;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import rmiinterfaces.ClientRegData;

public class ClientServerFormController implements Initializable, Service {

    @FXML
    private TextField userNameTxt;
    @FXML
    private TextField emailTxt;
    @FXML
    private TextField passwordTxt;
    @FXML
    private TextField countryTxt;
    @FXML
    private TextField addressTxt;
    @FXML
    private RadioButton maleBtn;
    @FXML
    private RadioButton femaleBtn;
    @FXML
    private Button signUpBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private BorderPane serverFormMainPane;
    @FXML
    private HBox topBar;
    @FXML
    private Label emailRules;
    @FXML
    private Label passRules;
    private double xOffset = 0;
    private double yOffset = 0;
    private Stage stage;
    private ClientRegData clientRegData;
    private ToggleGroup genderGroup;
    private static ClientServerFormController instance = new ClientServerFormController();
    private static ServerController serverController;
    private DataBaseConnection dataBaseConnection;

    public static ClientServerFormController getInstance() {
        return instance;
    }

    private ClientServerFormController() {

    }

    public void signUpBtn() throws IOException {
        clientRegData = new ClientRegData();
        if (!(userNameTxt.getText().isEmpty() && emailTxt.getText().isEmpty() && passwordTxt.getText().isEmpty()
                && countryTxt.getText().isEmpty() && addressTxt.getText().isEmpty()
                && genderGroup.getSelectedToggle() == null)) {
            if ((userNameTxt.getText().length() > 6)) {
//                    && (userNameTxt.getText().matches(""))) {
                if (passwordTxt.getText().length() > 9) {
                    try {
                        clientRegData.setClient_user_name(userNameTxt.getText());
                        clientRegData.setAddress(emailTxt.getText());
                        clientRegData.setPassword(passwordTxt.getText());
                        clientRegData.setGender(genderGroup.getSelectedToggle().getUserData().toString());
                        dataBaseConnection.insertClientInfo(clientRegData);
                        stage = (Stage) userNameTxt.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader();
                        serverController = (ServerController) ServiceLocator.getService("serverController");
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
                                stage.setX(event.getScreenX() - xOffset);
                                stage.setY(event.getScreenY() - yOffset);
                            }
                        });
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.resizableProperty().set(false);
                        stage.show();
                        // primaryStage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                    } catch (IOException ex) {
                        Logger.getLogger(ClientServerFormController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    userNameTxt.setText("");
                    emailRules.setText("Not available");
                }
            } else {
                passwordTxt.setText("");
                passRules.setText("Not valid");
            }
        } else {
            userNameTxt.setText("");
            emailRules.setText("Not valid");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataBaseConnection = (DataBaseConnection) ServiceLocator.getService("dbConn");
        ToggleGroup genderGroup = new ToggleGroup();
        genderGroup.getToggles().add(maleBtn);
        genderGroup.getToggles().add(femaleBtn);
    }

    public void minimizeStage(MouseEvent event) {
        Stage stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
        stage.setIconified(true);
    }

    public void closeStage(MouseEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    public void cancelBtn() throws IOException {
        stage = (Stage) userNameTxt.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        serverController = (ServerController) ServiceLocator.getService("serverController");
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
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.resizableProperty().set(false);
        stage.show();
    }

    @Override
    public String getName() {
        return "ClientServerFormController";
    }

    @Override
    public void excute() {

    }

}
