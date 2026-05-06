package presentation.transport;

import presentation.InputUtil;
import service.TransportService;

public class TransportMenu {
    private final TransportService transportService;

    public TransportMenu(TransportService transportService) {
        this.transportService = transportService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Transport Menu ===");
            System.out.println("1. Deliveries");
            System.out.println("2. Trucks");
            System.out.println("3. Drivers");
            System.out.println("4. Sites");
            System.out.println("0. Back");

            int choice = InputUtil.readInt("Choose option: ");

            switch (choice) {
                case 1:
                    new DeliveryMenu(transportService).start();
                    break;
                case 2:
                    new TruckMenu(transportService).start();
                    break;
                case 3:
                    new DriverMenu(transportService).start();
                    break;
                case 4:
                    new SiteMenu(transportService).start();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}