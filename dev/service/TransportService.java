package service;

import domain.*;
import java.util.List;
import java.util.stream.Collectors;

public class TransportService {

    private final TransportController transportController;

    public TransportService(TransportController transportController) {
        this.transportController = transportController;
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
}