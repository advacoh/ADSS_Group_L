package dev.presentation;

import dev.service.ServiceFactory;

public class Main {

    public static void main(String[] args) {

        boolean empty = args.length > 0 && args[0].equals("1");

        ServiceFactory factory = new ServiceFactory(empty);

        MenuManager manager = new MenuManager(
                factory.getAuthService(),
                factory.getSchedulingService(),
                factory.getPersonnelService()
        );

        manager.start();
    }
}