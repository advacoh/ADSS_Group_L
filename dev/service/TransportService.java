package service;

import domain.TransportController;
import domain.Delivery;
import domain.Driver;
import domain.Truck;
import domain.Site;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    public List<Delivery> getAllDeliveries() {
        return transportController.getAllDeliveries();
    }

    public List<Truck> getAllTrucks() {
        return transportController.getAllTrucks();
    }

    public List<Driver> getAllDrivers() {
        return transportController.getAllDrivers();
    }

    public List<Site> getAllSites() {
        return transportController.getAllSites();
    }
}