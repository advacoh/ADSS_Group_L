package repository;

import domain.Driver;

import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    private List<Driver> drivers;

    public DriverRepository() {
        this.drivers = new ArrayList<>();
    }

    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

    public List<Driver> getAllDrivers() {
        return drivers;
    }
}