package presentation;

import service.ServiceFactory;

public class Main {

    public static void main(String[] args) {
        System.out.println(">>> MAIN CALLED, args: " + java.util.Arrays.toString(args));

        boolean withData = args.length > 0 && "1".equals(args[0]);;
        //  System.out.println("DB PATH: " + new java.io.File("supermarket.db").getAbsolutePath());
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