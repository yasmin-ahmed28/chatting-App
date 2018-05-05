package chatapplication;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import rmiinterfaces.*;

public class ClientRegController implements Initializable, Service {

    @FXML
    private Label usernameStatus;
    @FXML
    private Label passwordStatus;
    @FXML
    private TextField nameTxt;
    @FXML
    private TextField userNameTxt;
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
    private Button cancelBtn;
    @FXML
    private BorderPane clientFormMainPane;
    @FXML
    private HBox topBar;

    private double xOffset = 0;
    private double yOffset = 0;
    private Stage primaryStage;
    private final CallServerRMI iConnection;
    private ClientRegData clientRegData;
    private final String[] countries = {"Egypt", "France", "Italy", "Brazil"};
    private ObservableList<String> countriesBox;
    ToggleGroup genderGroup;

    private static final ClientRegController instance = new ClientRegController();

    private ClientRegController() {
        iConnection = (CallServerRMI) ServiceLocator.getService("rmiService");
    }

    public static ClientRegController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    public void signUp() throws RemoteException {
        clientRegData = new ClientRegData();
        if (!(nameTxt.getText().isEmpty() && userNameTxt.getText().isEmpty() && passwordTxt.getText().isEmpty()
                && addressTxt.getText().isEmpty() && genderGroup.getSelectedToggle() == null)) {
            if ((userNameTxt.getText().length() > 6)) {
//                    && (userNameTxt.getText().matches(""))) {
                if (passwordTxt.getText().length() > 9) {
                    if (!iConnection.checkUserName(userNameTxt.getText())) {
                        try {
                            clientRegData.setAddress(addressTxt.getText());
                            clientRegData.setPassword(passwordTxt.getText());
                            clientRegData.setClient_name(nameTxt.getText());
                            clientRegData.setClient_user_name(userNameTxt.getText());
                            clientRegData.setCountry(countryTxt.getValue());
                            clientRegData.setGender(genderGroup.getSelectedToggle().getUserData().toString());
                            iConnection.signUp(clientRegData);
                            primaryStage = (Stage) userNameTxt.getScene().getWindow();
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
                            Scene scene = new Scene(root);
                            primaryStage.setScene(scene);
                        } catch (IOException ex) {
                            Logger.getLogger(ClientRegController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        userNameTxt.setText("");
                        usernameStatus.setText("Not available");
                    }
                } else {
                    passwordTxt.setText("");
                    passwordStatus.setText("Not valid");
                }
            } else {
                userNameTxt.setText("");
                usernameStatus.setText("Not valid");
            }
        }
    }

    public void cancel() throws IOException {
        primaryStage = (Stage) userNameTxt.getScene().getWindow();
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
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    public void minimizeStage(MouseEvent event) {
        primaryStage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
        primaryStage.setIconified(true);
    }

    public void closeStage(MouseEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    @Override
    public String getName() {
        return "clientRegController";
    }

    @Override
    public void excute() {

    }

}
