package presentation.transport;

import presentation.InputUtil;
import service.TransportService;
import service.TruckSL;
import domain.transportation.Truck;
import enums.LicenseType;

import java.util.List;

public class TruckMenu {
    private final TransportService transportService;

    public TruckMenu(TransportService transportService) {
        this.transportService = transportService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Truck Menu ===");
            System.out.println("1) Add truck");
            System.out.println("2) View all trucks");
            System.out.println("3) View truck details");
            System.out.println("4) Back");

            switch (InputUtil.readInt("Choose option: ")) {
                case 1 -> addTruck();
                case 2 -> viewAllTrucks();
                case 3 -> viewTruckDetails();
                case 4 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewAllTrucks() {
        List<TruckSL> trucks = transportService.getAllTrucks();

        if (trucks.isEmpty()) {
            System.out.println("No trucks found.");
            return;
        }

        System.out.println("\n=== Trucks ===");
        for (int i = 0; i < trucks.size(); i++) {
            System.out.println((i + 1) + ") " + trucks.get(i).shortString());
        }
    }

    private void viewTruckDetails() {
        List<TruckSL> trucks = transportService.getAllTrucks();

        if (trucks.isEmpty()) {
            System.out.println("No trucks found.");
            return;
        }

        TruckSL selected = InputUtil.selectItem(trucks);
        if (selected == null) return;

        System.out.println("\n--- Truck Details ---");
        System.out.println(selected);
    }
    private void addTruck() {
        System.out.println("\n--- Add Truck ---");

        String licenseNumber = InputUtil.readString("Enter license number: ");
        String model = InputUtil.readString("Enter model: ");
        double netWeight = InputUtil.readDouble("Enter net weight: ");
        double maxCapacityWeight = InputUtil.readDouble("Enter max capacity weight: ");
        LicenseType requiredLicenseType = InputUtil.readLicenseType();

        boolean isAdded = transportService.addTruck(licenseNumber, model, netWeight, maxCapacityWeight, requiredLicenseType);
        
        if (isAdded) {
            System.out.println("Truck added successfully!");
        } else {
            System.out.println("Error: A truck with this license number already exists.");
        }
    }
}