package rmiinterfaces;

import com.healthmarketscience.rmiio.RemoteInputStream;
import java.io.InputStream;
import java.rmi.*;
import java.util.ArrayList;

public interface ClientInt extends Remote {

    void receiveNotification(String notification) throws RemoteException;

    void receiveMsg(Message msg) throws RemoteException;

    public ArrayList<Client> getContactList() throws RemoteException;

    public void setContactList(ArrayList<Client> contactList) throws RemoteException;

    public Client getCurrentClient() throws RemoteException;

    public void setCurrentClient(Client currentClient) throws RemoteException;

    public ArrayList<Client> getRequestsList() throws RemoteException;

    public void setRequestsList(ArrayList<Client> requestsList) throws RemoteException;
    
    public void addToContactList(Client client) throws RemoteException;
    
    void updateNotifList(Client client) throws RemoteException;   // requests 
    
    void updateImage() throws RemoteException;
    
    void receiveFile(RemoteInputStream data,String name) throws RemoteException;

    void writeToFile(InputStream stream,String name) throws RemoteException;
    
    void updateStatus(String status) throws RemoteException;
}
