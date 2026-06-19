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
            System.out.println("2) Update delivery"); // חדש!
            System.out.println("3) View all deliveries");
            System.out.println("4) View delivery details");
            System.out.println("5) Execute delivery");
            System.out.println("6) Resolve PENDING deliveries");
            System.out.println("7) Back");

            switch (InputUtil.readInt("Choose option: ")) {
                case 1 -> createDelivery();
                case 2 -> updateDelivery();
                case 3 -> viewAllDeliveries();
                case 4 -> viewDeliveryDetails();
                case 5 -> executeDelivery();
                case 6 -> resolvePendingDeliveries();
                case 7 -> { return; }
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

        DriverSL driver = selectAvailableDriver(date, departureTime, truck);
        if (driver == null) return;

        double recordedWeight = InputUtil.readDouble("Enter recorded truck weight: ");

        List<domain.transportation.TransportController.DocInput> documents = createDocumentInputs();
        if (documents.isEmpty()) {
            System.out.println("Delivery must contain at least one delivery document.");
            return;
        }

        boolean success = transportService.createDelivery(
                id, date, departureTime, recordedWeight,
                source.getId(), truck.getLicenseNumber(), driver.getId(), documents
        );

        if (success) {
            System.out.println("Delivery created successfully!");
        } else {
            System.out.println("Delivery creation failed. See error messages above.");
        }
    }

    private DriverSL selectAvailableDriver(LocalDate date, LocalTime time, TruckSL truck) {
        List<DriverSL> availableDrivers = transportService.getAvailableDrivers(date, time, truck.getLicenseNumber());

        if (availableDrivers.isEmpty()) {
            System.out.println("No available drivers found for this date, time, and truck (Filtered by HR Shifts & License).");
            return null;
        }

        System.out.println("\nSelect driver (Showing ONLY available drivers with matching license):");
        return InputUtil.selectItem(availableDrivers);
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

    private List<domain.transportation.TransportController.DocInput> createDocumentInputs() {
        List<domain.transportation.TransportController.DocInput> documents = new ArrayList<>();

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

            documents.add(new domain.transportation.TransportController.DocInput(
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

    private void executeDelivery() {
        System.out.println("\n--- Execute Delivery ---");
        
        List<DeliverySL> deliveries = transportService.getAllDeliveries();
        List<DeliverySL> executableDeliveries = new ArrayList<>();
        
        for (DeliverySL d : deliveries) {
            if (d.getStatus() == enums.DeliveryStatus.READY || d.getStatus() == enums.DeliveryStatus.EXECUTING) {
                executableDeliveries.add(d);
            }
        }

        if (executableDeliveries.isEmpty()) {
            System.out.println("No deliveries are currently available to execute or resume.");
            return;
        }

        System.out.println("Select a delivery to begin/resume execution:");
        DeliverySL selectedDelivery = InputUtil.selectItem(executableDeliveries);
        if (selectedDelivery == null) return;

        if (selectedDelivery.getStatus() == enums.DeliveryStatus.READY) {
            transportService.updateDeliveryStatus(selectedDelivery.getId(), enums.DeliveryStatus.EXECUTING);
            System.out.println("\nStarting fresh execution for Delivery ID: " + selectedDelivery.getId());
        } else {
            System.out.println("\nResuming execution for Delivery ID: " + selectedDelivery.getId());
        }
        
        System.out.println("Source Origin: " + selectedDelivery.getSource().getName());

        boolean ongoing = true;
        
        while (ongoing) {
            int currentStep = transportService.getDeliveryCurrentStep(selectedDelivery.getId());
            
            String nextSiteName = transportService.getNextDestinationName(selectedDelivery.getId(), currentStep);
            if (nextSiteName == null) {
                transportService.completeDelivery(selectedDelivery.getId());
                System.out.println("\nAll checkpoints visited. Delivery successfully completed!");
                ongoing = false;
                break;
            }

            System.out.println("\n--- Next Stop: " + nextSiteName + " ---");
            System.out.println("1) Record site arrival & weight check");
            System.out.println("2) Pause delivery here (Save progress and return to menu)");
        
            int action = -1;
            while (action != 1 && action != 2) {
                action = InputUtil.readInt("Choose option: ");
                if (action != 1 && action != 2) {
                    System.out.println("Invalid option. Please choose 1 or 2.");
                }
            }
            
            if (action == 2) {
                System.out.println("Delivery paused. Progress saved at step " + currentStep + ".");
                ongoing = false;
                break; 
            }

            double newWeight = InputUtil.readDouble("Enter the current truck weight recorded at this site: ");

            boolean weightOk = transportService.processDeliveryStop(selectedDelivery.getId(), currentStep, newWeight);
            if (!weightOk) {
                System.out.println("\nError: The truck is OVERWEIGHT for its maximum legal capacity.");
                System.out.println("1) Reduce supply and re-weigh.");
                System.out.println("2) Replace current destination with a new destination.");
                System.out.println("3) Replace current truck with a new truck.");
                
                int emergencyChoice = InputUtil.readInt("Choose option: ");
                switch (emergencyChoice) {
                    case 1 -> System.out.println("Please unload items or fix the load balance.");
                    case 2 -> {
                        List<SiteSL> allSites = transportService.getAllSites();
                        List<SiteSL> validReplacements = allSites.stream()
                                .filter(site -> !site.getName().equals(nextSiteName))
                                .toList();

                        if (validReplacements.isEmpty()) {
                            System.out.println("No alternative replacement sites available in the system.");
                        } else {
                            System.out.println("\nSelect a replacement destination site:");
                            SiteSL replacementSite = InputUtil.selectItem(validReplacements);
                            if (replacementSite != null) {
                                transportService.changeDocumentDestination(selectedDelivery.getId(), currentStep, replacementSite.getId());
                                System.out.println("Destination updated successfully. Proceeding to " + replacementSite.getName());
                            }
                        }
                    }
                    case 3 -> {
                        List<TruckSL> allTrucks = transportService.getAllTrucks();
    
                        DeliverySL currentDeliveryState = transportService.getAllDeliveries().stream()
                                .filter(d -> d.getId() == selectedDelivery.getId())
                                .findFirst()
                                .get();
                                
                        DriverSL activeDriver = currentDeliveryState.getDriver();
                        List<TruckSL> alternativeTrucks = allTrucks.stream()
                                .filter(truck -> !truck.getLicenseNumber().equals(currentDeliveryState.getTruck().getLicenseNumber()))
                                .filter(truck -> {
                                    enums.LicenseType driverLicense = activeDriver.getLicenseType(); 
                                    if (driverLicense == enums.LicenseType.C) 
                                        return true; 
                                    if (driverLicense == enums.LicenseType.C1) 
                                        return truck.getRequiredLicenseType() != enums.LicenseType.C; 
                                    return truck.getRequiredLicenseType() == enums.LicenseType.B;
                                })
                                .toList();

                        if (alternativeTrucks.isEmpty()) {
                            System.out.println("No alternative trucks available that match this driver's license (" + activeDriver.getLicenseType() + ").");
                            continue; 
                        }

                        System.out.println("\nSelect a replacement truck (Showing ONLY compatible trucks):");
                        TruckSL replacementTruck = InputUtil.selectItem(alternativeTrucks);
                        
                        if (replacementTruck != null) {
                            boolean truckSwapped = transportService.changeDeliveryTruck(selectedDelivery.getId(), replacementTruck.getLicenseNumber());
                            if (truckSwapped) {
                                System.out.println("Truck swapped successfully to license: " + replacementTruck.getLicenseNumber());
                            } else {
                                System.out.println("Failed to swap truck.");
                            }
                        }
                    }
                    default -> System.out.println("Staying at checkpoint.");
                }
            } else {
                System.out.println("Weight verified. Items handled successfully.");
                transportService.incrementDeliveryStep(selectedDelivery.getId()); 
            }
        }
    }

    private void resolvePendingDeliveries() {
        System.out.println("\n--- Resolve PENDING Deliveries ---");
        List<DeliverySL> pendingDeliveries = transportService.getAllDeliveries().stream()
                .filter(d -> d.getStatus() == enums.DeliveryStatus.PENDING)
                .toList();

        if (pendingDeliveries.isEmpty()) {
            System.out.println("No pending deliveries at the moment.");
            return;
        }

        for (DeliverySL d : pendingDeliveries) {
            System.out.println("Delivery ID: " + d.getId() + " | Status: PENDING");
        }

        int id = InputUtil.readInt("Enter Delivery ID to attempt resolution (or 0 to cancel): ");
        if (id == 0) return;

        boolean success = transportService.resolvePendingDelivery(id);
        if (success) {
            System.out.println("Success! Missing staff assigned. Delivery is now READY.");
        } else {
            System.out.println("Still missing staff! Contact HR to assign the missing roles.");
        }
    }

    private void updateDelivery() {
        System.out.println("\n--- Update Delivery ---");
        List<DeliverySL> deliveries = transportService.getAllDeliveries();
        if (deliveries.isEmpty()) {
            System.out.println("No deliveries to update.");
            return;
        }

        System.out.println("Select a delivery to update:");
        DeliverySL selected = InputUtil.selectItem(deliveries);
        if (selected == null) return;

        if (selected.getStatus() == enums.DeliveryStatus.EXECUTING || selected.getStatus() == enums.DeliveryStatus.COMPLETED) {
            System.out.println("Cannot update a delivery that is already executing or completed.");
            return;
        }

        System.out.println("\nEnter new details for Delivery ID " + selected.getId() + ":");
        
        System.out.println("Enter new delivery date:");
        LocalDate date = InputUtil.readDate();
        if (date == null) return;

        LocalTime departureTime = InputUtil.readTime();

        SiteSL source = selectSourceSite();
        if (source == null) return;

        TruckSL truck = selectTruck();
        if (truck == null) return;

        DriverSL driver = selectAvailableDriver(date, departureTime, truck);
        if (driver == null) return;

        double recordedWeight = InputUtil.readDouble("Enter new recorded truck weight: ");

        System.out.println("\n--- Rebuild Documents & Destinations ---");
        List<domain.transportation.TransportController.DocInput> documents = createDocumentInputs();
        if (documents.isEmpty()) {
            System.out.println("Delivery must contain at least one document.");
            return;
        }

        boolean success = transportService.updateDelivery(
                selected.getId(), date, departureTime, recordedWeight,
                source.getId(), truck.getLicenseNumber(), driver.getId(), documents
        );

        if (success) {
            System.out.println("Delivery updated successfully!");
        } else {
            System.out.println("Delivery update failed. See error messages above.");
        }
    }
}







