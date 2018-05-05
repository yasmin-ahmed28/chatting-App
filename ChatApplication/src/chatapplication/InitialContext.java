package chatapplication;


public class InitialContext {

    public Object lookup(String jndiName) {

        if (jndiName.equalsIgnoreCase("chatController")) {
            return ChatController.getInstance();
        }else if(jndiName.equalsIgnoreCase("rmiService")){
            return CallServerRMI.getInstance();
        }else if(jndiName.equalsIgnoreCase("clientRegController")){
            return ClientRegController.getInstance();
        }else if(jndiName.equalsIgnoreCase("clientLogController")){
            return ClientLogFormController.getInstance();
        }else if(jndiName.equalsIgnoreCase("clientService")){
            return ClientImp.getInstance();
        }else if(jndiName.equalsIgnoreCase("main")){
            return ClientUI.getInstance();
        }
        
        return null;
    }
}
