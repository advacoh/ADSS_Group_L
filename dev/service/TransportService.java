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
            int id,
            LocalDate date,
            LocalTime departureTime,
            double recordedWeight,
            int sourceSiteId,
            String truckLicenseNumber,
            int driverId,
            List<DeliveryDocumentInput> documentInputs
    ) {
        Site source = findSiteById(sourceSiteId);
        Truck truck = findTruckByLicenseNumber(truckLicenseNumber);
        Driver driver = findDriverById(driverId);

        if (source == null || truck == null || driver == null) {
            return false;
        }

        List<DeliveryDocument> documents = new ArrayList<>();

        for (DeliveryDocumentInput input : documentInputs) {
            Site destination = findSiteById(input.destinationSiteId());

            if (destination == null || input.items() == null || input.items().isEmpty()) {
                return false;
            }

            documents.add(new DeliveryDocument(
                    input.documentId(),
                    destination,
                    input.items()
            ));
        }

        Delivery delivery = new Delivery(
                id,
                date,
                departureTime,
                recordedWeight,
                DeliveryStatus.READY,
                source,
                truck,
                driver,
                documents
        );

        return transportController.createDelivery(delivery);
    }

    public boolean createDelivery(Delivery delivery) {
        return transportController.createDelivery(delivery);
    }

    public void addTruck(Truck truck) {
        transportController.addTruck(truck);
    }

    public void addDriver(Driver driver) {
        transportController.addDriver(driver);
    }

    public void addSite(Site site) {
        transportController.addSite(site);
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

}