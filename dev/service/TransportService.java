package service;

import domain.transportation.*;
import enums.DeliveryStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransportService {

    private final TransportController transportController;

    public TransportService(TransportController transportController) {
        this.transportController = transportController;
    }

    public record DeliveryDocumentInput(
            int documentId,
            int destinationSiteId,
            List<TransportedItem> items
    ) {}

   public boolean createDelivery(
            int id, LocalDate date, LocalTime departureTime, double recordedWeight,
            int sourceSiteId, String truckLicenseNumber, int driverId,
            List<TransportController.DocInput> documentInputs
    ) {
        return transportController.createDelivery(id, date, departureTime, recordedWeight, sourceSiteId, truckLicenseNumber, driverId, documentInputs);
    }

    public boolean addTruck(String licenseNumber, String model, double netWeight, double maxCapacityWeight, enums.LicenseType requiredLicenseType) {
        return transportController.addTruck(licenseNumber, model, netWeight, maxCapacityWeight, requiredLicenseType);
    }

    public boolean addSite(int id, String name, String address, String phoneNumber, String contactPerson, enums.SiteType siteType, int zoneId, String zoneName) {
        return transportController.addSite(id, name, address, phoneNumber, contactPerson, siteType, zoneId, zoneName);
    }

    public void addDriver(Driver driver) {
        transportController.addDriver(driver);
    }

    public List<DeliverySL> getAllDeliveries() {
        return transportController.getAllDeliveries()
                .stream()
                .map(DeliverySL::new)
                .collect(Collectors.toList());
    }

    public List<TruckSL> getAllTrucks() {
        return transportController.getAllTrucks()
                .stream()
                .map(TruckSL::new)
                .collect(Collectors.toList());
    }

    public List<DriverSL> getAllDrivers() {
        return transportController.getAllDrivers()
                .stream()
                .map(DriverSL::new)
                .collect(Collectors.toList());
    }

    public List<SiteSL> getAllSites() {
        return transportController.getAllSites()
                .stream()
                .map(SiteSL::new)
                .collect(Collectors.toList());
    }

    public boolean isDriverCompatibleWithTruck(Driver driver, Truck truck) {
        return transportController.isDriverCompatibleWithTruck(driver, truck);
    }

    public boolean isOverweight(double recordedWeight, Truck truck) {
        return transportController.isOverweight(recordedWeight, truck);
    }

    public boolean hasDocuments(Delivery delivery) {
        return transportController.hasDocuments(delivery);
    }

    public boolean areAllDocumentsValid(Delivery delivery) {
        return transportController.areAllDocumentsValid(delivery);
    }

    private Site findSiteById(int siteId) {
        return transportController.getAllSites()
                .stream()
                .filter(site -> site.getId() == siteId)
                .findFirst()
                .orElse(null);
    }

    private Truck findTruckByLicenseNumber(String licenseNumber) {
        return transportController.getAllTrucks()
                .stream()
                .filter(truck -> truck.getLicenseNumber().equals(licenseNumber))
                .findFirst()
                .orElse(null);
    }

    private Driver findDriverById(int driverId) {
        return transportController.getAllDrivers()
                .stream()
                .filter(driver -> driver.getID() == driverId)
                .findFirst()
                .orElse(null);
    }

    public String getNextDestinationName(int deliveryId, int step) {
        return transportController.getNextDestinationName(deliveryId, step);
    }

    public boolean processDeliveryStop(int deliveryId, int step, double newWeight) {
        return transportController.processDeliveryStop(deliveryId, step, newWeight);
    }

    public void completeDelivery(int deliveryId) {
        transportController.updateDeliveryStatus(deliveryId, enums.DeliveryStatus.COMPLETED); 
    }

    public void abortDelivery(int deliveryId) {
        transportController.updateDeliveryStatus(deliveryId, enums.DeliveryStatus.OVERWEIGHT);
    }

    public void changeDocumentDestination(int deliveryId, int step, int newSiteId) {
        transportController.changeDocumentDestination(deliveryId, step, newSiteId);
    }

    public boolean changeDeliveryTruck(int deliveryId, String newLicenseNumber) {
        return transportController.changeDeliveryTruck(deliveryId, newLicenseNumber);
    }

    public int getDeliveryCurrentStep(int deliveryId) {
        domain.transportation.Delivery delivery = transportController.getAllDeliveries()
                .stream()
                .filter(d -> d.getId() == deliveryId)
                .findFirst()
                .orElse(null);
                
        return (delivery != null) ? delivery.getCurrentStep() : 0;
    }

    public void incrementDeliveryStep(int deliveryId) {
        domain.transportation.Delivery delivery = transportController.getAllDeliveries()
                .stream()
                .filter(d -> d.getId() == deliveryId)
                .findFirst()
                .orElse(null);
                
        if (delivery != null) {
            delivery.incrementStep();
        }
    }

    public void updateDeliveryStatus(int deliveryId, enums.DeliveryStatus status) {
        transportController.updateDeliveryStatus(deliveryId, status);
    }

    public boolean resolvePendingDelivery(int deliveryId) {
        return transportController.resolvePendingDelivery(deliveryId);
    }

    public List<DriverSL> getAvailableDrivers(LocalDate date, LocalTime time, String truckLicenseNumber) {
        return transportController.getAvailableDrivers(date, time, truckLicenseNumber).stream()
                .map(DriverSL::new)
                .collect(Collectors.toList());
    }

    public boolean updateDelivery(int id, LocalDate newDate, LocalTime newTime, double newWeight,
                                  int sourceSiteId, String truckLicenseNumber, int driverId,
                                  List<domain.transportation.TransportController.DocInput> documentInputs) {
        return transportController.updateDelivery(id, newDate, newTime, newWeight, sourceSiteId, truckLicenseNumber, driverId, documentInputs);
    }
}