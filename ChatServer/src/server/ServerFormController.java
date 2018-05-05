package server;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import rmiinterfaces.ClientRegData;

public class ServerFormController implements Initializable, Service {

    @FXML
    private TextField userNameTxt;
    @FXML
    private TextField emailTxt;
    @FXML
    private TextField passwordTxt;
    @FXML
    private ComboBox<String> countryTxt;
    @FXML
    private TextField addressTxt;
    @FXML
    private RadioButton maleBtn;
    @FXML
    private RadioButton femaleBtn;
    @FXML
    private Button signUpBtn;
    @FXML
    private Button cancelbtn;
    @FXML
    private BorderPane serverFormMainPane;
    @FXML
    private HBox topBar;
    @FXML
    private Label emailRules;
    @FXML
    private Label passRules;

    private Stage stage;
    private ClientRegData clientRegData;
    private ToggleGroup genderGroup;
    private static ServerFormController instance = new ServerFormController();
    private static AdminLoginFormController adminLogInstance;
    private final String[] countries = {"Egypt", "France", "Italy", "Brazil"};
    private ObservableList<String> countriesBox;
    private DataBaseConnection dataBaseConnection;
    private ServerController serverController;
    private double xOffset = 0;
    private double yOffset = 0;

    public static ServerFormController getInstance() {
        return instance;
    }

    private ServerFormController() {

    }

    public void signUpBtn() throws IOException {

        clientRegData = new ClientRegData();
        if (!(userNameTxt.getText().isEmpty() && emailTxt.getText().isEmpty() && passwordTxt.getText().isEmpty()
                && addressTxt.getText().isEmpty() && genderGroup.getSelectedToggle() == null)) {
            if ((userNameTxt.getText().length() > 6)) {
//                    && (userNameTxt.getText().matches(""))) {
                if (passwordTxt.getText().length() > 9) {
                    try {
                        clientRegData.setClient_user_name(userNameTxt.getText());
                        clientRegData.setAddress(emailTxt.getText());
                        clientRegData.setPassword(passwordTxt.getText());
                        clientRegData.setGender(genderGroup.getSelectedToggle().getUserData().toString());
                        dataBaseConnection.insertAdminInfo(clientRegData);
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
                        Logger.getLogger(ServerFormController.class.getName()).log(Level.SEVERE, null, ex);
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
        countriesBox = FXCollections.observableArrayList();
        for (String country : countries) {
            countriesBox.add(country);
        }
        genderGroup = new ToggleGroup();
        genderGroup.getToggles().add(maleBtn);
        maleBtn.setUserData("male");
        genderGroup.getToggles().add(femaleBtn);
        femaleBtn.setUserData("female");
        countryTxt.setItems(countriesBox);
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
        adminLogInstance = (AdminLoginFormController) ServiceLocator.getService("adminLoginController");
        loader.setController(adminLogInstance);
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
        stage.setScene(scene);
        stage.resizableProperty().set(false);
        stage.show();
    }

    @Override
    public String getName() {
        return "ServerFormController";
    }

    @Override
    public void excute() {

    }

}
