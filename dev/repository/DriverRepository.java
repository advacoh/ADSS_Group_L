package repository;

import dataAccess.transportation.DriverDTO;
import dataAccess.transportation.DriverMapper;
import domain.transportation.Driver;

import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    private final List<Driver> drivers;
    private final DriverMapper driverMapper;

    public DriverRepository() {
        this.drivers = new ArrayList<>();
        this.driverMapper = new DriverMapper();
    }

    public void addDriver(Driver driver) {
        if (getDriverById(driver.getId()) != null) {
            return;
        }

        drivers.add(driver);

        DriverDTO dto = new DriverDTO(
                driver.getId(),
                driver.getLicenseType()
        );

        driverMapper.insert(dto);
    }

    public void removeDriver(Driver driver) {
        drivers.remove(driver);
        driverMapper.delete(driver.getId());
    }

    public List<Driver> getAllDrivers() {
        return drivers;
    }

    public Driver getDriverById(int id) {
        for (Driver driver : drivers) {
            if (driver.getId() == id) {
                return driver;
            }
        }
        return null;
    }
}