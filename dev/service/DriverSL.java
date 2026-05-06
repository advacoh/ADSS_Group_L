package service;

import domain.Driver;
import enums.LicenseType;

public class DriverSL {
    private final int id;
    private final String name;
    private final LicenseType licenseType;

    public DriverSL(Driver driver) {
        this.id = driver.getID(); // אם אצלכן ב-Employee זה getId ולא getID, תשני כאן
        this.name = driver.getName();
        this.licenseType = driver.getLicenseType();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public LicenseType getLicenseType() { return licenseType; }

    public String shortString() {
        return id + " | " + name + " | License: " + licenseType;
    }

    @Override
    public String toString() {
        return "Driver ID: " + id +
                "\nName: " + name +
                "\nLicense Type: " + licenseType;
    }
}