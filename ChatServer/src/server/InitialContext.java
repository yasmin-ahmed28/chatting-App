package server;

public class InitialContext {

    public Object lookup(String jndiName) {
        if (jndiName.equalsIgnoreCase("serverController")) {
            return ServerController.getInstance();
        } else if (jndiName.equalsIgnoreCase("dbConn")) {
            return DataBaseConnection.getInstance();
        } else if (jndiName.equalsIgnoreCase("serverImpl")) {
            return ServerImpl.getInstance();
        } else if (jndiName.equalsIgnoreCase("adminLoginController")) {
            return AdminLoginFormController.getInstance();
        } else if (jndiName.equalsIgnoreCase("ServerFormController")) {
            return ServerFormController.getInstance();
        } else if (jndiName.equalsIgnoreCase("ClientServerFormController")) {
            return ClientServerFormController.getInstance();
        }

        return null;
    }
}
