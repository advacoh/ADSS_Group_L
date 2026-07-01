package presentation.transport;

import presentation.InputUtil;
import service.DriverSL;
import service.TransportService;

import java.util.List;

public class DriverMenu {
    private final TransportService transportService;

    public DriverMenu(TransportService transportService) {
        this.transportService = transportService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Driver Menu ===");
            System.out.println("1) View all drivers");
            System.out.println("2) View driver details");
            System.out.println("3) Back");

            switch (InputUtil.readInt("Choose option: ")) {
                case 1 -> viewAllDrivers();
                case 2 -> viewDriverDetails();
                case 3 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewAllDrivers() {
        List<DriverSL> drivers = transportService.getAllDrivers();

        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
            return;
        }

        System.out.println("\n=== Drivers ===");
        for (int i = 0; i < drivers.size(); i++) {
            System.out.println((i + 1) + ") " + drivers.get(i).shortString());
        }
    }

    private void viewDriverDetails() {
        List<DriverSL> drivers = transportService.getAllDrivers();

        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
            return;
        }

        DriverSL selected = InputUtil.selectItem(drivers);
        if (selected == null) return;

        System.out.println("\n--- Driver Details ---");
        System.out.println(selected);
    }
}