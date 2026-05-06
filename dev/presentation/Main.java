package presentation;

import service.ServiceFactory;

public class Main {

    public static void main(String[] args) {

        boolean withData = args.length > 0 && "1".equals(args[0]);;
        ServiceFactory factory = new ServiceFactory(withData);
        

        MenuManager manager = new MenuManager(
                factory.getAuthService(),
                factory.getSchedulingService(),
                factory.getPersonnelService(),
                factory.getTransportService()
        );

        manager.start();
    }
}