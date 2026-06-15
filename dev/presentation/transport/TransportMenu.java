package presentation.transport;

import presentation.InputUtil;
import service.TransportService;

public class TransportMenu {
    private final DeliveryMenu deliveryMenu;
    private final TruckMenu truckMenu;
    private final DriverMenu driverMenu;
    private final SiteMenu siteMenu;

    public TransportMenu(TransportService transportService) {
        this.deliveryMenu = new DeliveryMenu(transportService);
        this.truckMenu = new TruckMenu(transportService);
        this.driverMenu = new DriverMenu(transportService);
        this.siteMenu = new SiteMenu(transportService);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Transport Menu ===");
            System.out.println("1) Deliveries");
            System.out.println("2) Trucks");
            System.out.println("3) Drivers");
            System.out.println("4) Sites");
            System.out.println("5) Back");

            switch (InputUtil.readInt("Choose option: ")) {
                case 1 -> deliveryMenu.start();
                case 2 -> truckMenu.start();
                case 3 -> driverMenu.start();
                case 4 -> siteMenu.start();
                case 5 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}