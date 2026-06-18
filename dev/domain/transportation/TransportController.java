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

public class TransportController {

    private DeliveryRepository deliveryRepository;
    private TruckRepository truckRepository;
    private DriverRepository driverRepository;
    private SiteRepository siteRepository;
    private ShiftController shiftController;

    public TransportController(ShiftController shiftController) {
        this.deliveryRepository = new DeliveryRepository();
        this.truckRepository = new TruckRepository();
        this.driverRepository = new DriverRepository();
        this.siteRepository = new SiteRepository();
        this.shiftController = shiftController;    }



    public void addDriver(Driver driver) {
        driverRepository.addDriver(driver);
    }

    public boolean addTruck(Truck truck) {
        for (Truck t : getAllTrucks()) {
            if (t.getLicenseNumber().equals(truck.getLicenseNumber())) {
                return false; //Truck with the same license number already exists
            }
        }
        truckRepository.addTruck(truck);
        return true;
    }

    public boolean addSite(Site site) {
        if (siteExists(site.getId())) {
            return false; //Site with the same ID already exists
        }
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

    public boolean createDelivery(Delivery delivery) {
        // checking for positive recorded weight
        if (delivery.getRecordedWeight() <= 0) {
            System.out.println("Error: Recorded weight must be greater than zero.");
            return false;
        }

        for (Delivery d : getAllDeliveries()) {
            // checking for duplicate delivery ID
            if (d.getId() == delivery.getId()) {
                System.out.println("Error: Delivery ID already exists.");
                return false;
            }

            // checking that driver or truck are not assigned to another delivery on the same date
            if (d.getDate().equals(delivery.getDate())) {
                if (d.getDriver().getId() == delivery.getDriver().getId()) {
                    System.out.println("Error: The selected driver is already assigned to another delivery on this date.");
                    return false;
                }
                if (d.getTruck().getLicenseNumber().equals(delivery.getTruck().getLicenseNumber())) {
                    System.out.println("Error: The selected truck is already assigned to another delivery on this date.");
                    return false;
                }
            }
        }

        // checking for duplicate document IDs and destination IDs within the same delivery
        Set<Integer> documentIds = new HashSet<>();
        Set<Integer> destinationIds = new HashSet<>();
        
        if (delivery.getDocuments() != null) {
            for (DeliveryDocument doc : delivery.getDocuments()) {
                // checking for duplicate document IDs
                if (!documentIds.add(doc.getDocumentId())) { 
                    System.out.println("Error: Duplicate Document ID found in this delivery.");
                    return false;
                }
                
                // checking that destination site is not the same as the source site
                if (doc.getDestination().getId() == delivery.getSource().getId()) {
                    System.out.println("Error: Destination site (" + doc.getDestination().getName() + ") cannot be the same as the Source site.");
                    return false;
                }
                
                // checking for duplicate destination IDs
                if (!destinationIds.add(doc.getDestination().getId())) {
                    System.out.println("Error: Duplicate destination found. A delivery cannot have multiple documents for the same destination (" + doc.getDestination().getName() + ").");
                    return false;
                }
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

        if (!hasDocuments(delivery)) {
            System.out.println("Error: Delivery must have at least one document.");
            return false;
        }

        if (!areAllDocumentsValid(delivery)) {
            return false;
        }
       
        try {
            shiftController.verifyDelivery(delivery.getBranches(), delivery.getDate(), delivery.getDepartureTime(), delivery.getDriver().getId());
            delivery.setStatus(DeliveryStatus.READY);
            delivery.setPendingReason(null);
        } catch (IllegalStateException e) { 
            delivery.setStatus(DeliveryStatus.PENDING);
            delivery.setPendingReason(e.getMessage());
            System.out.println("Notice: Delivery created but marked as PENDING. Reason: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to create delivery due to system error: " + e.getMessage());
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
        return true;
    }

    public void updateDeliveryStatus(int deliveryId, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.getDeliveryById(deliveryId);
        if (delivery != null) {
            delivery.setStatus(status);
        }
    }

    public void changeDocumentDestination(int deliveryId, int step, int newSiteId) {
        Delivery delivery = deliveryRepository.getDeliveryById(deliveryId);
        Site newSite = siteRepository.getSiteById(newSiteId); 
        if (delivery != null && newSite != null) {
            delivery.getDocuments().get(step).setDestination(newSite);
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
                return true;
            }
        }
        return false;
    }
}