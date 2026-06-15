package presentation.transport;

import domain.transportation.TransportedItem;
import presentation.InputUtil;
import service.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DeliveryMenu {
    private final TransportService transportService;

    public DeliveryMenu(TransportService transportService) {
        this.transportService = transportService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Delivery Menu ===");
            System.out.println("1) Create delivery");
            System.out.println("2) View all deliveries");
            System.out.println("3) View delivery details");
            System.out.println("4) Back");

            switch (InputUtil.readInt("Choose option: ")) {
                case 1 -> createDelivery();
                case 2 -> viewAllDeliveries();
                case 3 -> viewDeliveryDetails();
                case 4 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createDelivery() {
        System.out.println("\n--- Create Delivery ---");

        int id = InputUtil.readInt("Enter delivery ID: ");

        System.out.println("Enter delivery date:");
        LocalDate date = InputUtil.readDate();
        if (date == null) return;

        LocalTime departureTime = InputUtil.readTime();

        SiteSL source = selectSourceSite();
        if (source == null) return;

        TruckSL truck = selectTruck();
        if (truck == null) return;

        DriverSL driver = selectDriver();
        if (driver == null) return;

        double recordedWeight = InputUtil.readDouble("Enter recorded truck weight: ");

        List<TransportService.DeliveryDocumentInput> documents = createDocumentInputs();
        if (documents.isEmpty()) {
            System.out.println("Delivery must contain at least one delivery document.");
            return;
        }

        boolean success = transportService.createDelivery(
                id,
                date,
                departureTime,
                recordedWeight,
                source.getId(),
                truck.getLicenseNumber(),
                driver.getId(),
                documents
        );

        if (success) {
            System.out.println("Delivery created successfully!");
        } else {
            System.out.println("Delivery creation failed.");
            System.out.println("Possible reasons: incompatible driver license, overweight truck, invalid documents, or missing shift requirements.");
        }
    }

    private SiteSL selectSourceSite() {
        List<SiteSL> sites = transportService.getAllSites();

        if (sites.isEmpty()) {
            System.out.println("No sites found. Add sites before creating a delivery.");
            return null;
        }

        System.out.println("\nSelect source site:");
        return InputUtil.selectItem(sites);
    }

    private TruckSL selectTruck() {
        List<TruckSL> trucks = transportService.getAllTrucks();

        if (trucks.isEmpty()) {
            System.out.println("No trucks found. Add trucks before creating a delivery.");
            return null;
        }

        System.out.println("\nSelect truck:");
        return InputUtil.selectItem(trucks);
    }

    private DriverSL selectDriver() {
        List<DriverSL> drivers = transportService.getAllDrivers();

        if (drivers.isEmpty()) {
            System.out.println("No drivers found. Drivers should be managed through HR.");
            return null;
        }

        System.out.println("\nSelect driver:");
        return InputUtil.selectItem(drivers);
    }

    private List<TransportService.DeliveryDocumentInput> createDocumentInputs() {
        List<TransportService.DeliveryDocumentInput> documents = new ArrayList<>();

        while (true) {
            System.out.println("\n--- Create Delivery Document ---");

            int documentId = InputUtil.readInt("Enter document ID: ");

            SiteSL destination = selectDestinationSite();
            if (destination == null) break;

            List<TransportedItem> items = createTransportedItems();
            if (items.isEmpty()) {
                System.out.println("Document must contain at least one item.");
                continue;
            }

            documents.add(new TransportService.DeliveryDocumentInput(
                    documentId,
                    destination.getId(),
                    items
            ));

            if (!InputUtil.readYesNo("Add another destination/document?")) {
                break;
            }
        }

        return documents;
    }

    private SiteSL selectDestinationSite() {
        List<SiteSL> sites = transportService.getAllSites();

        if (sites.isEmpty()) {
            System.out.println("No destination sites found.");
            return null;
        }

        System.out.println("\nSelect destination site:");
        return InputUtil.selectItem(sites);
    }

    private List<TransportedItem> createTransportedItems() {
        List<TransportedItem> items = new ArrayList<>();

        while (true) {
            System.out.println("\n--- Add Transported Item ---");

            int itemId = InputUtil.readInt("Enter item ID: ");
            String itemName = InputUtil.readString("Enter item name: ");
            int quantity = InputUtil.readInt("Enter quantity: ");

            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                continue;
            }

            items.add(new TransportedItem(itemId, itemName, quantity));

            if (!InputUtil.readYesNo("Add another item?")) {
                break;
            }
        }

        return items;
    }

    private void viewAllDeliveries() {
        List<DeliverySL> deliveries = transportService.getAllDeliveries();

        if (deliveries.isEmpty()) {
            System.out.println("No deliveries found.");
            return;
        }

        System.out.println("\n=== Deliveries ===");
        for (int i = 0; i < deliveries.size(); i++) {
            System.out.println((i + 1) + ") " + deliveries.get(i).shortString());
        }
    }

    private void viewDeliveryDetails() {
        List<DeliverySL> deliveries = transportService.getAllDeliveries();

        if (deliveries.isEmpty()) {
            System.out.println("No deliveries found.");
            return;
        }

        System.out.println("\nSelect delivery:");
        DeliverySL selected = InputUtil.selectItem(deliveries);
        if (selected == null) return;

        System.out.println("\n--- Delivery Details ---");
        System.out.println(selected);
    }
}