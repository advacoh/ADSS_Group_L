package domain.transportation;

import enums.DeliveryStatus;
import java.util.List;
import repository.DeliveryRepository;
import repository.DriverRepository;
import repository.SiteRepository;
import repository.TruckRepository;
import domain.hr.ShiftController;

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

    public void addTruck(Truck truck) {
        truckRepository.addTruck(truck);
    }

    public void addDriver(Driver driver) {
        driverRepository.addDriver(driver);
    }

    public void addSite(Site site) {
        siteRepository.addSite(site);
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
        return driver.getLicenseType() == truck.getRequiredLicenseType();
    }

    public boolean isOverweight(double recordedWeight, Truck truck) {
        return recordedWeight > truck.getMaxCapacityWeight();
    }
    public boolean hasDocuments(Delivery delivery) {
        return delivery.getDocuments() != null && !delivery.getDocuments().isEmpty();
    }
    public boolean isValidDocument(DeliveryDocument document) {
        return document.getItems() != null && !document.getItems().isEmpty();
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
        if (!isDriverCompatibleWithTruck(delivery.getDriver(), delivery.getTruck())) {
            return false;
        }

        if (isOverweight(delivery.getRecordedWeight(), delivery.getTruck())) {
            delivery.setStatus(DeliveryStatus.OVERWEIGHT);
            return false;
        }

        if (!hasDocuments(delivery)) {
            return false;
        }

        if (!areAllDocumentsValid(delivery)) {
            return false;
        }
        
        try{
            shiftController.verifyDelivery(delivery.getBranches(),delivery.getDate(), delivery.getDepartureTime(), delivery.getDriver().getId());
        } catch (Exception e) {
            System.out.println("Failed to create delivery: " + e.getMessage());
            return false;        
        }

        delivery.setStatus(DeliveryStatus.READY);
        deliveryRepository.addDelivery(delivery);   
        return true;
    }

    public boolean siteExists(int siteId) {
        return siteRepository.siteExists(siteId);
    }
}