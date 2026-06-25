package domain.transportation;

import enums.DeliveryStatus;
import java.util.List;
import repository.DeliveryRepository;
import repository.DriverRepository;
import repository.SiteRepository;
import repository.TruckRepository;
import domain.hr.ShiftController;
import enums.LicenseType;
import java.util.HashSet;
import java.util.Set;
import domain.transportation.DeliveryZone;
import enums.SiteType;
import domain.transportation.DeliveryDocument;
import domain.transportation.TransportedItem;
import java.time.LocalDate;
import java.time.LocalTime;

public class TransportController {

    private DeliveryRepository deliveryRepository;
    private TruckRepository truckRepository;
    private DriverRepository driverRepository;
    private SiteRepository siteRepository;
    private ShiftController shiftController;

    public record DocInput(int documentId, int destinationSiteId, List<TransportedItem> items) {}

    public TransportController(ShiftController shiftController, DriverRepository driverRepository) {
        this.truckRepository = new TruckRepository();
        this.driverRepository = driverRepository;
        this.siteRepository = new SiteRepository();
        this.deliveryRepository = new DeliveryRepository(driverRepository, truckRepository, siteRepository);
        this.shiftController = shiftController; 
    }

    public TransportController(ShiftController shiftController, DriverRepository driverRepository, String connectionString) {
        this.truckRepository = new TruckRepository(connectionString);
        this.siteRepository = new SiteRepository(connectionString);
        this.deliveryRepository = new DeliveryRepository(driverRepository, this.truckRepository, this.siteRepository, connectionString);
        
        this.driverRepository = driverRepository;
        this.shiftController = shiftController; 
    }


    public void addDriver(Driver driver) {
        driverRepository.addDriver(driver);
    }

    public boolean addTruck(String licenseNumber, String model, double netWeight, double maxCapacityWeight, LicenseType requiredLicenseType) {
        for (Truck t : getAllTrucks()) {
            if (t.getLicenseNumber().equals(licenseNumber)) {
                return false; 
            }
        }
        Truck truck = new Truck(licenseNumber, model, netWeight, maxCapacityWeight, requiredLicenseType);
        truckRepository.addTruck(truck);
        return true;
    }

    public boolean addSite(int id, String name, String address, String phoneNumber, String contactPerson, SiteType siteType, int zoneId, String zoneName) {
        if (siteExists(id)) {
            return false; 
        }
        DeliveryZone zone = new DeliveryZone(zoneId, zoneName);
        Site site = new Site(id, name, address, phoneNumber, contactPerson, siteType, zone);
        siteRepository.addSite(site);
        return true;
    }

    public List<Truck> getAllTrucks() {
        return truckRepository.getAllTrucks();
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.getAllDrivers();
    }

    public List<Site> getAllSites() {
        return siteRepository.getAllSites();
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.getAllDeliveries();
    }

    public boolean isDriverCompatibleWithTruck(Driver driver, Truck truck) {
        LicenseType driverLicense = driver.getLicenseType();
        LicenseType requiredLicense = truck.getRequiredLicenseType();

        // C1 compatible with B, C, C1
        if (driverLicense == LicenseType.C1) {
            return requiredLicense == LicenseType.B || requiredLicense == LicenseType.C || requiredLicense == LicenseType.C1;
        } 
        // C compatible with B, C
        else if (driverLicense == LicenseType.C) {
            return requiredLicense == LicenseType.B || requiredLicense == LicenseType.C;
        } 
        // B compatible only with B
        else if (driverLicense == LicenseType.B) {
            return requiredLicense == LicenseType.B;
        }
        
        return false;
    }

    public boolean isOverweight(double recordedWeight, Truck truck) {
        return recordedWeight > truck.getMaxCapacityWeight();
    }
    public boolean hasDocuments(Delivery delivery) {
        return delivery.getDocuments() != null && !delivery.getDocuments().isEmpty();
    }

    public boolean isValidDocument(DeliveryDocument document) {
        if (document.getItems() == null || document.getItems().isEmpty()) {
            System.out.println("Error: Document " + document.getDocumentId() + " has no items.");
            return false;
        }
        
        // checking for positive quantity for each item in the document
        for (TransportedItem item : document.getItems()) {
            if (item.getQuantity() <= 0) {
                System.out.println("Error: Item '" + item.getItemName() + "' has invalid quantity (" + item.getQuantity() + "). Quantity must be positive.");
                return false;
            }
        }
        return true;
    }
    
    public boolean areAllDocumentsValid(Delivery delivery) {
        for (DeliveryDocument document : delivery.getDocuments()) {
            if (!isValidDocument(document)) {
                return false;
            }
        }
        return true;
    }

    public boolean createDelivery(int id, LocalDate date, LocalTime departureTime, double recordedWeight,
                                  int sourceSiteId, String truckLicenseNumber, int driverId,
                                  List<DocInput> documentInputs) {
                                      
        Site source = getAllSites().stream().filter(s -> s.getId() == sourceSiteId).findFirst().orElse(null);
        Truck truck = getAllTrucks().stream().filter(t -> t.getLicenseNumber().equals(truckLicenseNumber)).findFirst().orElse(null);
        Driver driver = getAllDrivers().stream().filter(d -> d.getId() == driverId).findFirst().orElse(null);

        if (source == null || truck == null || driver == null) {
            System.out.println("Error: Source site, truck, or driver not found.");
            return false;
        }

        List<DeliveryDocument> documents = new java.util.ArrayList<>();
        for (DocInput input : documentInputs) {
            Site destination = getAllSites().stream().filter(s -> s.getId() == input.destinationSiteId).findFirst().orElse(null);
            if (destination == null || input.items == null || input.items.isEmpty()) {
                System.out.println("Error: Invalid destination site or empty items list for document " + input.documentId);
                return false;
            }
            documents.add(new DeliveryDocument(input.documentId, destination, input.items));
        }

        Delivery delivery = new Delivery(id, date, departureTime, recordedWeight, DeliveryStatus.PLANNED, source, truck, driver, documents);

        if (delivery.getRecordedWeight() <= 0) {
            System.out.println("Error: Recorded weight must be greater than zero.");
            return false;
        }

        for (Delivery d : getAllDeliveries()) {
            if (d.getId() == delivery.getId()) {
                System.out.println("Error: Delivery ID already exists.");
                return false;
            }
            if (d.getDate().equals(delivery.getDate())) {
                if (d.getDriver().getId() == delivery.getDriver().getId()) {
                    System.out.println("Error: The driver is already assigned to another delivery on this date.");
                    return false;
                }
                if (d.getTruck().getLicenseNumber().equals(delivery.getTruck().getLicenseNumber())) {
                    System.out.println("Error: The truck is already assigned to another delivery on this date.");
                    return false;
                }
            }
        }

        Set<Integer> documentIds = new HashSet<>();
        Set<Integer> destinationIds = new HashSet<>();
        for (DeliveryDocument doc : delivery.getDocuments()) {
            if (!documentIds.add(doc.getDocumentId())) { 
                System.out.println("Error: Duplicate Document ID found.");
                return false;
            }
            if (isDocumentIdExists(doc.getDocumentId(), delivery.getId())) {
                System.out.println("Error: Document ID " + doc.getDocumentId() + " already exists in the system.");
                return false;
            }
            if (doc.getDestination().getId() == delivery.getSource().getId()) {
                System.out.println("Error: Destination site cannot be the same as the Source site.");
                return false;
            }
            if (!destinationIds.add(doc.getDestination().getId())) {
                System.out.println("Error: Duplicate destination found.");
                return false;
            }
        }

        if (!isDriverCompatibleWithTruck(delivery.getDriver(), delivery.getTruck())) {
            System.out.println("Error: Driver license is incompatible with the selected truck.");
            return false;
        }

        if (isOverweight(delivery.getRecordedWeight(), delivery.getTruck())) {
            delivery.setStatus(DeliveryStatus.OVERWEIGHT);
            System.out.println("Error: The recorded weight exceeds the truck's maximum capacity.");
            return false;
        }

        if (!areAllDocumentsValid(delivery)) {
            return false;
        }
       
        try {
            shiftController.verifyDelivery(delivery.getBranches(), delivery.getDate(), delivery.getDepartureTime(), delivery.getDriver().getId());
            delivery.setStatus(DeliveryStatus.READY);
        } catch (IllegalStateException e) { 
            delivery.setStatus(DeliveryStatus.PENDING);
            delivery.setPendingReason(e.getMessage());
            System.out.println("Notice: Delivery created but marked as PENDING. Reason: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to create delivery: " + e.getMessage());
            return false;
        }

        deliveryRepository.addDelivery(delivery);
        return true;
    }

    public boolean resolvePendingDelivery(int deliveryId) {
        Delivery delivery = deliveryRepository.getDeliveryById(deliveryId);
        if (delivery == null || delivery.getStatus() != DeliveryStatus.PENDING) {
            return false;
        }

        try {
            // trying to resolve the pending issue
            shiftController.verifyDelivery(delivery.getBranches(), delivery.getDate(), delivery.getDepartureTime(), delivery.getDriver().getId());
            delivery.setStatus(DeliveryStatus.READY);
            delivery.setPendingReason(null);
            deliveryRepository.updateDelivery(delivery);
            return true;
        } catch (IllegalStateException e) {
            // still cannot resolve the pending issue
            delivery.setPendingReason(e.getMessage());
            return false;
        }
    }


    public boolean siteExists(int siteId) {
        return siteRepository.siteExists(siteId);
    }

    public String getNextDestinationName(int deliveryId, int step) {
        Delivery delivery = deliveryRepository.getDeliveryById(deliveryId); 
        if (delivery == null || delivery.getDocuments() == null || step >= delivery.getDocuments().size()) {
            return null;
        }
        return delivery.getDocuments().get(step).getDestination().getName();
    }

    public boolean processDeliveryStop(int deliveryId, int step, double newWeight) {
        Delivery delivery = deliveryRepository.getDeliveryById(deliveryId);
        if (delivery == null) return false;

        if (isOverweight(newWeight, delivery.getTruck())) {
            return false; 
        }

        if (step == 0) {
            delivery.setStatus(DeliveryStatus.EXECUTING); 
        }
        
        delivery.setRecordedWeight(newWeight); 
        deliveryRepository.updateDelivery(delivery);
        return true;
    }

    public void updateDeliveryStatus(int deliveryId, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.getDeliveryById(deliveryId);
        if (delivery != null) {
            delivery.setStatus(status);
            deliveryRepository.updateDelivery(delivery);
        }
    }

    public void changeDocumentDestination(int deliveryId, int step, int newSiteId) {
        Delivery delivery = deliveryRepository.getDeliveryById(deliveryId);
        Site newSite = siteRepository.getSiteById(newSiteId); 
        if (delivery != null && newSite != null) {
            delivery.getDocuments().get(step).setDestination(newSite);
            deliveryRepository.updateDelivery(delivery);
        }
    }

    public boolean changeDeliveryTruck(int deliveryId, String newLicenseNumber) {
        Delivery delivery = deliveryRepository.getDeliveryById(deliveryId);
        Truck newTruck = null;
        for (Truck t : getAllTrucks()) {
            if (t.getLicenseNumber().equals(newLicenseNumber)) {
                newTruck = t;
                break;
            }
        }

        if (delivery != null && newTruck != null) {
            if (isDriverCompatibleWithTruck(delivery.getDriver(), newTruck)) {
                delivery.setTruck(newTruck); 
                deliveryRepository.updateDelivery(delivery);
                return true;
            }
        }
        return false;
    }

    // function to get only available drivers
    public List<Driver> getAvailableDrivers(LocalDate date, LocalTime time, String truckLicenseNumber) {
        List<Driver> availableDrivers = new java.util.ArrayList<>();
        Truck truck = getAllTrucks().stream()
                .filter(t -> t.getLicenseNumber().equals(truckLicenseNumber))
                .findFirst().orElse(null);

        if (truck == null) return availableDrivers;

        for (Driver driver : getAllDrivers()) {
            if (!isDriverCompatibleWithTruck(driver, truck)) continue;
            boolean hasDeliveryToday = getAllDeliveries().stream()
                    .anyMatch(d -> d.getDate().equals(date) && d.getDriver().getId() == driver.getId());
            if (hasDeliveryToday) continue;

            try {
                shiftController.verifyDelivery(new java.util.ArrayList<>(), date, time, driver.getId());
                availableDrivers.add(driver); // הנהג עבר את כל הסינונים!
            } catch (IllegalStateException e) {}
        }
        return availableDrivers;
    }

    // function to update an existing delivery
    public boolean updateDelivery(int id, LocalDate newDate, LocalTime newTime, double newWeight,
                                  int sourceSiteId, String truckLicenseNumber, int driverId,
                                  List<DocInput> documentInputs) {
        
        Delivery existing = getAllDeliveries().stream().filter(d -> d.getId() == id).findFirst().orElse(null);
        if (existing == null) {
            System.out.println("Error: Delivery not found.");
            return false;
        }

        if (existing.getStatus() == DeliveryStatus.EXECUTING || existing.getStatus() == DeliveryStatus.COMPLETED) {
             System.out.println("Error: Cannot update a delivery that is already executing or completed.");
             return false;
        }

        Site source = getAllSites().stream().filter(s -> s.getId() == sourceSiteId).findFirst().orElse(null);
        Truck truck = getAllTrucks().stream().filter(t -> t.getLicenseNumber().equals(truckLicenseNumber)).findFirst().orElse(null);
        Driver driver = getAllDrivers().stream().filter(d -> d.getId() == driverId).findFirst().orElse(null);

        if (source == null || truck == null || driver == null) {
            System.out.println("Error: Source site, truck, or driver not found.");
            return false;
        }

        List<DeliveryDocument> documents = new java.util.ArrayList<>();
        for (DocInput input : documentInputs) {
            Site destination = getAllSites().stream().filter(s -> s.getId() == input.destinationSiteId).findFirst().orElse(null);
            if (destination == null || input.items == null || input.items.isEmpty()) return false;
            documents.add(new DeliveryDocument(input.documentId, destination, input.items));
        }

        Delivery tempDelivery = new Delivery(id, newDate, newTime, newWeight, DeliveryStatus.PLANNED, source, truck, driver, documents);

        Set<Integer> documentIds = new HashSet<>();
        Set<Integer> destinationIds = new HashSet<>();
        for (DeliveryDocument doc : tempDelivery.getDocuments()) {
            if (!documentIds.add(doc.getDocumentId())) { 
                System.out.println("Error: Duplicate Document ID found in input.");
                return false;
            }
            if (isDocumentIdExists(doc.getDocumentId(), tempDelivery.getId())) {
                System.out.println("Error: Document ID " + doc.getDocumentId() + " already exists in another delivery.");
                return false;
            }
            if (doc.getDestination().getId() == tempDelivery.getSource().getId()) {
                System.out.println("Error: Destination site cannot be the same as the Source site.");
                return false;
            }
            if (!destinationIds.add(doc.getDestination().getId())) {
                System.out.println("Error: Duplicate destination found.");
                return false;
            }
        }

        if (tempDelivery.getRecordedWeight() <= 0) return false;

        for (Delivery d : getAllDeliveries()) {
            if (d.getId() == id) continue;

            if (d.getDate().equals(tempDelivery.getDate())) {
                if (d.getDriver().getId() == tempDelivery.getDriver().getId()) {
                    System.out.println("Error: The driver is already assigned to another delivery on this date.");
                    return false;
                }
                if (d.getTruck().getLicenseNumber().equals(tempDelivery.getTruck().getLicenseNumber())) {
                    System.out.println("Error: The truck is already assigned to another delivery on this date.");
                    return false;
                }
            }
        }

        if (!isDriverCompatibleWithTruck(tempDelivery.getDriver(), tempDelivery.getTruck())) {
            System.out.println("Error: Driver license is incompatible with the selected truck.");
            return false;
        }

        if (isOverweight(tempDelivery.getRecordedWeight(), tempDelivery.getTruck())) {
            tempDelivery.setStatus(DeliveryStatus.OVERWEIGHT);
        }

        if (!areAllDocumentsValid(tempDelivery)) return false;

        try {
            shiftController.verifyDelivery(tempDelivery.getBranches(), tempDelivery.getDate(), tempDelivery.getDepartureTime(), tempDelivery.getDriver().getId());
            if(tempDelivery.getStatus() != DeliveryStatus.OVERWEIGHT) {
                tempDelivery.setStatus(DeliveryStatus.READY);
            }
            tempDelivery.setPendingReason(null);
        } catch (IllegalStateException e) { 
            tempDelivery.setStatus(DeliveryStatus.PENDING);
            tempDelivery.setPendingReason(e.getMessage());
            System.out.println("Notice: Update successful, but marked as PENDING. Reason: " + e.getMessage());
        }

        existing.setDate(newDate);
        existing.setDepartureTime(newTime);
        existing.setRecordedWeight(newWeight);
        existing.setSource(source);
        existing.setTruck(truck);
        existing.setDriver(driver);
        existing.setDocuments(documents);
        existing.setStatus(tempDelivery.getStatus());
        existing.setPendingReason(tempDelivery.getPendingReason());
        
        deliveryRepository.updateDelivery(existing);
        
        return true;
    }

    private boolean isDocumentIdExists(int documentId, int currentDeliveryId) {
        for (Delivery d : getAllDeliveries()) {
            if (d.getId() == currentDeliveryId) continue; // אם אנחנו בעדכון, נדלג על ההובלה הנוכחית
            for (DeliveryDocument doc : d.getDocuments()) {
                if (doc.getDocumentId() == documentId) {
                    return true; // ה-ID הזה כבר תפוס בהובלה אחרת!
                }
            }
        }
        return false;
    }
}