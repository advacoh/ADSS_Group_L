package domain;

import enums.LicenseType;

public class Truck {

    private String licenseNumber;
    private String model;
    private double netWeight;
    private double maxCapacityWeight;
    private LicenseType requiredLicenseType;

    public Truck(String licenseNumber, String model, double netWeight, double maxCapacityWeight, LicenseType requiredLicenseType) {
        this.licenseNumber = licenseNumber;
        this.model = model;
        this.netWeight = netWeight;
        this.maxCapacityWeight = maxCapacityWeight;
        this.requiredLicenseType = requiredLicenseType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getModel() {
        return model;
    }

    public double getNetWeight() {
        return netWeight;
    }

    public double getMaxCapacityWeight() {
        return maxCapacityWeight;
    }

    public LicenseType getRequiredLicenseType() {
        return requiredLicenseType;
    }
}