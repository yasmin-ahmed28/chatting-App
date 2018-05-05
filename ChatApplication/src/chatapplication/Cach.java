package chatapplication;



import java.util.ArrayList;
import java.util.List;

public class Cach {

    private List<Service> services;

    public Cach() {
        services = new ArrayList<Service>();
    }

    public Service getService(String serviceName) {

        for (Service service : services) {
            if (service.getName().equalsIgnoreCase(serviceName)) {
                return service;
            }
        }
        return null;
    }

    public void addService(Service newService) {
        boolean exists = false;

        for (Service service : services) {
            if (service.getName().equalsIgnoreCase(newService.getName())) {
                exists = true;
            }
        }
        if (!exists) {
            services.add(newService);
        }
    }
}
