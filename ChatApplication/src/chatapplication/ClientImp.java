package chatapplication;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import rmiinterfaces.*;

public class ClientImp extends UnicastRemoteObject implements ClientInt, Service {

    private ArrayList<Client> contactList;
    private Client currentClient;
    private ArrayList<Client> requestsList;
    private static ClientImp instance;
    private final ChatController chatController = (ChatController) ServiceLocator.getService("chatController");

    private ClientImp() throws RemoteException {
    }

    public static ClientImp getInstance() {
        if (instance == null) {
            try {
                instance = new ClientImp();
            } catch (RemoteException ex) {
                Logger.getLogger(ClientImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }

    @Override
    public void receiveNotification(String notification) throws RemoteException {
        chatController.updateOtherNotifications(notification);
        Platform.runLater(() -> {
            Notifications notificationBuilder = Notifications.create()
                    .title("Server Speaks")
                    .text(notification)
                    .hideAfter(Duration.seconds(10))
                    .position(Pos.TOP_RIGHT);
            notificationBuilder.darkStyle();
            notificationBuilder.showInformation();
            System.out.println("Finished");
        });
    }

    @Override
    public void receiveMsg(Message msg) throws RemoteException {
        chatController.displayMsg(msg);
    }

    @Override
    public ArrayList<Client> getContactList() throws RemoteException {
        return contactList;
    }

    @Override
    public void setContactList(ArrayList<Client> contactList) throws RemoteException {
        this.contactList = contactList;
    }

    @Override
    public Client getCurrentClient() throws RemoteException {
        return currentClient;
    }

    @Override
    public void setCurrentClient(Client currentClient) throws RemoteException {
        this.currentClient = currentClient;
    }

    @Override
    public ArrayList<Client> getRequestsList() throws RemoteException {
        return requestsList;
    }

    @Override
    public void setRequestsList(ArrayList<Client> requestsList) throws RemoteException {
        this.requestsList = requestsList;
    }

    @Override
    public String getName() {
        return "clientService";
    }

    @Override
    public void excute() {

    }

    @Override
    public void addToContactList(Client client) throws RemoteException {
        contactList.add(client);
        chatController.setContactObservablelist(client);
        Platform.runLater(() -> {
            Notifications notificationBuilder = Notifications.create()
                    .title("Server Speaks")
                    .text(client.getClient_name() + " accepted your friend request.")
                    .hideAfter(Duration.seconds(10))
                    .position(Pos.TOP_RIGHT);
            notificationBuilder.darkStyle();
            notificationBuilder.showInformation();
            System.out.println("Finished");
        });
    }

    @Override
    public void updateNotifList(Client client) throws RemoteException {
        requestsList.add(client);
        chatController.setNotificationsList(client);
        Platform.runLater(() -> {
            Notifications notificationBuilder = Notifications.create()
                    .title("Server Speaks")
                    .text(client.getClient_name() + " sent you a friend request.")
                    .hideAfter(Duration.seconds(10))
                    .position(Pos.TOP_RIGHT);
            notificationBuilder.darkStyle();
            notificationBuilder.showInformation();
            System.out.println("Finished");
        });
    }

    @Override
    public void updateImage() throws RemoteException {
        chatController.updateImage();
    }

    @Override
    public void receiveFile(RemoteInputStream data, String name) throws RemoteException {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("File Confirmation");
            alert.setContentText("Do you want to save file " + name + " ?");
            Optional<ButtonType> result = alert.showAndWait();
            if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                new Thread(() -> {
                    InputStream input = null;
                    try {
                        input = RemoteInputStreamClient.wrap(data);
                        writeToFile(input, name);
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ClientImp.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            input.close();
                        } catch (IOException ex) {
                            Logger.getLogger(ClientImp.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    Platform.runLater(() -> {
                        Notifications notificationBuilder = Notifications.create()
                                .title("File Completed")
                                .text(name + " successfully saved.")
                                .hideAfter(Duration.seconds(10))
                                .position(Pos.TOP_RIGHT);
                        notificationBuilder.darkStyle();
                        notificationBuilder.showInformation();
                        System.out.println("Finished");
                    });
                }).start();
            }
        });
    }

    @Override
    public void writeToFile(InputStream stream, String name) throws RemoteException {
        try {
            File file = File.createTempFile(name, "", new File("D:\\"));
            FileOutputStream output = new FileOutputStream(file);
            int chunk = 4096;
            byte[] result = new byte[chunk];
            int readBytes = 0;
            do {
                try {
                    readBytes = stream.read(result);
                    if (readBytes >= 0) {
                        output.write(result, 0, readBytes);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClientImp.class.getName()).log(Level.SEVERE, null, ex);
                }
            } while (readBytes != -1);
            output.flush();
            if (output != null) {
                output.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateStatus(String status) throws RemoteException {
        chatController.updateStatus(status);
    }
}
