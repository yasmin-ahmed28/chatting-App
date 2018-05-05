package server;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import rmiinterfaces.ServerInt;

public class ServerController implements Initializable, Service {

    @FXML
    private Button sendToAll;
    @FXML
    private Button serverStart;
    @FXML
    private Button serverStop;
    @FXML
    private Button signOut;
    @FXML
    private Label offUsersVal;
    @FXML
    private Label onUsersVal;
    @FXML
    private TextArea notificationTxt;
    @FXML
    private BarChart<String, Integer> barChart;
    @FXML
    private Button addUsers;
    @FXML
    private Label serverRunningStatus;
    @FXML
    private Circle circleServerStatus;
    private Integer online;
    private Integer offline;
    private Integer busy;
    private Registry reg;
    private ServerInt serverObj;
    private static ServerController instance = new ServerController();
    private final ServerImpl serverImpl;
    private final DataBaseConnection dbConn;
    private AdminLoginFormController adminLoginFormController;
    private Stage primaryStage;
    private ClientServerFormController clientServerFormController;
    private double xOffset = 0;
    private double yOffset = 0;

    private ServerController() {
        dbConn = (DataBaseConnection) ServiceLocator.getService("dbConn");
        serverImpl = (ServerImpl) ServiceLocator.getService("serverImpl");
        serverImpl.excute();
    }

    public static ServerController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            serverObj = (ServerInt) ServiceLocator.getService("serverImpl");
            reg = LocateRegistry.createRegistry(5005);
            serverStart.setShape(new Circle(50));
            serverStart.setMaxSize(100, 100);
            addUsers.setShape(new Circle(50));
            addUsers.setMaxSize(100, 100);
            serverStop.setShape(new Circle(50));
            serverStop.setMaxSize(100, 100);
            signOut.setShape(new Circle(70));
            signOut.setMaxSize(120, 120);
            sendToAll.setShape(new Circle(80));
            sendToAll.setMaxSize(120, 120);
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
           
            barChart.setTitle("Statistics About Clients");
          
            xAxis.setLabel("Status");
            yAxis.setLabel("Number Of Users");
            XYChart.Series series1 = new XYChart.Series();
            series1.setName("Status");
            int x = dbConn.getAllUsersNum();
            new Thread(() -> {               
                while (true) {                     
                    try {
                        Thread.sleep(10000L);                                                              
                        Platform.runLater(() -> {
                            series1.getData().add(new XYChart.Data("Online", ServerImpl.getClients().size()));
                            series1.getData().add(new XYChart.Data("Offline", x-ServerImpl.getClients().size()));
                        });                        
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
            barChart.getData().addAll(series1);
        } catch (RemoteException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start() {
        try {
            reg.rebind("chatService", serverObj);
            circleServerStatus.setFill(Paint.valueOf("GREEN"));
            serverRunningStatus.setText("Running");
        } catch (RemoteException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() throws NotBoundException {
        try {
            reg.unbind("chatService");
            circleServerStatus.setFill(Paint.valueOf("RED"));
            serverRunningStatus.setText("Stopped");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addUsers() throws IOException {
        primaryStage = (Stage) notificationTxt.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        clientServerFormController = (ClientServerFormController) ServiceLocator.getService("ClientServerFormController");
        loader.setController(clientServerFormController);
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
    }

    public void signOut() throws IOException {
        primaryStage = (Stage) notificationTxt.getScene().getWindow();
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
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    public void sendNotification() {
        serverImpl.sendNotificationsToAll(notificationTxt.getText());
    }

    public void minimizeStage(MouseEvent event) {
        Stage stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
        stage.setIconified(true);
    }

    public void closeStage(MouseEvent event) {
        try {
            ((Node) (event.getSource())).getScene().getWindow().hide();
            reg.unbind("chatService");
            System.out.println("stopped");
        } catch (RemoteException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getName() {
        return "serverController";
    }

    @Override
    public void excute() {

    }

}
