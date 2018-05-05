package chatapplication;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.TransformerException;
import rmiinterfaces.*;
import messages.*;

public class ChatController implements Initializable, Service {

    @FXML
    private ImageView contactProfIcon;
    @FXML
    private Label contactName;
    @FXML
    private Circle contactStatus;
    @FXML
    private ImageView minimizeIcon;
    @FXML
    private ImageView closeIcon;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ComboBox<String> fontTypePicker;
    @FXML
    private ComboBox<Integer> fontSizePicker;
    @FXML
    private BorderPane contactAndMsgPane;
    @FXML
    private HBox bottomBar;
    @FXML
    private HBox topBar;
    @FXML
    private TextField typeMsgField;
    @FXML
    private ListView<Client> contactListView;
    @FXML
    private ComboBox<String> statusBox;
    @FXML
    private Button reqNotifications;
    @FXML
    private TextField addContactField;
    @FXML
    private ComboBox<String> otherNotifications;
    @FXML
    private ScrollPane messageScrollPane;
    @FXML
    private Button createGrBtn;
    @FXML
    private ImageView profilePic;
    @FXML
    private Label personName;
    @FXML
    private Circle clientStatus;
    @FXML
    private ImageView saveChat;
    @FXML
    private ImageView send;
    @FXML
    private ImageView attachFileIcon;
    private ArrayList<Client> matchedUsers;
    private Stage primaryStage;
    private double xOffset = 0;
    private double yOffset = 0;
    private static final ChatController instance = new ChatController();
    private CallServerRMI iConnection;
    private Client receiverClient;
    private ObservableList<Client> contactObservablelist;
    private ObservableList<Client> notificationsList;
    private ObservableList<String> otherNotificationsList;
    private ObservableList<String> statusObservableList;
    private ObservableList<String> fontObservableList;
    private ObservableList<Integer> sizeObservableList;
    private ClientImp personalData;
    private Message message;
    private static HashMap<String, VBox> vBoxesOfMsgs;
    private String lastClientName;
    private Boolean flag = false;
    private HBox msgTemp;
    private ContextMenu reqNotifiPop;
    private ContextMenu notifiPopMenu;
    private FileChooser fileChooser;
    private File file;
    private byte[] imageInByte;
    private ArrayList<Message> messages;
    private Boolean flage;

    public Client getReceiverClient() {
        return receiverClient;
    }

    public void setReceiverClient(Client receiverClient) {
        this.receiverClient = receiverClient;
    }

    public static HashMap<String, VBox> getMsgArea() {
        return vBoxesOfMsgs;
    }

    private ChatController() {
        messages = new ArrayList<>();
    }

    public static ChatController getInstance() {
        return instance;
    }

    public void setContactObservablelist(Client client) {
        Platform.runLater(() -> {
            this.contactObservablelist.add(client);
            vBoxesOfMsgs.put(client.getClient_user_name(), new VBox());
        });
    }

    public void setNotificationsList(Client client) {
        Platform.runLater(() -> {
            this.notificationsList.add(client);
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            personalData = (ClientImp) ServiceLocator.getService("clientService");
            System.out.println(getClass().getResource("..\\images"));
//            try {
//                otherNotifications.getButtonCell().setGraphic(new ImageView(new Image(getClass().getResource("..\\images\\notif.png").openStream())));
//            } catch (IOException ex) {
//                Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
//            }
            setClientData();
            String status[] = {"Online", "Offline", "Busy"};
            statusObservableList = FXCollections.observableArrayList(status);
            statusBox.setItems(statusObservableList);
            statusBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String t, String t1) {
                    try {
                        iConnection.setStatus(t1, personalData.getCurrentClient());
                    } catch (RemoteException ex) {
                        Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            vBoxesOfMsgs = new HashMap<>();
            notificationsList = FXCollections.observableArrayList(personalData.getRequestsList());
            colorPicker.setValue(Color.DARKGREY);
            colorPicker.setStyle("-fx-color-label-visible: false ;");
            fontObservableList = FXCollections.observableArrayList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
            fontTypePicker.setItems(fontObservableList);
            fontTypePicker.setValue(fontObservableList.get(0));
            Integer sizes[] = {10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32};
            sizeObservableList = FXCollections.observableArrayList(sizes);
            fontSizePicker.setItems(sizeObservableList);
            fontSizePicker.setValue(sizeObservableList.get(0));
            iConnection = (CallServerRMI) ServiceLocator.getService("rmiService");
            contactObservablelist = FXCollections.observableArrayList(personalData.getContactList());
            for (Client contactItem : personalData.getContactList()) {
                VBox vBox = new VBox();
                vBox.setSpacing(3.0);
                vBox.setPadding(new Insets(5.0));
                vBoxesOfMsgs.put(contactItem.getClient_user_name(), vBox);
            }
            contactListView.setCellFactory(new ContactListViewCellFactory());
            contactListView.setItems(contactObservablelist);
            otherNotificationsList = FXCollections.observableArrayList();
            otherNotifications.setItems(otherNotificationsList);
        } catch (RemoteException ex) {
            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void minimizeStage() {
        primaryStage = (Stage) topBar.getScene().getWindow();
        primaryStage.setIconified(true);
    }

    public void closeStage() {
        primaryStage = (Stage) minimizeIcon.getScene().getWindow();
        primaryStage.close();
    }

    public void sendMsg(KeyEvent e) {
        if (e.getCode().equals(KeyCode.ENTER)) {
            try {
                message = new Message();
                message.setSenderName(personalData.getCurrentClient());
                message.setReceiverName(receiverClient);
                message.setFont_color(colorPicker.getValue().toString());
                message.setFont_type(fontTypePicker.getValue());
                message.setFont_size(fontSizePicker.getValue().toString());
                message.setMsg_context(typeMsgField.getText());
                message.setMsg_date(LocalDateTime.now().toString());
                messages.add(message);
                iConnection.sendMsg(message);
                fontTypePicker.valueProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue ov, String t, String t1) {
                        message.setFont_type(t1);
                    }
                });
                fontSizePicker.valueProperty().addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue ov, Integer t, Integer t1) {
                        message.setFont_size(Double.toString(t1));
                    }
                });
                colorPicker.valueProperty().addListener(new ChangeListener<Color>() {
                    @Override
                    public void changed(ObservableValue ov, Color t, Color t1) {
                        message.setFont_color(t1.toString());
                    }
                });
                typeMsgField.setText("");
            } catch (RemoteException ex) {
                Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void searchToAddUsers(KeyEvent e) throws RemoteException {
        if (e.getCode().equals(KeyCode.ENTER)) {
            notifiPopMenu = new ContextMenu();
            matchedUsers = iConnection.matchUsers(addContactField.getText());
            if (matchedUsers.isEmpty()) {
                MenuItem notFoundItem = new MenuItem("User not found..");
                notifiPopMenu.getItems().add(notFoundItem);
            } else {
                for (Client matchedUser : matchedUsers) {
                    flage = true;
                    VBox addAndName = new VBox();
                    HBox notifBox = new HBox();
                    ImageView pic = new ImageView();
                    Label name = new Label();
                    Button add = new Button("Add");
                    Label reqSent = new Label("Request Sent");
                    name.setText(matchedUser.getClient_name());
                    if (matchedUser.getClient_image() != null) {
                        Image img = new Image(new ByteArrayInputStream(matchedUser.getClient_image()), 50, 50, true, true);
                        pic.setImage(img);
                    }

                    for (Client contact : personalData.getContactList()) {
                        System.out.println(matchedUser.getClient_user_name());
                        System.out.println(contact.getClient_user_name());
                        System.out.println(personalData.getCurrentClient().getClient_user_name());
                        if (matchedUser.getClient_user_name().equals(contact.getClient_user_name())
                                || matchedUser.getClient_user_name().equals(personalData.getCurrentClient()
                                        .getClient_user_name())) {
                            addAndName.getChildren().addAll(name);
                            flage = false;
                            break;
                        }
                    }
                    if (flage) {
                        addAndName.getChildren().addAll(name, add);
                    }
                    add.setOnAction((event) -> {
                        addAndName.getChildren().remove(add);
                        addAndName.getChildren().add(reqSent);
                        notifiPopMenu.show(addContactField.getScene().getWindow(), 300, 300);
                        try {
                            iConnection.tellServerToAdd(matchedUser, personalData.getCurrentClient());
                        } catch (RemoteException ex) {
                            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    notifBox.getChildren().addAll(pic, addAndName);
                    CustomMenuItem matchedUserItem = new CustomMenuItem(notifBox);
                    notifiPopMenu.getItems().add(matchedUserItem);
                }
            }
            notifiPopMenu.show(addContactField.getScene().getWindow(),
                    addContactField.getScene().getWindow().getX() + addContactField.getLayoutX() + 290,
                    addContactField.getScene().getWindow().getY() + addContactField.getLayoutY() + addContactField.getHeight() + 65);
        }
    }

    public void updateOtherNotifications(String notification) {
        Platform.runLater(() -> {
            otherNotificationsList.add(notification);
        });
    }

    public void setMsgVBox(VBox vBox) {
        messageScrollPane.setContent(vBox);
        messageScrollPane.setFitToHeight(true);
        messageScrollPane.setFitToWidth(true);
        typeMsgField.setDisable(false);
        saveChat.setDisable(false);
        send.setDisable(false);
        attachFileIcon.setDisable(false);
        colorPicker.setDisable(false);
        fontSizePicker.setDisable(false);
        fontTypePicker.setDisable(false);
        
    }

    public void displayMsg(Message msg) {
        messages.add(msg);
        Platform.runLater(() -> {
            try {
                if (msg.getReceiverName().getClient_user_name().equals(personalData.getCurrentClient().getClient_user_name())) {
                    display(vBoxesOfMsgs.get(msg.getSenderName().getClient_user_name()), msg, msg.getSenderName());
                } else {
                    display(vBoxesOfMsgs.get(msg.getReceiverName().getClient_user_name()), msg, msg.getSenderName());
                }
            } catch (RemoteException ex) {
                Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void display(VBox chatArea, Message msg, Client sender) {
        try {
            HBox msgLblWrapper = new HBox();
            Label msgLbl = new Label();
            HBox nameLblWrapper = new HBox();
            if (msg.getReceiverName().getClient_user_name().equals(personalData.getCurrentClient().getClient_user_name())) {
                msgLblWrapper.setAlignment(Pos.CENTER_LEFT);
                msgLbl.getStyleClass().add("firstMsg");
                nameLblWrapper.setAlignment(Pos.CENTER_LEFT);
                msgLblWrapper.getChildren().addAll(msgLbl);
            } else {
                msgLblWrapper.setAlignment(Pos.CENTER_RIGHT);
                msgLbl.getStyleClass().add("firstMsg");
                nameLblWrapper.setAlignment(Pos.CENTER_RIGHT);
                msgLblWrapper.getChildren().addAll(msgLbl);
            }
            if (lastClientName == null) {
                Label client = new Label(sender.getClient_name());
                nameLblWrapper.getChildren().add(client);
                msgLbl.setText(msg.getMsg_context());
                msgLbl.getStyleClass().add("firstMsg");
                Platform.runLater(() -> {
                    chatArea.getChildren().addAll(nameLblWrapper, msgLblWrapper);
                });
                lastClientName = sender.getClient_user_name();
            } else if (!lastClientName.equalsIgnoreCase(sender.getClient_user_name())) {
                Label client = new Label(sender.getClient_name());
                nameLblWrapper.getChildren().add(client);
                msgLbl.setText(msg.getMsg_context());
                msgLbl.getStyleClass().add("firstMsg");
                Platform.runLater(() -> {
                    chatArea.getChildren().addAll(nameLblWrapper, msgLblWrapper);
                });
                lastClientName = sender.getClient_user_name();
            } else if (lastClientName.equalsIgnoreCase(sender.getClient_user_name())) {
                msgLbl.setText(msg.getMsg_context());
                Platform.runLater(() -> {
                    chatArea.getChildren().addAll(msgLblWrapper);
                });
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createGroup() {
        //contactListView.setCellFactory(new CreateGrListviewCellFactory());
        contactListView.setItems(contactObservablelist);
    }

    public void retriveRequests() {
        reqNotifiPop = new ContextMenu();
        for (Client item : notificationsList) {
            VBox notifContainer = new VBox();
            HBox topHBox = new HBox();
            HBox bottomHBox = new HBox();
            Button acceptReqBtn = new Button("Accept");
            Button removeReqBtn = new Button("Ignore");
            Label client_name = new Label();
            ImageView reqProfPic = new ImageView();
            client_name.setText(item.getClient_name() + " sent you a friend request");
            if (item.getClient_image() != null) {
                Image img = new Image(new ByteArrayInputStream(item.getClient_image()), 50, 50, true, true);
                reqProfPic.setImage(img);
            }
            topHBox.getChildren().addAll(reqProfPic, client_name);
            bottomHBox.getChildren().addAll(acceptReqBtn, removeReqBtn);
            notifContainer.getChildren().addAll(topHBox, bottomHBox);
            CustomMenuItem matchedUserItem = new CustomMenuItem(notifContainer);
            reqNotifiPop.getItems().add(matchedUserItem);
            reqNotifiPop.show(reqNotifications.getScene().getWindow(),
                    reqNotifications.getScene().getWindow().getX() + reqNotifications.getLayoutX() + 290,
                    reqNotifications.getScene().getWindow().getY() + reqNotifications.getLayoutY() + reqNotifications.getHeight() + 65);
            acceptReqBtn.setOnAction((e) -> {
                try {
                    System.out.println("accept request");
                    personalData.getContactList().add(item);
                    iConnection.responseToRequest(item, personalData.getCurrentClient());
                    reqNotifiPop.hide();
                    notificationsList.remove(item);
                } catch (RemoteException ex) {
                    Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            removeReqBtn.setOnAction((e) -> {
                try {
                    System.out.println("ignore request");
                    iConnection.tellServerToRemove(item.getClient_user_name(),
                            personalData.getCurrentClient().getClient_user_name());
                    reqNotifiPop.hide();
                    notificationsList.remove(item);
                } catch (RemoteException ex) {
                    Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    public void setContactData() {
        if (receiverClient.getClient_image() != null) {
            Image img = new Image(new ByteArrayInputStream(receiverClient.getClient_image()), 40, 48, true, true);
            contactProfIcon.setImage(img);
        }
        contactName.setText(receiverClient.getClient_name());
        switch (receiverClient.getClient_status()) {
            case "online":
                contactStatus.setFill(Paint.valueOf("GREEN"));
                break;
            case "offline":
                contactStatus.setFill(Paint.valueOf("GREY"));
                break;
            case "busy":
                contactStatus.setFill(Paint.valueOf("RED"));
                break;
            case "away":
                contactStatus.setFill(Paint.valueOf("YELLOW"));
                break;
            default:
                contactStatus.setFill(Paint.valueOf("GREEN"));
                break;
        }
    }

    public void setClientData() {
        try {
            if (personalData.getCurrentClient().getClient_image() != null) {
                Image img = new Image(new ByteArrayInputStream(personalData.getCurrentClient().getClient_image()), 40, 48, true, true);
                profilePic.setImage(img);
            }
            personName.setText(personalData.getCurrentClient().getClient_name());
            switch (personalData.getCurrentClient().getClient_status()) {
                case "online":
                    clientStatus.setFill(Paint.valueOf("GREEN"));
                    break;
                case "offline":
                    clientStatus.setFill(Paint.valueOf("GREY"));
                    break;
                case "busy":
                    clientStatus.setFill(Paint.valueOf("RED"));
                    break;
                case "away":
                    clientStatus.setFill(Paint.valueOf("YELLOW"));
                    break;
                default:
                    clientStatus.setFill(Paint.valueOf("GREEN"));
                    break;
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setImage() {
        try {
            fileChooser = new FileChooser();
            file = fileChooser.showOpenDialog(minimizeIcon.getScene().getWindow());
            if (file != null) {
                BufferedImage originalImage = ImageIO.read(file);
                ByteArrayOutputStream originalImageToBytes = new ByteArrayOutputStream();
                ImageIO.write(originalImage, file.getName().substring(file.getName().lastIndexOf(".") + 1), originalImageToBytes);
                originalImageToBytes.flush();
                imageInByte = originalImageToBytes.toByteArray();
                originalImageToBytes.close();
                iConnection.sendImage(imageInByte, personalData.getCurrentClient());
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendFile() {
        try {
            fileChooser = new FileChooser();
            file = fileChooser.showOpenDialog(minimizeIcon.getScene().getWindow());
            if (file != null) {
                FileInputStream fileInputStream = new FileInputStream(file);
                iConnection.sendFile(fileInputStream, receiverClient, file.getName());
                System.out.println("chatcontroller finished");
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createXML() throws TransformerException {
        try {
            JAXBContext context = JAXBContext.newInstance("messages");
            ObjectFactory obj = new ObjectFactory();
            ContentType content = obj.createContentType();
            List<MsgType> messagesList = content.getMessage();
            for (Message usersMsg : messages) {
                if ((usersMsg.getReceiverName().getClient_user_name().equals(receiverClient.getClient_user_name()) || (usersMsg.getSenderName().getClient_user_name().equals(receiverClient.getClient_user_name())))) {
                    MsgType msg = obj.createMsgType();
                    if(personalData.getCurrentClient().getClient_user_name().equalsIgnoreCase(message.getReceiverName().getClient_user_name())){                        
                        msg.setType("reciever");
                    }else{
                        msg.setType("sender");
                    }                    
                    msg.setFrom(usersMsg.getReceiverName().getClient_name());
                    msg.setTo(usersMsg.getSenderName().getClient_name());
                    msg.setBody(usersMsg.getMsg_context());
                    msg.setColor(usersMsg.getFont_color());
                    msg.setDate(usersMsg.getMsg_date());
                    msg.setFont(usersMsg.getFont_type());
                    msg.setSize(usersMsg.getFont_size());
                    messagesList.add(msg);
                    JAXBElement JAXBMsg = obj.createMessages(content);
                    Marshaller marsh = context.createMarshaller();
                    marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    marsh.setProperty("com.sun.xml.internal.bind.xmlHeaders", "<?xml-stylesheet type=\"text/xsl\" href=\"Messages.xsl\"?>");
                    marsh.marshal(JAXBMsg, new FileOutputStream("messages" + LocalTime.now().getMinute() + ".xml"));
                    System.out.println("done");
                }
            }
        } catch (JAXBException ex) {
            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateImage() {
        try {
            if (personalData.getCurrentClient().getClient_image() != null) {
                Image img = new Image(new ByteArrayInputStream(personalData.getCurrentClient().getClient_image()), 40, 48, true, true);
                profilePic.setImage(img);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateStatus(String status) {
        switch (status.toLowerCase()) {
            case "online":
                clientStatus.setFill(Paint.valueOf("GREEN"));
                break;
            case "offline":
                clientStatus.setFill(Paint.valueOf("GREY"));
                break;
            case "busy":
                clientStatus.setFill(Paint.valueOf("RED"));
                break;
            case "away":
                clientStatus.setFill(Paint.valueOf("YELLOW"));
                break;
            default:
                clientStatus.setFill(Paint.valueOf("GREEN"));
                break;
        }
    }

    @Override
    public String getName() {
        return "chatController";
    }

    @Override
    public void excute() {

    }

}
