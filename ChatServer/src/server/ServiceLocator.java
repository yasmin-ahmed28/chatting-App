package server;


public class ServiceLocator {

    private static Cach cach;

    static {
        cach = new Cach();
    }

    public static Service getService(String jndiName) {
        Service service = cach.getService(jndiName);
        if (service != null) {
            return service;
        }
        InitialContext context = new InitialContext();
        Service notExist = (Service) context.lookup(jndiName);
        cach.addService(notExist);
        return notExist;
    }
}
