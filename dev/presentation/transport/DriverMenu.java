package presentation.transport;

import service.TransportService;
import service.Response;
import service.DriverSL;

import java.util.List;

public class DriverMenu {
    private final TransportService transportService;

    public DriverMenu(TransportService transportService) {
        this.transportService = transportService;
    }

    public void start() {
        System.out.println("\n=== Drivers ===");

        List<DriverSL> drivers = transportService.getAllDrivers();

        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
            return;
        }

        for (DriverSL driver : drivers) {
            System.out.println(driver);
            System.out.println("--------------------");
        }
    }
}