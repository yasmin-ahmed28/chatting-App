package server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.rmi.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.*;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;
import rmiinterfaces.*;

public class DataBaseConnection implements Service {

    private Connection connection;
    private PreparedStatement preparedStmt;
    private OraclePreparedStatement oraclePreparedStatement;
    private Statement stmt;
    private ResultSet resultSet;
    private static final DataBaseConnection instance = new DataBaseConnection();
    private ServerImpl serverImpl;
    private ArrayList<Client> contactlist;
    private ArrayList<Client> requestsList;

    private DataBaseConnection() {
        serverImpl = (ServerImpl) ServiceLocator.getService("serverImpl");
        try {
            DriverManager.registerDriver(new OracleDriver());
            connection = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "chat", "year");
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static DataBaseConnection getInstance() {
        return instance;
    }

    public void insertAdminInfo(ClientRegData adminRegData) {
        try {
            preparedStmt = connection.prepareStatement("INSERT INTO admin_data (admin_user_name,"
                    + " admin_password, admin_gender ,admin_address) VALUES (?,?,?,?)",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            preparedStmt.setString(1, adminRegData.getClient_user_name());
            preparedStmt.setString(2, adminRegData.getPassword());
            preparedStmt.setString(3, adminRegData.getGender());
            preparedStmt.setString(4, adminRegData.getAddress());
            preparedStmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Client> searchUserName(String client_user_name) {
        ArrayList<Client> matchedUsers = new ArrayList<>();
        try {
            stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = stmt.executeQuery("select *  from client_data where client_user_name like '%"
                    + client_user_name + "%" + "'");
            while (resultSet.next()) {
                Client client = new Client();
                BLOB blob = (BLOB) resultSet.getObject("client_pic");
                if (blob != null) {
                    int blobLength = (int) blob.length();
                    client.setClient_image(blob.getBytes(1, blobLength));
                }
                client.setClient_name(resultSet.getString("client_name"));
                client.setClient_user_name(resultSet.getString("client_user_name"));
                matchedUsers.add(client);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return matchedUsers;
    }

    public Boolean clientValidate(String client_user_name, String password) throws SQLException {
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        resultSet = stmt.executeQuery("select * from client_data where client_user_name = " + "'" + client_user_name + "'");
        if (resultSet.first()) {
            if (resultSet.getString("client_password").equals(password)) {
                return true;
            }
        }
        return false;
    }

    public Boolean adminValidate(String admin_user_name, String password) throws SQLException {
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        resultSet = stmt.executeQuery("select * from admin_data where admin_user_name = " + "'" + admin_user_name + "'");
        if (resultSet.first()) {
            if (resultSet.getString("admin_password").equals(password)) {
                return true;
            }
        }
        return false;
    }
    
    public void insertClientInfo(ClientRegData clientRegData) {
        try {
            preparedStmt = connection.prepareStatement("INSERT INTO client_data ("
                    + "client_user_name,client_password,client_gender,client_status,client_address,client_name) "
                    + "VALUES (?,?,?,?,?,?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            preparedStmt.setString(1, clientRegData.getClient_user_name());
            preparedStmt.setString(2, clientRegData.getPassword());
            preparedStmt.setString(3, clientRegData.getGender());
            preparedStmt.setString(4, "online");
            preparedStmt.setString(5, clientRegData.getAddress());
            preparedStmt.setString(6, clientRegData.getClient_name());
            preparedStmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getStatus(String username) throws SQLException {
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        resultSet = stmt.executeQuery("select client_status from client_data where client_id = "
                + "'" + username + "'");
        if (resultSet.next()) {
            return resultSet.getString("client_status");
        }
        return null;
    }

    public Client fillData(String username) throws SQLException, RemoteException {
        Client client = new Client();
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        resultSet = stmt.executeQuery("select * from client_data where client_user_name = " + "'" + username + "'");
        if (resultSet.next()) {
            BLOB blob = (BLOB) resultSet.getObject("client_pic");
            if (blob != null) {
                int blobLength = (int) blob.length();
                client.setClient_image(blob.getBytes(1, blobLength));
            }
            client.setClient_status(resultSet.getString("client_status"));
            client.setClient_name(resultSet.getString("client_name"));
            client.setClient_user_name(username);
            return client;
        }
        return null;
    }

    public void setStatus(String status,Client client) {
        try {
            preparedStmt = connection.prepareStatement("UPDATE client_data set client_status = ? where client_user_name = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            preparedStmt.setString(1,status);  
            preparedStmt.setString(2,client.getClient_user_name());  
            preparedStmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Client> getContactList(String username) {
        try {
            contactlist = new ArrayList<>();
            stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = stmt.executeQuery("select * from contact_list cl join client_data cd "
                    + "on cl.CONTACT_USER_NAME = cd.client_user_name where cl.client_user_name = " + "'" + username + "'");
            while (resultSet.next()) {
                Client client = new Client();
                BLOB blob = (BLOB) resultSet.getObject("client_pic");
                if (blob != null) {
                    int blobLength = (int) blob.length();
                    client.setClient_image(blob.getBytes(1, blobLength));
                }
                client.setClient_user_name(resultSet.getString("contact_user_name"));
                client.setClient_name(resultSet.getString("client_name"));
                client.setClient_status(resultSet.getString("client_status"));
                contactlist.add(client);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contactlist;
    }

    public ArrayList<Client> getAllRequests(String username) {
        try {
            requestsList = new ArrayList<>();
            stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = stmt.executeQuery("select * from requests rq join client_data cd "
                    + "on rq.SENDER_USER_NAME = cd.client_user_name where rq.receiver_user_name = " + "'" + username + "'");
            while (resultSet.next()) {
                Client client = new Client();
                BLOB blob = (BLOB) resultSet.getObject("client_pic");
                if (blob != null) {
                    int blobLength = (int) blob.length();
                    client.setClient_image(blob.getBytes(1, blobLength));
                }
                client.setClient_user_name(resultSet.getString("client_user_name"));
                client.setClient_name(resultSet.getString("client_name"));
                client.setClient_status(resultSet.getString("client_status"));
                requestsList.add(client);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return requestsList;
    }

    public void acceptContact(Client sender, Client receiver) throws RemoteException {
        try {
            preparedStmt = connection.prepareStatement("INSERT INTO contact_list (contact_user_name, client_user_name)"
                    + "VALUES (?,?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            preparedStmt.setString(1, sender.getClient_user_name());
            preparedStmt.setString(2, receiver.getClient_user_name());
            preparedStmt.executeUpdate();
            preparedStmt.setString(2, sender.getClient_user_name());
            preparedStmt.setString(1, receiver.getClient_user_name());
            preparedStmt.executeUpdate();
            removeRequest(sender.getClient_user_name(), receiver.getClient_user_name());
            System.out.println("accepted");
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeRequest(String reqSender, String reqReceiver) throws SQLException, RemoteException {
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        stmt.executeUpdate("delete from requests where sender_user_name = " + "'" + reqSender + "'"
                + " and receiver_user_name = " + "'" + reqReceiver + "'");
    }

    public void addToRequestsTable(Client receiver, Client sender) {
        try {
            preparedStmt = connection.prepareStatement("INSERT INTO requests (receiver_user_name, SENDER_USER_NAME)"
                    + "VALUES (?,?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            preparedStmt.setString(1, receiver.getClient_user_name());
            preparedStmt.setString(2, sender.getClient_user_name());
            preparedStmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Boolean uniqueUserName(String username) throws SQLException {
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        resultSet = stmt.executeQuery("select * from client_data where client_user_name = " + "'" + username + "'");
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    public void setImage(byte[] imageInByte, Client client) {
        try {
            InputStream in = new ByteArrayInputStream(imageInByte);
            preparedStmt = connection.prepareStatement("UPDATE client_data set client_pic = ? where client_user_name = ?");
            preparedStmt.setBinaryStream(1, in, imageInByte.length);
            preparedStmt.setString(2, client.getClient_user_name());
            preparedStmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getOnUsersNum() throws SQLException {
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        resultSet = stmt.executeQuery("select count(*) from client_data where client_status = 'online'");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }

    public int getOffUsersNum() throws SQLException {
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        resultSet = stmt.executeQuery("select count(*) from client_data where client_status = 'offline'");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }
    
    public int getAllUsersNum() throws SQLException {
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        resultSet = stmt.executeQuery("select count(*) from client_data");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }
    
    @Override
    public String getName() {
        return "dbConn";
    }

    @Override
    public void excute() {
    }

}
