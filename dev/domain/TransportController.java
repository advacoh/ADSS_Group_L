package domain;

import enums.DeliveryStatus;
import java.util.List;
import repository.DeliveryRepository;
import repository.DriverRepository;
import repository.SiteRepository;
import repository.TruckRepository;

public class TransportController {

    private DeliveryRepository deliveryRepository;
    private TruckRepository truckRepository;
    private DriverRepository driverRepository;
    private SiteRepository siteRepository;

    public TransportController() {
        this.deliveryRepository = new DeliveryRepository();
        this.truckRepository = new TruckRepository();
        this.driverRepository = new DriverRepository();
        this.siteRepository = new SiteRepository();
    }

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

    public boolean createDelivery(Delivery delivery) {
        if (!isDriverCompatibleWithTruck(delivery.getDriver(), delivery.getTruck())) {
            return false;
        }

        if (isOverweight(delivery.getRecordedWeight(), delivery.getTruck())) {
            delivery.setStatus(DeliveryStatus.OVERWEIGHT);
            return false;
        }
        delivery.setStatus(DeliveryStatus.READY);
        deliveryRepository.addDelivery(delivery);
        return true;
    }
}