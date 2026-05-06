package presentation.transport;

import service.TransportService;
import service.Response;
import service.TruckSL;

import java.util.List;

public class TruckMenu {
    private final TransportService transportService;

    public TruckMenu(TransportService transportService) {
        this.transportService = transportService;
    }

    public void start() {
        System.out.println("\n=== Trucks ===");

        List<TruckSL> trucks = transportService.getAllTrucks();

        if (trucks.isEmpty()) {
            System.out.println("No trucks found.");
            return;
        }

        for (TruckSL truck : trucks) {
            System.out.println(truck);
            System.out.println("--------------------");
        }
    }
}