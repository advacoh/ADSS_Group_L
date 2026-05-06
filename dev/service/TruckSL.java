package service;

import domain.Truck;
import enums.LicenseType;

public class TruckSL {
    private final String licenseNumber;
    private final String model;
    private final double netWeight;
    private final double maxCapacityWeight;
    private final LicenseType requiredLicenseType;

    public TruckSL(Truck truck) {
        this.licenseNumber = truck.getLicenseNumber();
        this.model = truck.getModel();
        this.netWeight = truck.getNetWeight();
        this.maxCapacityWeight = truck.getMaxCapacityWeight();
        this.requiredLicenseType = truck.getRequiredLicenseType();
    }

    public String getLicenseNumber() { return licenseNumber; }
    public String getModel() { return model; }
    public double getNetWeight() { return netWeight; }
    public double getMaxCapacityWeight() { return maxCapacityWeight; }
    public LicenseType getRequiredLicenseType() { return requiredLicenseType; }

    public String shortString() {
        return licenseNumber + " | " + model + " | Max: " + maxCapacityWeight +
                " | License: " + requiredLicenseType;
    }

    @Override
    public String toString() {
        return "Truck License Number: " + licenseNumber +
                "\nModel: " + model +
                "\nNet Weight: " + netWeight +
                "\nMax Capacity Weight: " + maxCapacityWeight +
                "\nRequired License: " + requiredLicenseType;
    }
}