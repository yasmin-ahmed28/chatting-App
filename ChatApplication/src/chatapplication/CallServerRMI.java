package chatapplication;

import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import java.io.FileInputStream;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.ArrayList;
import java.util.logging.*;
import rmiinterfaces.*;

public class CallServerRMI implements Service {

    private ClientImp personalData;
    private ServerInt serverInt;
    private static final CallServerRMI instance = new CallServerRMI();

    public ServerInt getServerInt() {
        return serverInt;
    }

    private CallServerRMI() {
        try {
            personalData = (ClientImp) ServiceLocator.getService("clientService");
            Registry reg = LocateRegistry.getRegistry("10.0.0.61",5005);
            serverInt = (ServerInt) reg.lookup("chatService");
        } catch (RemoteException ex) {
            Logger.getLogger(CallServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(CallServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMsg(Message msg) {
        try {
            serverInt.tellOthers(msg);
        } catch (RemoteException ex) {
            Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Boolean regToServer(String username, String password) {
        try {
            serverInt.register(username, password, personalData);
            if (personalData.getCurrentClient() == null) {
                return false;
            }
        } catch (RemoteException ex) {
            Logger.getLogger(CallServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static CallServerRMI getInstance() {
        return instance;
    }

    public ArrayList<Client> matchUsers(String userName) throws RemoteException {
        ArrayList<Client> returnMatchUsers = new ArrayList<>();
        returnMatchUsers = serverInt.searchUserName(userName);
        return returnMatchUsers;
    }

    public void responseToRequest(Client client, Client currClient) {
        try {
            serverInt.addUserName(client, currClient);
        } catch (RemoteException ex) {
            Logger.getLogger(CallServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tellServerToRemove(String reqSender, String reqReceiver) throws RemoteException {
        serverInt.removeRequestFromDB(reqSender, reqReceiver);
    }

    public void tellServerToAdd(Client client, Client currClient) throws RemoteException {
        serverInt.addToRequests(client, currClient);
    }

    public Boolean checkUserName(String username) throws RemoteException {
        if (serverInt.checkUserName(username)) {
            return true;
        }
        return false;
    }

    public void signUp(ClientRegData clientRegData) {
        try {
            serverInt.signUp(clientRegData);
        } catch (RemoteException ex) {
            Logger.getLogger(CallServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendImage(byte[] imageInByte, Client client) {
        try {
            serverInt.addImage(imageInByte, client);
        } catch (RemoteException ex) {
            Logger.getLogger(CallServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendFile(FileInputStream file, Client client,String name) {
        new Thread(() -> {
            try {
                SimpleRemoteInputStream istream = new SimpleRemoteInputStream(file);
                serverInt.forwardFile(istream.export(), client,name);
                System.out.println("callservertrmi finished");
            } catch (RemoteException ex) {
                Logger.getLogger(CallServerRMI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

    }

    public void close(){
        try {
            serverInt.unregister(personalData);
        } catch (RemoteException ex) {
            Logger.getLogger(CallServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setStatus(String status,Client client){
        try {
            serverInt.setStatus(status,client);
        } catch (RemoteException ex) {
            Logger.getLogger(CallServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String getName() {
        return "rmiService";
    }

    @Override
    public void excute() {

    }
}
